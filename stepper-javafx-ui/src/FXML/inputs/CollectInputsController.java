package FXML.inputs;

import FXML.execution.ExecutionController;
import FXML.main.MainAppController;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import step.api.DataNecessity;

import java.util.*;



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


    public void initInputsComponents(UUID id) {
        clearAll();
        int mandatoryRowIndex = 2, optionalRowIndex = 2;
        int mandatoryColIndex = 1, optionalColIndex = 5;
        FlowExecutionDTO executionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        List<DataInFlowDTO> currentFreeInputs = executionDTO.getFlowDefinitionDTO().getFreeInputs();
        for (DataInFlowDTO input: currentFreeInputs) {
            //addRowConstraints();
            if(input.getDataNecessity().equals(DataNecessity.MANDATORY)) {
                if(!input.getFinalName().equals(input.getOriginalName())){
                    addLabel(input.getUserString() + " (" + input.getFinalName() +"):", mandatoryRowIndex, mandatoryColIndex);
                } else {
                    addLabel(input.getUserString() + ":", mandatoryRowIndex, mandatoryColIndex);
                }
                if(input.getDataDefinition().getType().equals(Integer.class)){
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
                if(input.getDataDefinition().getType().equals(Integer.class)){
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

        initExecuteButton(id, Math.max(optionalRowIndex, mandatoryRowIndex));
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
                mainAppController.executeListener(id);
                clearAll();
                Runnable task = () -> {
                    mainAppController.getModel().executeFlow(id);
                    Platform.runLater(() -> {
                        mainAppController.addExecutionToTable();
                        executionController.addContinuations(id);
                        mainAppController.addStatistics();
                        executionController.enableRerun();
                    });
                };
                mainAppController.getModel().getExecutor().execute(task);
            }
        });
    }

    public void addRerunButton(UUID id){
        rerunButton = new Button("Rerun flow");
        Button button = addButton("Rerun flow", 2, 1);
        button.setOnAction(event1 -> {
            mainAppController.prepareToReExecution(id, mainAppController.getModel().getExecutionDTOByUUID(id).getFlowDefinitionDTO().getName());
        });
    }

    private boolean executeButtonActionListener(UUID id){
        for (Map.Entry<DataInFlowDTO, Spinner<Integer>> entry: freeInputsSpinnerComponents.entrySet()) {
            mainAppController.getModel().addFreeInputToFlowExecution(id, entry.getKey().getFinalName(), entry.getValue().getValue());
        }
        for (Map.Entry<DataInFlowDTO, TextField> entry: freeInputsTextFieldComponents.entrySet()) {
            if(!entry.getValue().getText().isEmpty()) {
                if(entry.getKey().getDataDefinition().getType().equals(Double.class)) {
                    try {
                        double value = Double.parseDouble(entry.getValue().getText());
                        mainAppController.getModel().addFreeInputToFlowExecution(id, entry.getKey().getFinalName(), value);
                    } catch (NumberFormatException e) {
                        // Display an alert or handle the invalid input
                        showErrorDialog("Invalid Input", "Please enter a valid double value.");
                        entry.getValue().clear();
                        return false;
                    }
                } else {
                    mainAppController.getModel().addFreeInputToFlowExecution(id, entry.getKey().getFinalName(), entry.getValue().getText());
                }
            }
        }
        mainAppController.showFlowExecutionDetails(id);
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
