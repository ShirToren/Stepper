package FXML.execution.details;


import FXML.execution.UIAdapter;
import FXML.flow.execution.details.FlowExecutionDetailsController;
import FXML.main.MainAppController;
import FXML.step.execution.details.StepExecutionDetailsController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import impl.StepUsageDeclarationDTO;
import impl.continuations.ContinuationDTO;
import impl.continuations.ContinuationsDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.adapter.DataInFlowMapDeserializer;
import utils.adapter.FreeInputsMapDeserializer;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import static utils.Constants.REFRESH_RATE;

public class ExecutionDetailsController {
    private MainAppController mainAppController;
    private Timer timer;
    private TimerTask executionDetailsRefresher;
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
    private final UIAdapter uiAdapter = createUIAdapter();

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
        mainAppController.updateProgress(0,0);
        mainAppController.executionWantsToEnableProperty().set(false);
    }

    public void clearContinuations(){
        continuationsData.clear();
    }

    public void startExecutionDetailsRefresher(String id) {
        executionDetailsRefresher = new ExecutionDetailsRefresher(this::updateExecutionDetails, id);
        timer = new Timer();
        timer.schedule(executionDetailsRefresher, REFRESH_RATE, REFRESH_RATE);
        //////and update details again
    }
    public void closeTimer() {
        if(timer != null) {
            timer.cancel();
        }
        if(executionDetailsRefresher != null) {
            executionDetailsRefresher.cancel();
        }
    }

    private void updateExecutionDetails(FlowExecutionDTO flowExecutionDTO) {
        if (flowExecutionDTO.isFinished() || !flowExecutionDTO.isUsersLastExecution()) {
            timer.cancel();
            executionDetailsRefresher.cancel();
            if(flowExecutionDTO.isFinished()) {
                uiAdapter.updateFlowStartTime(flowExecutionDTO.getStartExecutionTime());
                uiAdapter.clearStepsItems();
                for (StepUsageDeclarationDTO step : flowExecutionDTO.getOnlyExecutedSteps()) {
                    uiAdapter.addNewStep(step.getName());
                }
                uiAdapter.updateFlowDuration(Long.toString(flowExecutionDTO.getTotalTime()));
                uiAdapter.updateFlowEndTime(flowExecutionDTO.getEndExecutionTime());
                uiAdapter.updateFlowResult(flowExecutionDTO.getExecutionResult());
                uiAdapter.clearOutputsItems();
                for (Map.Entry<DataInFlowDTO, Object> entry : flowExecutionDTO.getAllExecutionOutputs().entrySet()) {
                    uiAdapter.addNewOutput(entry);
                }
                Platform.runLater(() -> {
                    mainAppController.updateProgress(1, 1);
                    updateReRun(flowExecutionDTO);
                    addContinuations(flowExecutionDTO.getUuid().toString());
                    if(mainAppController.getAvailableFlows().contains(flowExecutionDTO.getFlowDefinitionDTO().getName())){
                        mainAppController.executionWantsToEnableProperty().set(true);
                        mainAppController.setExecutedFlowName(flowExecutionDTO.getFlowDefinitionDTO().getName());
                        mainAppController.setExecutedFlowID(flowExecutionDTO.getUuid().toString());
                    }

                });
            } else {
                Platform.runLater(() -> {
                        executedFlowAndStepsTV.getRoot().getChildren().clear();
                        executedFlowAndStepsTV.setRoot(null);
                        mainAppController.updateProgress(0, 0);
                });
                uiAdapter.clearOutputsItems();
                uiAdapter.clearInputsItems();
            }
        } else {
            if (flowExecutionDTO.getStartExecutionTime() != null) {
                uiAdapter.updateFlowStartTime(flowExecutionDTO.getStartExecutionTime());
            }
            uiAdapter.updateFlowName(flowExecutionDTO.getFlowDefinitionDTO().getName());
            uiAdapter.updateFlowID(flowExecutionDTO.getUuid().toString());
            Platform.runLater(() -> {
                mainAppController.updateProgress(flowExecutionDTO.getExecutedSteps().size(), flowExecutionDTO.getFlowDefinitionDTO().getSteps().size());
            });
            Map<DataInFlowDTO, Object> allExecutionOutputs = flowExecutionDTO.getAllExecutionOutputs();
            showFreeInputsDetails(flowExecutionDTO);
            uiAdapter.clearOutputsItems();
            for (Map.Entry<DataInFlowDTO, Object> entry : allExecutionOutputs.entrySet()) {
                if (!entry.getValue().equals("Not created due to failure in flow")) {
                    uiAdapter.addNewOutput(entry);
                }
            }
            List<StepUsageDeclarationDTO> onlyExecutedSteps = flowExecutionDTO.getOnlyExecutedSteps();
            if (onlyExecutedSteps.size() != 0) {
                uiAdapter.clearStepsItems();
            }
            for (StepUsageDeclarationDTO step : onlyExecutedSteps) {
                uiAdapter.addNewStep(step.getName());
            }
        }
    }

    private void showFreeInputsDetails(FlowExecutionDTO flowExecutionDTO) {
        uiAdapter.clearInputsItems();
        Map<String, Object> actualFreeInputs = flowExecutionDTO.getFreeInputs();
        List<DataInFlowDTO> optionalInput = new ArrayList<>();
        List<DataInFlowDTO> freeInputs = flowExecutionDTO.getFlowDefinitionDTO().getFreeInputs();
        for (DataInFlowDTO freeInput : freeInputs) {
            if (freeInput.getDataNecessity().equals("MANDATORY") &&
                    actualFreeInputs.containsKey(freeInput.getFinalName())) {
                uiAdapter.addNewInput(freeInput.getFinalName());
            } else if (actualFreeInputs.containsKey(freeInput.getFinalName())) {
                optionalInput.add(freeInput);
            }
        }
        for (DataInFlowDTO optional : optionalInput) {
            uiAdapter.addNewInput(optional.getFinalName());
        }
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        flowExecutionDetailsComponentController.setMainAppController(mainAppController);
        stepExecutionDetailsComponentController.setMainAppController(mainAppController);
    }

    private void httpCallGetExecution(String id, Consumer<FlowExecutionDTO> consumer) {
        String finalUrl = HttpUrl
                .parse(Constants.FLOW_EXECUTION)
                .newBuilder()
                .addQueryParameter(Constants.EXECUTION_ID_PARAMETER, id)
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
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<DataInFlowDTO, Object>>(){}.getType(), new DataInFlowMapDeserializer());
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(), new FreeInputsMapDeserializer());
                    Gson gson = gsonBuilder.create();

                    FlowExecutionDTO executionDTO = gson.fromJson(rawBody, FlowExecutionDTO.class);
                    Platform.runLater(() -> {
                        consumer.accept(executionDTO);
                    });
                }
            }
        });
    }

    public void addExecutedFlowAndSteps(String id) {
        httpCallGetExecution(id, (flowExecutionDTO -> {
            TreeItem<String> rootItem = new TreeItem<>(flowExecutionDTO.getFlowDefinitionDTO().getName());
            executedFlowAndStepsTV.setRoot(rootItem);
            executedFlowAndStepsTV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    httpCallGetExecution(mainAppController.getExecutedFlowID(), updatedFlowExecutionDTO -> {
                        if (newValue != null) {
                            if(updatedFlowExecutionDTO.isFlowName(newValue.getValue())){
                                executionDetailsComponent.getChildren().clear();
                                executionDetailsComponent.getChildren().add(flowExecutionDetailsComponent);
                            } else {
                                executionDetailsComponent.getChildren().clear();
                                executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);
                                for (StepUsageDeclarationDTO step : updatedFlowExecutionDTO.getFlowDefinitionDTO().getSteps()) {
                                    if (step.getName().equals(newValue.getValue())) {
                                        stepExecutionDetailsComponentController.addStepDetails(updatedFlowExecutionDTO, step);
                                    }
                                }
                            }
                        }
                    });
            });
            updateFinalDetails(id);
            addExecutedSteps(id);
        }));
    }

    public void addExecutedFlowAndStepsToHistory(String id, List<FlowExecutionDTO> finishedExecutions) {
        FlowExecutionDTO flowExecutionDTO = null;
        for (FlowExecutionDTO dto: finishedExecutions) {
            if(dto.getUuid().toString().equals(id)){
                flowExecutionDTO = dto;
            }
        }
            TreeItem<String> rootItem = new TreeItem<>(flowExecutionDTO.getFlowDefinitionDTO().getName());
            executedFlowAndStepsTV.setRoot(rootItem);
        FlowExecutionDTO finalFlowExecutionDTO = flowExecutionDTO;
        executedFlowAndStepsTV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
               // httpCallGetExecution(mainAppController.getSelectedHistoryID(), updatedFlowExecutionDTO -> {
                    if (newValue != null) {
                        if(finalFlowExecutionDTO.isFlowName(newValue.getValue())){
                            executionDetailsComponent.getChildren().clear();
                            executionDetailsComponent.getChildren().add(flowExecutionDetailsComponent);
                        } else {
                            executionDetailsComponent.getChildren().clear();
                            executionDetailsComponent.getChildren().add(stepExecutionDetailsComponent);
                            for (StepUsageDeclarationDTO step : finalFlowExecutionDTO.getFlowDefinitionDTO().getSteps()) {
                                if (step.getName().equals(newValue.getValue())) {
                                    stepExecutionDetailsComponentController.addStepDetails(finalFlowExecutionDTO, step);
                                }
                            }
                        }
                    }
               // });
            });
            updateFinalDetails(id);
            addExecutedSteps(id);

    }

    private void updateReRun(FlowExecutionDTO flowExecutionDTO) {
       mainAppController.updateExecutionReRun(flowExecutionDTO.getFlowDefinitionDTO().getName());
    }

    private void updateFlowRootItem(String flowName){
        if(executedFlowAndStepsTV.getRoot() != null){
            executedFlowAndStepsTV.getRoot().setValue(flowName);
        }
    }

    public void addExecutedSteps(String id) {
        httpCallGetExecution(id,
                (flowExecutionDTO -> {
                    for (StepUsageDeclarationDTO step: flowExecutionDTO.getExecutedSteps()) {
                        executedFlowAndStepsTV.getRoot().getChildren().add(new TreeItem<>(step.getName()));
                    }
                }));
    }
    public void addFlowExecutionDetails(String id) {
        flowExecutionDetailsComponentController.addFlowExecutionDetails(id);
        addExecutedFlowAndSteps(id);
        continueButton.setOnAction(event -> {
            mainAppController.prepareToContinuation(id, selectedContinuation);
        });
    }
    public void addFlowExecutionDetailsToHistory(String id, List<FlowExecutionDTO> finishedExecutions) {
        flowExecutionDetailsComponentController.addFlowExecutionDetails(id);
        addExecutedFlowAndStepsToHistory(id, finishedExecutions);
        continueButton.setOnAction(event -> {
            mainAppController.prepareToContinuation(id, selectedContinuation);
        });
    }

    public void updateFinalDetails(String id){
        httpCallGetExecution(id,
                (flowExecutionDTO -> {
                    if(flowExecutionDTO.getTotalTime() != 0 &&
                            flowExecutionDTO.getExecutionResult() != null &&
                            flowExecutionDTO.getEndExecutionTime() != null &&
                            flowExecutionDTO.getStartExecutionTime() != null) {
                        flowExecutionDetailsComponentController.updateDuration(Long.toString(flowExecutionDTO.getTotalTime()));
                        flowExecutionDetailsComponentController.updateResult(flowExecutionDTO.getExecutionResult());
                        flowExecutionDetailsComponentController.updateEndTime(flowExecutionDTO.getEndExecutionTime());
                        flowExecutionDetailsComponentController.updateStartTime(flowExecutionDTO.getStartExecutionTime());
                    }
                }));
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
    public void addContinuations(String id){
        Platform.runLater(continuationsData::clear);

        String finalUrl = HttpUrl
                .parse(Constants.FLOW_EXECUTION)
                .newBuilder()
                .addQueryParameter(Constants.EXECUTION_ID_PARAMETER, id)
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
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<DataInFlowDTO, Object>>(){}.getType(), new DataInFlowMapDeserializer());
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(), new FreeInputsMapDeserializer());
                    Gson gson = gsonBuilder.create();
                    FlowExecutionDTO flowExecutionDTO = gson.fromJson(rawBody, FlowExecutionDTO.class);
                        ContinuationsDTO continuations = flowExecutionDTO.getFlowDefinitionDTO().getContinuations();
                        Platform.runLater(() -> {
                            if(continuations != null){
                                for (ContinuationDTO continuation: continuations.getContinuations()) {
                                    if(mainAppController.getAvailableFlows().contains(continuation.getTargetFlow())){
                                        continuationsData.add(continuation.getTargetFlow());
                                    }else{
                                        continuationsData.remove(continuation.getTargetFlow());
                                    }
                                }
                            }
                        });
                }
            }
        });
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
                , uuid -> {
                    mainAppController.addRerunButton(uuid);
                }, () -> {
                        executedFlowAndStepsTV.getRoot().getChildren().clear();
                },
                () -> {
                    flowExecutionDetailsComponentController.clearAllOutputs();
                }, () -> {
                    flowExecutionDetailsComponentController.clearAllInputs();
        }, () -> {
                    mainAppController.clearRerunButton();
        });
    }
    public String getSelectedContinuation() {
        return selectedContinuation;
    }

}

