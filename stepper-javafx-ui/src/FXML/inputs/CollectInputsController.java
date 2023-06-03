package FXML.inputs;

import FXML.main.MainAppController;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import step.api.DataNecessity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CollectInputsController {

    private MainAppController mainAppController;
    @FXML
    private GridPane collectInputsGP;
    private final List<TextField> mandatoryTextFields;
    private Button executeButton;
    
    private final Map<DataInFlowDTO, TextField> freeInputsTextFieldComponents;
    private final Map<DataInFlowDTO, Spinner<Integer>> freeInputsSpinnerComponents;

    public CollectInputsController() {
        this.mandatoryTextFields = new ArrayList<>();
        this.freeInputsTextFieldComponents = new HashMap<>();
        this.freeInputsSpinnerComponents = new HashMap<>();
    }

    @FXML
    public void initialize() {

    }

    public void initInputsComponents() {
        int mandatoryRowIndex = 2, optionalRowIndex = 2;
        int mandatoryColIndex = 1, optionalColIndex = 5;
        List<DataInFlowDTO> currentFreeInputs = mainAppController.getModel().getCurrentFreeInputs();
        for (DataInFlowDTO input: currentFreeInputs) {
            //addRowConstraints();
            if(input.getDataNecessity().equals(DataNecessity.MANDATORY)) {
                addLabel(input.getUserString() + ":", mandatoryRowIndex, mandatoryColIndex);
                if(input.getDataDefinition().getType().equals(String.class)) {
                    TextField textField = addMandatoryTextField(mandatoryRowIndex, mandatoryColIndex + 1);
                    freeInputsTextFieldComponents.put(input, textField);
                } else {
                    Spinner<Integer> integerSpinner = addSpinner(mandatoryRowIndex, mandatoryColIndex + 1);
                    freeInputsSpinnerComponents.put(input, integerSpinner);
                }
                mandatoryRowIndex++;
            } else {
                addLabel(input.getUserString() + ":", optionalRowIndex, optionalColIndex);
                if(input.getDataDefinition().getType().equals(String.class)) {
                    TextField textField = addTextField(optionalRowIndex, optionalColIndex + 1);
                    freeInputsTextFieldComponents.put(input, textField);
                } else {
                    Spinner<Integer> integerSpinner = addSpinner(optionalRowIndex, optionalColIndex + 1);
                    freeInputsSpinnerComponents.put(input, integerSpinner);
                }
                optionalRowIndex++;
            }
        }

        initExecuteButton(Math.max(optionalRowIndex, mandatoryRowIndex));


    }

    private void initExecuteButton(int rowIndex) {
        executeButton = new Button();
        executeButton.setText("Start!");
        for (TextField textField: mandatoryTextFields) {
            executeButton.disableProperty().bind(
                    Bindings.createBooleanBinding(() -> textField.getText().isEmpty(),
                            textField.textProperty()));
        }
        collectInputsGP.add(executeButton, 7 ,rowIndex);
        executeButton.setOnAction(event -> {
            for (Map.Entry<DataInFlowDTO, Spinner<Integer>> entry: freeInputsSpinnerComponents.entrySet()) {
                mainAppController.getModel().addFreeInputToFlowExecution(entry.getKey().getFinalName() +
                        "." + entry.getKey().getOwnerStep().getName(), entry.getValue().getValue());
            }
            for (Map.Entry<DataInFlowDTO, TextField> entry: freeInputsTextFieldComponents.entrySet()) {
                if(!entry.getValue().getText().isEmpty()) {
                    mainAppController.getModel().addFreeInputToFlowExecution(entry.getKey().getFinalName() +
                            "." + entry.getKey().getOwnerStep().getName(), entry.getValue().getText());
                }
            }
            FlowExecutionDTO flowExecutionDTO = mainAppController.getModel().executeFlow();
            mainAppController.setCurrentExecutionDTO(flowExecutionDTO);
            mainAppController.showFlowExecutionDetails();
        });
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    private void addRowConstraints() {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(10.0);
        rowConstraints.setPrefHeight(30.0);
        rowConstraints.setVgrow(Priority.SOMETIMES);
        collectInputsGP.getRowConstraints().add(rowConstraints);

    }

    private void addLabel(String text, int rowIndex, int colIndex) {
        Label label = new Label(text);
        collectInputsGP.add(label, colIndex,rowIndex);
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
