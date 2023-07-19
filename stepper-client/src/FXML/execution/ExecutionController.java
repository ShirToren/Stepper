package FXML.execution;

import FXML.execution.details.ExecutionDetailsController;
import FXML.inputs.CollectInputsController;
import FXML.main.MainAppController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
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
            mainAppController.prepareToReExecution(id, mainAppController.getModel().getExecutionDTOByUUID(id).getFlowDefinitionDTO().getName());
        });
    }

    public void initFreeInputsComponents(String id) {
        collectInputsComponentController.initInputsComponents(id);
    }
    public void executeListener(String id) {
        executionDetailsComponentController.startExecutionDetailsRefresher(id);
        //UIAdapter uiAdapter = executionDetailsComponentController.createUIAdapter();
       // UpdateExecutionDetailsTask task = new UpdateExecutionDetailsTask(id, uiAdapter);
       // executionProgressBar.progressProperty().bind(task.progressProperty());
        //new Thread(task).start();
    }

    public void enableRerun(){
        rerunButton.setDisable(false);
    }

    public void clearAll(){
        executionDetailsComponentController.clearAll();
        collectInputsComponentController.clearAll();
        rerunButton.setDisable(true);
    }

    public void updateProgress(double x, double all){
        executionProgressBar.setProgress((100*x)/all);
    }

    public void addRerunButton(UUID id) {
        collectInputsComponentController.addRerunButton(id);
    }
    public void clearRerunButton(){
        collectInputsComponentController.clearRerunButton();
    }

    public void addContinuations(String id){
        executionDetailsComponentController.addContinuations(id);
    }
}
