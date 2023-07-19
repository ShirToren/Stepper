package FXML.execution.details;


import FXML.execution.UIAdapter;
import FXML.flow.execution.details.FlowExecutionDetailsController;
import FXML.main.AdminMainAppController;
import FXML.step.execution.details.StepExecutionDetailsController;
import impl.FlowExecutionDTO;
import impl.StepUsageDeclarationDTO;
import flow.definition.api.continuations.Continuation;
import flow.definition.api.continuations.Continuations;
import impl.continuations.ContinuationDTO;
import impl.continuations.ContinuationsDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ExecutionDetailsController {
    private AdminMainAppController mainAppController;
    @FXML
    private Button continueButton;
    @FXML
    private ListView<String> continuationsLV;
    private final ObservableList<String> continuationsData = FXCollections.observableArrayList();
    private String selectedContinuation;
    @FXML
    private TreeView<String> executedFlowAndStepsTV;
    @FXML
    private AnchorPane executionDetailsComponent;
    @FXML
    private GridPane flowExecutionDetailsComponent;
    @FXML
    private FlowExecutionDetailsController flowExecutionDetailsComponentController;
    private StepExecutionDetailsController stepExecutionDetailsComponentController;
    private Node stepExecutionDetailsComponent;

    @FXML
    public void initialize() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/FXML/step/execution/details/stepExecutionDetails.fxml");
            fxmlLoader.setLocation(url);
            stepExecutionDetailsComponent = fxmlLoader.load(url.openStream());
            stepExecutionDetailsComponentController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        continuationsLV.setItems(continuationsData);
        continueButton.disableProperty().bind(continuationsLV.getSelectionModel().selectedItemProperty().isNull());
    }

    private void clearFlowExecutionDetails() {
        flowExecutionDetailsComponentController.clearAll();
        if(executedFlowAndStepsTV.getRoot() != null) {
            executedFlowAndStepsTV.getRoot().getChildren().clear();
            executedFlowAndStepsTV.setRoot(new TreeItem<>());
        }
    }

    private void clearStepExecutionDetails(){
        stepExecutionDetailsComponentController.clearAll();
    }


    public void clearAll(){
        clearFlowExecutionDetails();
        clearStepExecutionDetails();
        executionDetailsComponent.getChildren().clear();
        executionDetailsComponent.getChildren().add(flowExecutionDetailsComponent);
        continuationsData.clear();
    }

    public void setMainAppController(AdminMainAppController mainAppController) {
        this.mainAppController = mainAppController;
        flowExecutionDetailsComponentController.setMainAppController(mainAppController);
        stepExecutionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void addExecutedFlowAndSteps(UUID id) {
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id.toString());
        TreeItem<String> rootItem = new TreeItem<>(currentExecutionDTO.getFlowDefinitionDTO().getName());
        executedFlowAndStepsTV.setRoot(rootItem);

        executedFlowAndStepsTV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(currentExecutionDTO.isFlowName(newValue.getValue())){
                    executionDetailsComponent.getChildren().clear();
                    executionDetailsComponent.getChildren().add(flowExecutionDetailsComponent);
                } else {
                    executionDetailsComponent.getChildren().clear();
                    executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);
                    for (StepUsageDeclarationDTO step : currentExecutionDTO.getFlowDefinitionDTO().getSteps()) {
                        if (step.getName().equals(newValue.getValue())) {
                            stepExecutionDetailsComponentController.addStepDetails(id, step);
                        }
                    }
                }
/*                executionDetailsComponent.getChildren().clear();
                if(newValue.isLeaf()) {
                    executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);
                    for (StepUsageDeclarationDTO step : currentExecutionDTO.getFlowDefinitionDTO().getSteps()) {
                        if (step.getName().equals(newValue.getValue())) {
                            stepExecutionDetailsComponentController.addStepDetails(id, step);
                        }
                    }
                } else if(oldValue != null && oldValue.isLeaf() && !newValue.isLeaf()) {
                    executionDetailsComponent.getChildren().add(flowExecutionDetailsComponent);
                } else if(oldValue != null && oldValue.isLeaf() && newValue.isLeaf()) {
                    executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);
                    for (StepUsageDeclarationDTO step : currentExecutionDTO.getFlowDefinitionDTO().getSteps()) {
                        if (step.getName().equals(newValue.getValue())) {
                            stepExecutionDetailsComponentController.addStepDetails(id, step);
                        }
                    }
                }*/
            }
        });


    }


    private void updateFlowRootItem(String flowName){
        if(executedFlowAndStepsTV.getRoot() != null){
            executedFlowAndStepsTV.getRoot().setValue(flowName);
        }
    }

    public void addExecutedSteps(UUID id) {
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id.toString());
        for (StepUsageDeclarationDTO step: currentExecutionDTO.getExecutedSteps()) {
            executedFlowAndStepsTV.getRoot().getChildren().add(new TreeItem<>(step.getName()));
        }
    }

    public void addFlowExecutionDetails(UUID id) {
        flowExecutionDetailsComponentController.addFlowExecutionDetails(id);
        addExecutedFlowAndSteps(id);
       /* continueButton.setOnAction(event -> {
            mainAppController.prepareToContinuation(id, selectedContinuation);
        });*/
    }

    public void updateFinalDetails(UUID id){
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id.toString());
        if(currentExecutionDTO.getTotalTime() != 0 &&
                currentExecutionDTO.getExecutionResult() != null &&
                currentExecutionDTO.getEndExecutionTime() != null &&
                currentExecutionDTO.getStartExecutionTime() != null) {
            flowExecutionDetailsComponentController.updateDuration(Long.toString(currentExecutionDTO.getTotalTime()));
            flowExecutionDetailsComponentController.updateResult(currentExecutionDTO.getExecutionResult());
            flowExecutionDetailsComponentController.updateEndTime(currentExecutionDTO.getEndExecutionTime());
            flowExecutionDetailsComponentController.updateStartTime(currentExecutionDTO.getStartExecutionTime());
        }
    }

    @FXML
    void rowClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {
            int selectedIndex = continuationsLV.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < continuationsData.size()) {
                selectedContinuation = continuationsLV.getSelectionModel().getSelectedItem();
            }
        }
    }
    public void addContinuations(UUID id){
        continuationsData.clear();
        FlowExecutionDTO executionDTOByUUID = mainAppController.getModel().getExecutionDTOByUUID(id.toString());
        ContinuationsDTO continuations = executionDTOByUUID.getFlowDefinitionDTO().getContinuations();
        if(continuations != null){
            for (ContinuationDTO continuation: continuations.getContinuations()) {
                continuationsData.add(continuation.getTargetFlow());
            }
        }
    }

    public UIAdapter createUIAdapter() {
        return new UIAdapter(
                name -> {
                    flowExecutionDetailsComponentController.updateName(name);
                    updateFlowRootItem(name);

                }, id -> {
                    flowExecutionDetailsComponentController.updateID(id);
                }, endTime -> {
                    flowExecutionDetailsComponentController.updateEndTime(endTime);
                },
                duration -> {
                    flowExecutionDetailsComponentController.updateDuration(duration);
                },
                result -> {
                    flowExecutionDetailsComponentController.updateResult(result);
                },
                entry -> {
                    flowExecutionDetailsComponentController.addOutput(entry);
                },
                input -> {
                    flowExecutionDetailsComponentController.addInput(input);
                }, stepName -> {
                    executedFlowAndStepsTV.getRoot().getChildren().add(new TreeItem<>(stepName));
                },
                startTime -> {
                    flowExecutionDetailsComponentController.updateStartTime(startTime);
                }
                , () -> {
                    executedFlowAndStepsTV.getRoot().getChildren().clear();
                },
                () -> {
                    flowExecutionDetailsComponentController.clearAllOutputs();
                }, () -> {
                    flowExecutionDetailsComponentController.clearAllInputs();
        });
    }
    public String getSelectedContinuation() {
        return selectedContinuation;
    }

}

