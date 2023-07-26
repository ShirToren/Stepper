package FXML.execution.details;


import FXML.execution.UIAdapter;
import FXML.flow.execution.details.FlowExecutionDetailsController;
import FXML.main.AdminMainAppController;
import FXML.step.execution.details.StepExecutionDetailsController;
import utils.Constants;
import utils.adapter.DataInFlowMapDeserializer;
import utils.adapter.FreeInputsMapDeserializer;
import utils.http.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import impl.StepUsageDeclarationDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class ExecutionDetailsController {

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

    public void setMainAppController(AdminMainAppController mainAppController) {
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
                httpCallGetExecution(id, updatedFlowExecutionDTO -> {
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

