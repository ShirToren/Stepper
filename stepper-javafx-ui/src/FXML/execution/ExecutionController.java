package FXML.execution;

import FXML.flow.execution.details.FlowExecutionDetailsController;
import FXML.inputs.CollectInputsController;
import FXML.main.MainAppController;
import FXML.step.execution.details.StepExecutionDetailsController;
import dto.FlowExecutionDTO;
import dto.StepUsageDeclarationDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import tasks.UpdateExecutionDetailsTask;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class ExecutionController {
    private MainAppController mainAppController;
    @FXML
    private TreeView<String> executedFlowAndStepsTV;
    @FXML
    private AnchorPane executionDetailsComponent;
    @FXML private ProgressBar executionProgressBar;
    @FXML
    private ScrollPane collectInputsComponent;

    @FXML
    private CollectInputsController collectInputsComponentController;
    @FXML
    private GridPane flowExecutionDetailsComponent;
    @FXML
    private FlowExecutionDetailsController flowExecutionDetailsComponentController;
    private StepExecutionDetailsController stepExecutionDetailsComponentController;

    private Node stepExecutionDetailsComponent;
    private final Map<String, StepUsageDeclarationDTO> stepsTreeItems;

    public ExecutionController() {
        this.stepsTreeItems = new HashMap<>();
    }

    @FXML
    public void initialize() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/FXML/step/execution/details/stepExecutionDetails.fxml");
            fxmlLoader.setLocation(url);
            stepExecutionDetailsComponent = fxmlLoader.load(url.openStream());
            stepExecutionDetailsComponentController = fxmlLoader.getController();

            // Set the included content as the content of the AnchorPane
            //executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearFlowExecutionDetails() {
        flowExecutionDetailsComponentController.clearAll();
        if(executedFlowAndStepsTV.getRoot() != null) {
            executedFlowAndStepsTV.getRoot().getChildren().clear();
            executedFlowAndStepsTV.setRoot(new TreeItem<>());
        }
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        collectInputsComponentController.setMainAppController(mainAppController, this);
        flowExecutionDetailsComponentController.setMainAppController(mainAppController);
        stepExecutionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void addExecutedFlowAndSteps(UUID id) {
        int index = 1;
        List<TreeItem<String>> stepsList = new ArrayList<>();
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        TreeItem<String> rootItem = new TreeItem<>(currentExecutionDTO.getFlowDefinitionDTO().getName());
        executedFlowAndStepsTV.setRoot(rootItem);
/*        for (StepUsageDeclarationDTO step: mainAppController.getModel().getOnlyExecutedSteps(currentExecutionDTO)) {
            TreeItem<String> child = new TreeItem<>("Step " + index + ": " + step.getName());
            stepsTreeItems.put(child.getValue(), step);
            stepsList.add(child);
            index++;
        }
        rootItem.getChildren().addAll(stepsList);
        rootItem.setExpanded(true);*/

        executedFlowAndStepsTV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(newValue.isLeaf() && oldValue == null || oldValue != null && !oldValue.isLeaf() && newValue.isLeaf()) {
                    executionDetailsComponent.getChildren().clear();
                    executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);
                } else if(oldValue!= null && oldValue.isLeaf() && !newValue.isLeaf()) {
                    executionDetailsComponent.getChildren().clear();
                    executionDetailsComponent.getChildren().add(flowExecutionDetailsComponent);
                }
                if(newValue.isLeaf()) {
                    StepUsageDeclarationDTO stepToShow = stepsTreeItems.get(newValue.getValue());
                    stepExecutionDetailsComponentController.addStepDetails(stepToShow);
                }
            }
        });
    }

    public void initFreeInputsComponents(UUID id) {
        collectInputsComponentController.initInputsComponents(id);
    }
    public void addFlowExecutionDetails(UUID id) {
        flowExecutionDetailsComponentController.addFlowExecutionDetails(id);
        addExecutedFlowAndSteps(id);
    }

    public void executeListener(UUID id) {
        UIAdapter uiAdapter = createUIAdapter();
        UpdateExecutionDetailsTask task = new UpdateExecutionDetailsTask(id,mainAppController.getModel(),
                uiAdapter);
        executionProgressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
    }

    private UIAdapter createUIAdapter() {
        return new UIAdapter(
                endTime -> {
                    flowExecutionDetailsComponentController.updateEndTime(endTime);
                },
                duration -> {
                    flowExecutionDetailsComponentController.updateDuration(duration);
                },
                result -> {
                    flowExecutionDetailsComponentController.updateResult(result);
                },
                output -> {
                        flowExecutionDetailsComponentController.addOutput(output);
                },
                stepName -> {
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
                }
                );
    }
}
