package FXML.execution.details;


import FXML.execution.UIAdapter;
import FXML.flow.execution.details.FlowExecutionDetailsController;
import FXML.main.MainAppController;
import FXML.step.execution.details.StepExecutionDetailsController;
import dto.FlowExecutionDTO;
import dto.StepUsageDeclarationDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ExecutionDetailsController {
    private MainAppController mainAppController;
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
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        flowExecutionDetailsComponentController.setMainAppController(mainAppController);
        stepExecutionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void addExecutedFlowAndSteps(UUID id) {
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        TreeItem<String> rootItem = new TreeItem<>(currentExecutionDTO.getFlowDefinitionDTO().getName());
        executedFlowAndStepsTV.setRoot(rootItem);

        executedFlowAndStepsTV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                executionDetailsComponent.getChildren().clear();
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
                }
/*                if (newValue.isLeaf() && oldValue == null || oldValue != null && !oldValue.isLeaf() && newValue.isLeaf()) {
                    executionDetailsComponent.getChildren().clear();
                    executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);
                } else if (oldValue != null && oldValue.isLeaf() && !newValue.isLeaf()) {
                    executionDetailsComponent.getChildren().clear();
                    executionDetailsComponent.getChildren().add(flowExecutionDetailsComponent);
                }
                if (newValue.isLeaf()) {
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
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        for (StepUsageDeclarationDTO step: currentExecutionDTO.getExecutedSteps()) {
            executedFlowAndStepsTV.getRoot().getChildren().add(new TreeItem<>(step.getName()));
        }
    }

    public void addFlowExecutionDetails(UUID id) {
        flowExecutionDetailsComponentController.addFlowExecutionDetails(id);
        addExecutedFlowAndSteps(id);
    }

    public void updateFinalDetails(UUID id){
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        if(currentExecutionDTO.getTotalTime() != null &&
                currentExecutionDTO.getExecutionResult() != null &&
                currentExecutionDTO.getEndExecutionTime() != null &&
                currentExecutionDTO.getStartExecutionTime() != null) {
            flowExecutionDetailsComponentController.updateDuration(Long.toString(currentExecutionDTO.getTotalTime().toMillis()));
            flowExecutionDetailsComponentController.updateResult(currentExecutionDTO.getExecutionResult().name());
            flowExecutionDetailsComponentController.updateEndTime(currentExecutionDTO.getEndExecutionTime().toString());
            flowExecutionDetailsComponentController.updateStartTime(currentExecutionDTO.getStartExecutionTime().toString());
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
}

