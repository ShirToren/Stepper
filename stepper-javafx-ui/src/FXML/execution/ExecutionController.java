package FXML.execution;

import FXML.execution.details.ExecutionDetailsController;
import FXML.inputs.CollectInputsController;
import FXML.main.MainAppController;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import tasks.UpdateExecutionDetailsTask;
import java.util.*;


public class ExecutionController {
    private MainAppController mainAppController;

    @FXML
    private BorderPane executionDetailsComponent;
    @FXML
    private ExecutionDetailsController executionDetailsComponentController;
    @FXML private ProgressBar executionProgressBar;
    @FXML
    private ScrollPane collectInputsComponent;
    @FXML
    private CollectInputsController collectInputsComponentController;


    public void clearFlowExecutionDetails() {
        executionDetailsComponentController.clearAll();
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        collectInputsComponentController.setMainAppController(mainAppController, this);
        executionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void addFlowExecutionDetails(UUID id) {
        executionDetailsComponentController.addFlowExecutionDetails(id);
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
}
