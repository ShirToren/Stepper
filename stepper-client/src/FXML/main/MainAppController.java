package FXML.main;

import FXML.definition.DefinitionController;
import FXML.execution.ExecutionController;
import FXML.execution.history.ExecutionHistoryController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import stepper.management.StepperEngineManager;
import utils.*;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static utils.Constants.REFRESH_RATE;

public class MainAppController {
    private StepperEngineManager model = new StepperEngineManager();
    private Timer timer;
    private TimerTask rolesListRefresher;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label nameLabel;
    @FXML
    private Label isManagerLabel;
    @FXML
    private ListView<String> rolesLV;
    private final ObservableList<String> rolesLVItems = FXCollections.observableArrayList();
    @FXML
    private ChoiceBox<String> cssChoiceBox;
    @FXML private BorderPane flowsDefinitionComponent;
    @FXML private DefinitionController flowsDefinitionComponentController;

    @FXML private BorderPane flowsExecutionComponent;
    @FXML private ExecutionController flowsExecutionComponentController;
    @FXML private BorderPane executionHistoryComponent;
    @FXML private ExecutionHistoryController executionHistoryComponentController;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab flowExecutionTab;
    @FXML
    private Tab executionHistoryTab;

    private final SimpleBooleanProperty isFlowSelected;


    public MainAppController() {
        this.isFlowSelected = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {
        rolesLV.setItems(rolesLVItems);
        if (flowsDefinitionComponentController != null &&
                flowsExecutionComponentController != null &&
                executionHistoryComponentController != null ) {
            flowsDefinitionComponentController.setMainAppController(this);
            flowsExecutionComponentController.setMainAppController(this);
            executionHistoryComponentController.setMainAppController(this);
        }
        executionHistoryTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                executionHistoryComponentController.showOldExecutions();
            }
        });

        flowsDefinitionComponentController.getExecuteButtonDisableProperty().bind(isFlowSelected.not());
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                if(oldTab.equals(flowExecutionTab) || newTab.equals((flowExecutionTab))) {
                    flowsExecutionComponentController.clearAll();
                }
                if(oldTab.equals(executionHistoryTab)) {
                    executionHistoryComponentController.clearAll();
                }
            }
        });

        borderPane.getStylesheets().add("/FXML/css/default.css");
        // Add items to the ChoiceBox
        cssChoiceBox.getItems().addAll("Default skin", "Skin 2", "Skin 3");

        // Set a default selection
        //cssChoiceBox.setValue("Skin 1");

        // Add a listener to handle selection changes
        cssChoiceBox.setOnAction(event -> {
            String selectedValue = cssChoiceBox.getValue();
            changeCss(selectedValue);
        });
        //startRolesRefresher();
    }

    public void updateProgress(double x, double all) {
        flowsExecutionComponentController.updateProgress(x, all);
    }

    public List<String> getRoles(){
        return rolesLVItems;
    }

    public void setActive() {
        startRolesRefresher();
    }

    public void startRolesRefresher() {
        rolesListRefresher = new RolesListRefresher(this::updateRolesList, nameLabel.getText());
        timer = new Timer();
        timer.schedule(rolesListRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateRolesList(List<String> roles) {
        Platform.runLater(() -> {
            rolesLVItems.clear();
            rolesLVItems.addAll(roles);
        });
        flowsDefinitionComponentController.startDefinitionRefresher();
    }

    public void updateUserName(String userName) {
        nameLabel.setText(userName);
        isManagerLabel.setText("No");
    }

    public void addRerunButton(UUID id){
        flowsExecutionComponentController.addRerunButton(id);
    }
    public void clearRerunButton(){
        flowsExecutionComponentController.clearRerunButton();
    }

    private void changeCss(String option){
        borderPane.getStylesheets().clear();
        if(option.endsWith("2")) {
            borderPane.getStylesheets().add("/FXML/css/second.css");
        } else if (option.endsWith("3")) {
            borderPane.getStylesheets().add("/FXML/css/third.css");
        } else if(option.equals("Default skin")){
            borderPane.getStylesheets().add("/FXML/css/default.css");
        }
    }

    private void clearAll(){
        flowsDefinitionComponentController.clearAll();
        isFlowSelected.set(false);
        flowsExecutionComponentController.clearAll();
        executionHistoryComponentController.clearAll();
    }

    public void setSelectedFlow() {
        isFlowSelected.set(true);
    }

    public void switchToExecutionTab() {
        tabPane.getSelectionModel().select(flowExecutionTab);
    }

    public void addExecutionToTable() {
        executionHistoryComponentController.addExecutionToTable();
    }

    private void httpCallCreateExecution(String flowName, Consumer<Response> consumer) {
        String finalUrl = HttpUrl
                .parse(Constants.FLOW_EXECUTION)
                .newBuilder()
                .build()
                .toString();
        RequestBody requestBody = new FormBody.Builder()
                .add("flowName", flowsDefinitionComponentController.getSelectedFlowName())
                .build();


        HttpClientUtil.runPostAsync(finalUrl, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure...:(");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                consumer.accept(response);
/*                String id = response.body().string();
                if (response.isSuccessful()) {
                    Platform.runLater(runnable);
                    // httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure. Error code: " + response.code());
                }*/
            }
        });
    }

    public void executeFlowButtonActionListener() {
        httpCallCreateExecution(flowsDefinitionComponentController.getSelectedFlowName(),
                (response -> {
                    String id = null;
                    try {
                        id = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            clearFlowExecutionDetails();
                            switchToExecutionTab();
                        });
                        flowsExecutionComponentController.initFreeInputsComponents(id);
                        // httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure. Error code: " + response.code());
                    }
                }));


/*        String finalUrl = HttpUrl
                .parse(Constants.FLOW_EXECUTION)
                .newBuilder()
                //.addQueryParameter("flowname", flowsDefinitionComponentController.getSelectedFlowName())
                .build()
                .toString();
        RequestBody requestBody = new FormBody.Builder()
                .add("flowName", flowsDefinitionComponentController.getSelectedFlowName())
                .build();


        HttpClientUtil.runPostAsync(finalUrl, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure...:(");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String id = response.body().string();
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        clearFlowExecutionDetails();
                        switchToExecutionTab();
                    });
                    flowsExecutionComponentController.initFreeInputsComponents(id);
                   // httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure. Error code: " + response.code());
                }
            }
        });*/

/*        clearFlowExecutionDetails();
        switchToExecutionTab();
        prepareToExecution(flowsDefinitionComponentController.getSelectedFlowName());*/
    }

    public void prepareToExecution(String flowName){
        clearFlowExecutionDetails();
        switchToExecutionTab();
        UUID id = model.createFlowExecution(flowName);
        flowsExecutionComponentController.initFreeInputsComponents(id.toString());
    }

    public void prepareToReExecution(String prevID, String flowName){
        clearFlowExecutionDetails();
        switchToExecutionTab();

        httpCallCreateExecution(flowName, (response -> {
            String id = null;
            try {
                id = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()) {
                String finalUrl = HttpUrl
                        .parse(Constants.COPY_FREE_INPUTS_VALUES)
                        .newBuilder()
                        .addQueryParameter("sourceID", prevID)
                        .addQueryParameter("targetID", id)
                        .build()
                        .toString();


                String finalId = id;
                HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String rawBody = response.body().string();
                        if (response.isSuccessful()) {
                            httpCallCopyFreeInputsValues(prevID.toString(), finalId);
                        }
                    }
                });
            }
        }));

        //UUID id = model.createFlowExecution(flowName);
        //model.copyFreeInputsValues(prevID, id);
        //flowsExecutionComponentController.initFreeInputsComponents(id.toString());
    }

    private void httpCallCopyFreeInputsValues(String sourceID, String targetID){
        String finalUrl = HttpUrl
                .parse(Constants.COPY_FREE_INPUTS_VALUES)
                .newBuilder()
                .addQueryParameter("sourceID", sourceID)
                .addQueryParameter("targetID", targetID)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                if (response.isSuccessful()) {
                    flowsExecutionComponentController.initFreeInputsComponents(targetID);
                }
            }
        });
    }

    public void prepareToContinuation(String prevID, String targetFlowName){
        clearFlowExecutionDetails();
        switchToExecutionTab();
        httpCallCreateExecution(targetFlowName, (response -> {
            String id = null;
            try {
                id = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()) {
                String finalUrl = HttpUrl
                        .parse(Constants.COPY_CONTINUATION_VALUES)
                        .newBuilder()
                        .addQueryParameter("sourceID", prevID)
                        .addQueryParameter("targetID", id)
                        .build()
                        .toString();


                String finalId = id;
                HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String rawBody = response.body().string();
                        if (response.isSuccessful()) {
                            flowsExecutionComponentController.initFreeInputsComponents(finalId);
                        }
                    }
                });
                //model.copyContinuationValues(prevID.toString(), id);
            }
        }));
    }

    public void clearFlowExecutionDetails(){
        flowsExecutionComponentController.clearFlowExecutionDetails();
    }

    public void showFlowExecutionDetails(String id){
        flowsExecutionComponentController.addFlowExecutionDetails(id);
    }

    public void executeListener(String id) {
        flowsExecutionComponentController.executeListener(id);
    }



    public StepperEngineManager getModel() {
        return model;
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

