package FXML.execution;

import FXML.execution.details.ExecutionDetailsController;
import FXML.inputs.CollectInputsController;
import FXML.main.MainAppController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import tasks.UpdateExecutionDetailsTask;
import java.util.*;


public class ExecutionController {
    private MainAppController mainAppController;

    @FXML
    private Button rerunButton;

    @FXML
    private ScrollPane executionDetailsComponent;
    @FXML
    private ExecutionDetailsController executionDetailsComponentController;
    @FXML private ProgressBar executionProgressBar;
    @FXML
    private ScrollPane collectInputsComponent;
    @FXML
    private CollectInputsController collectInputsComponentController;

    @FXML
    public void initialize() {
        rerunButton.setDisable(true);
    }

    public void clearFlowExecutionDetails() {
        executionDetailsComponentController.clearAll();
        rerunButton.setDisable(true);
    }
    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        collectInputsComponentController.setMainAppController(mainAppController, this);
        executionDetailsComponentController.setMainAppController(mainAppController);

    }
    public void addFlowExecutionDetails(UUID id) {
        executionDetailsComponentController.addFlowExecutionDetails(id);
        rerunButton.setOnAction(event -> {
            mainAppController.prepareToReExecution(id, mainAppController.getModel().getExecutionDTOByUUID(id).getFlowDefinitionDTO().getName());
        });
    }

    public void initFreeInputsComponents(UUID id) {
        collectInputsComponentController.initInputsComponents(id);
    }
    public void executeListener(UUID id) {
        UIAdapter uiAdapter = executionDetailsComponentController.createUIAdapter();
        UpdateExecutionDetailsTask task = new UpdateExecutionDetailsTask(id,mainAppController.getModel(),
                uiAdapter);
        executionProgressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
    }

    public void enableRerun(){
        rerunButton.setDisable(false);
    }

    public void clearAll(){
        executionDetailsComponentController.clearAll();
        collectInputsComponentController.clearAll();
        rerunButton.setDisable(true);
    }

    public void addRerunButton(UUID id) {
        collectInputsComponentController.addRerunButton(id);
    }
    public void clearRerunButton(){
        collectInputsComponentController.clearRerunButton();
    }

    public void addContinuations(UUID id){
        executionDetailsComponentController.addContinuations(id);
    }
}
