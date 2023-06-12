package FXML.execution;

import FXML.continuations.list.ContinuationsListController;
import FXML.execution.details.ExecutionDetailsController;
import FXML.inputs.CollectInputsController;
import FXML.main.MainAppController;
import flow.definition.api.continuations.Continuations;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import tasks.UpdateExecutionDetailsTask;
import java.util.*;


public class ExecutionController {
    private MainAppController mainAppController;
    @FXML
    private Button continueButton;

    @FXML
    private BorderPane executionDetailsComponent;
    @FXML
    private ExecutionDetailsController executionDetailsComponentController;
    @FXML private ProgressBar executionProgressBar;
    @FXML
    private ScrollPane collectInputsComponent;
    @FXML
    private CollectInputsController collectInputsComponentController;
    @FXML
    private GridPane continuationsComponent;
    @FXML
    private ContinuationsListController continuationsComponentController;

    public void clearFlowExecutionDetails() {
        executionDetailsComponentController.clearAll();
    }
    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        collectInputsComponentController.setMainAppController(mainAppController, this);
        executionDetailsComponentController.setMainAppController(mainAppController);
        continuationsComponentController.setMainAppController(mainAppController);
    }
    public void addFlowExecutionDetails(UUID id) {
        executionDetailsComponentController.addFlowExecutionDetails(id);
        continuationsComponentController.addContinuations(id);

        continueButton.setOnAction(event -> {
            String selectedContinuation = continuationsComponentController.getSelectedContinuation();
            mainAppController.prepareToContinuation(id, selectedContinuation);
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
    public void clearAll(){
        executionDetailsComponentController.clearAll();
        collectInputsComponentController.clearAll();
        //executionProgressBar.progressProperty().set(0.0);
    }
/*    @FXML
    void continueButtonActionListener(ActionEvent event) {
        String selectedContinuation = continuationsComponentController.getSelectedContinuation();
        mainAppController.prepareToExecution(selectedContinuation);
        //clearAll();
    }*/

}
