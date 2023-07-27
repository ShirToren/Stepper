package FXML.execution;

import FXML.execution.details.ExecutionDetailsController;
import FXML.inputs.CollectInputsController;
import FXML.main.MainAppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;

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
    public void addFlowExecutionDetails(String id) {
        executionDetailsComponentController.addFlowExecutionDetails(id);
        rerunButton.setOnAction(event -> {
            mainAppController.prepareToReExecution(id);
        });
    }

    public void initFreeInputsComponents(String id) {
        collectInputsComponentController.initInputsComponents(id);
    }
    public void executeListener(String id) {
        executionDetailsComponentController.startExecutionDetailsRefresher(id);
    }

    public void enableRerun(){
        rerunButton.setDisable(false);
    }
    public void disAbleRerun(){
        rerunButton.setDisable(true);
    }

    public void clearAll(){
        executionDetailsComponentController.clearAll();
        collectInputsComponentController.clearAll();
        rerunButton.setDisable(true);
    }

    public void updateProgress(double x, double all){
        executionProgressBar.setProgress(x/all);
    }

    public void addRerunButton(UUID id) {
        collectInputsComponentController.addRerunButton(id);
    }
    public void clearRerunButton(){
        collectInputsComponentController.clearRerunButton();
    }

    public void closeTimer(){
        executionDetailsComponentController.closeTimer();
    }

    public void addContinuations(String id) {
        executionDetailsComponentController.addContinuations(id);
    }
    public void clearContinuations() {
        executionDetailsComponentController.clearContinuations();
    }
}
