package FXML.inputs;

import FXML.execution.ExecutionController;
import FXML.main.MainAppController;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import step.api.DataNecessity;
import utils.Constants;
import utils.adapter.MapDeserializer;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;

import static utils.Constants.GSON_INSTANCE;


public class CollectInputsController {

    private MainAppController mainAppController;
    private ExecutionController executionController;
    @FXML
    private GridPane collectInputsGP;
    private final List<TextField> mandatoryTextFields;
    private Button executeButton;
    private Button rerunButton;
    private final SimpleBooleanProperty isMandatoryField;
    
    private final Map<DataInFlowDTO, TextField> freeInputsTextFieldComponents;
    private final Map<DataInFlowDTO, Spinner<Integer>> freeInputsSpinnerComponents;
    private int waitForInputs = 0;
    private final static Object counterLock = new Object();;

    public CollectInputsController() {
        this.mandatoryTextFields = new ArrayList<>();
        this.freeInputsTextFieldComponents = new HashMap<>();
        this.freeInputsSpinnerComponents = new HashMap<>();
        this.isMandatoryField = new SimpleBooleanProperty(false);
    }

    public void clearAll(){
        freeInputsTextFieldComponents.clear();
        freeInputsSpinnerComponents.clear();
        mandatoryTextFields.clear();
        collectInputsGP.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= 2);
    }

    public void clearRerunButton(){
        collectInputsGP.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= 2);
    }


    public void initInputsComponents(String id) {

        String finalUrl = HttpUrl
                .parse(Constants.FLOW_EXECUTION)
                .newBuilder()
                .addQueryParameter(Constants.EXECUTION_ID_PARAMETER, id)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure...:(");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                if (response.isSuccessful()) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<DataInFlowDTO, Object>>(){}.getType(), new MapDeserializer());
                    Gson gson = gsonBuilder.create();

                    FlowExecutionDTO executionDTO = gson.fromJson(rawBody, FlowExecutionDTO.class);
                    Platform.runLater(() -> {
                        initComponents(executionDTO);
                    });
                }
            }
        });
    }

    private void initComponents(FlowExecutionDTO executionDTO) {
        int mandatoryRowIndex = 2, optionalRowIndex = 2;
        int mandatoryColIndex = 1, optionalColIndex = 5;
        clearAll();
        List<DataInFlowDTO> currentFreeInputs = executionDTO.getFlowDefinitionDTO().getFreeInputs();
        for (DataInFlowDTO input: currentFreeInputs) {
            //addRowConstraints();
            if(input.getDataNecessity().equals(DataNecessity.MANDATORY.name())) {
                if(!input.getFinalName().equals(input.getOriginalName())){
                    addLabel(input.getUserString() + " (" + input.getFinalName() +"):", mandatoryRowIndex, mandatoryColIndex);
                } else {
                    addLabel(input.getUserString() + ":", mandatoryRowIndex, mandatoryColIndex);
                }
                if(input.getDataDefinition().getType().equals(Integer.class.getName())){
                    Spinner<Integer> integerSpinner = addSpinner(mandatoryRowIndex, mandatoryColIndex + 1);
                    if(executionDTO.getFreeInputs().containsKey(input.getFinalName())){
                        integerSpinner.getValueFactory().setValue((Integer) executionDTO.getFreeInputs().get(input.getFinalName()));
                    }
                    freeInputsSpinnerComponents.put(input, integerSpinner);
                } else {
                    TextField textField = addMandatoryTextField(mandatoryRowIndex, mandatoryColIndex + 1);
                    if(executionDTO.getFreeInputs().containsKey(input.getFinalName())){
                        textField.setText(executionDTO.getFreeInputs().get(input.getFinalName()).toString());
                    }
                    freeInputsTextFieldComponents.put(input, textField);
                }
                mandatoryRowIndex++;
            } else {
                addLabel(input.getUserString() + ":", optionalRowIndex, optionalColIndex);
                if(input.getDataDefinition().getType().equals(Integer.class.getName())){
                    Spinner<Integer> integerSpinner = addSpinner(optionalRowIndex, optionalColIndex + 1);
                    if(executionDTO.getFreeInputs().containsKey(input.getFinalName())){
                        integerSpinner.getValueFactory().setValue((Integer) executionDTO.getFreeInputs().get(input.getFinalName()));
                    }
                    freeInputsSpinnerComponents.put(input, integerSpinner);
                } else {
                    TextField textField = addTextField(optionalRowIndex, optionalColIndex + 1);
                    if(executionDTO.getFreeInputs().containsKey(input.getFinalName())){
                        textField.setText(executionDTO.getFreeInputs().get(input.getFinalName()).toString());
                    }
                    freeInputsTextFieldComponents.put(input, textField);
                }
                optionalRowIndex++;
            }
        }

        for (TextField textField: mandatoryTextFields) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                isMandatoryField.set(isAllMandatoryField());
            });
        }

        initExecuteButton(executionDTO.getUuid(), Math.max(optionalRowIndex, mandatoryRowIndex));

    }


    private boolean isAllMandatoryField(){
        for (TextField textField: mandatoryTextFields) {
            if (textField.getText().isEmpty()){
                return false;
            }
        }
        return true;
    }

    private void initExecuteButton(UUID id, int rowIndex) {
        executeButton = new Button();
        executeButton.setText("Start!");

        executeButton.disableProperty().bind(isMandatoryField.not());
        collectInputsGP.add(executeButton, 7 ,rowIndex);
        executeButton.setOnAction(event -> {
            boolean isValid = executeButtonActionListener(id);
            if(isValid){


/*                if(isValid){
                    mainAppController.executeListener(id);
                    clearAll();
                    Runnable task = () -> {
                        mainAppController.getModel().executeFlow(id.toString());
                        Platform.runLater(() -> {
                            mainAppController.addExecutionToTable();
                            executionController.addContinuations(id);
                            mainAppController.addStatistics();
                            executionController.enableRerun();
                        });
                    };
                    mainAppController.getModel().getExecutor().execute(task);
                }*/
/*
                mainAppController.showFlowExecutionDetails(id);
                clearAll();
                httpCallToExecuteFlow(id);
                mainAppController.executeListener(id);*/


/*                Runnable task = () -> {
                    mainAppController.getModel().executeFlow(id);
                    Platform.runLater(() -> {
                        mainAppController.addExecutionToTable();
                        executionController.addContinuations(id);
                        executionController.enableRerun();
                    });
                };
                mainAppController.getModel().getExecutor().execute(task);*/
            }
        });
    }

    public void addRerunButton(UUID id){
        rerunButton = new Button("Rerun flow");
        Button button = addButton("Rerun flow", 2, 1);
        button.setOnAction(event1 -> {
            mainAppController.prepareToReExecution(id.toString(), mainAppController.getModel().getExecutionDTOByUUID(id.toString()).getFlowDefinitionDTO().getName());
        });
    }

    private void httpCallToExecuteFlow(String id) {
        String finalUrl = HttpUrl
                .parse(Constants.EXECUTE_FLOW)
                .newBuilder()
                .addQueryParameter("id", id)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                System.out.println("hey");
                if(response.isSuccessful()){
                    Platform.runLater(() -> {
                        mainAppController.addExecutionToTable();
                        executionController.addContinuations(id);
                        executionController.enableRerun();
                    });
                }
            }
        });
    }
    private void httpCallToAddFreeInputs(List<FreeInput> freeInputs) {

        // Serialize the list to JSON
        String jsonBody = GSON_INSTANCE.toJson(freeInputs);

        // Set the JSON as the request body
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        String finalUrl = HttpUrl
                .parse(Constants.ADD_INPUTS)
                .newBuilder()
                .build()
                .toString();


        HttpClientUtil.runPostAsync(finalUrl, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Process the response as needed
                String responseBody = response.body().string();
                if(response.isSuccessful()) {
                    String id = GSON_INSTANCE.fromJson(responseBody, String.class);
                    Platform.runLater(() -> {
                        mainAppController.showFlowExecutionDetails(id);
                        mainAppController.executeListener(id);
                        clearAll();
                        httpCallToExecuteFlow(id);
                    });
                }
        }
        });
    }

    private boolean executeButtonActionListener(UUID id){
        List<FreeInput> freeInputs = new ArrayList<>();
        for (Map.Entry<DataInFlowDTO, Spinner<Integer>> entry: freeInputsSpinnerComponents.entrySet()) {
            freeInputs.add(new FreeInput(id.toString(), entry.getKey().getFinalName(), entry.getValue().getValue(), entry.getKey().getDataDefinition().getType()));
        }
        for (Map.Entry<DataInFlowDTO, TextField> entry: freeInputsTextFieldComponents.entrySet()) {
            if(!entry.getValue().getText().isEmpty()) {
                if(entry.getKey().getDataDefinition().getType().equals(Double.class.getName())) {
                    try {
                        double value = Double.parseDouble(entry.getValue().getText());
                       freeInputs.add(new FreeInput(id.toString(), entry.getKey().getFinalName(), value, entry.getKey().getDataDefinition().getType()));
                    } catch (NumberFormatException e) {
                        // Display an alert or handle the invalid input
                        showErrorDialog("Invalid Input", "Please enter a valid double value.");
                        entry.getValue().clear();
                        return false;
                    }
                } else {
                    freeInputs.add(new FreeInput(id.toString(), entry.getKey().getFinalName(), entry.getValue().getText(), entry.getKey().getDataDefinition().getType()));
                }
            }
        }
        httpCallToAddFreeInputs(freeInputs);
        return true;
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void setMainAppController(MainAppController mainAppController, ExecutionController executionController) {
        this.mainAppController = mainAppController;
        this.executionController = executionController;
    }

    private void addLabel(String text, int rowIndex, int colIndex) {
        Label label = new Label(text);
        collectInputsGP.add(label, colIndex,rowIndex);
    }
    private Button addButton(String text, int rowIndex, int colIndex) {
        Button button = new Button(text);
        collectInputsGP.add(button, colIndex,rowIndex);
        return button;
    }

    private TextField addTextField(int rowIndex, int colIndex) {
        TextField textField = new TextField();
        collectInputsGP.add(textField, colIndex, rowIndex);
        GridPane.setColumnSpan(textField, 2);
        return textField;
    }

    private TextField addMandatoryTextField(int rowIndex, int colIndex) {
        TextField textField = new TextField();
        collectInputsGP.add(textField, colIndex, rowIndex);
        GridPane.setColumnSpan(textField, 2);
        mandatoryTextFields.add(textField);
        return  textField;
    }

    private Spinner<Integer> addSpinner(int rowIndex, int colIndex) {
        Spinner<Integer> spinner = new Spinner<>();

        // Create a value factory for integer values
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);

        // Set the value factory for the spinner
        spinner.setValueFactory(valueFactory);
        collectInputsGP.add(spinner, colIndex, rowIndex);
        return spinner;
    }
}

