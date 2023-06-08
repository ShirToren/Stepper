package FXML.inputs;

import FXML.execution.ExecutionController;
import FXML.main.MainAppController;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import javafx.beans.binding.Bindings;
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

    private void clearAll(){
        freeInputsTextFieldComponents.clear();
        freeInputsSpinnerComponents.clear();
        mandatoryTextFields.clear();

        List<Integer> rowsToDelete = Arrays.asList(2, 3, 4, 5);
        collectInputsGP.getChildren().removeIf(node -> rowsToDelete.contains(GridPane.getRowIndex(node)));/*        for (int i = collectInputsGP.getRowConstraints().size() - 1; i > 1; i--) {
            collectInputsGP.getRowConstraints().remove(i);

            // Remove child nodes in the row
            int finalI = i;
            collectInputsGP.getChildren().removeIf(node -> GridPane.getRowIndex(node) == finalI);
        }*/
    }

    public void initInputsComponents(UUID id) {
        clearAll();
        int mandatoryRowIndex = 2, optionalRowIndex = 2;
        int mandatoryColIndex = 1, optionalColIndex = 5;
        List<DataInFlowDTO> currentFreeInputs = mainAppController.getModel().getExecutionDTOByUUID(id).getFlowDefinitionDTO().getFreeInputs();
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
        initExecuteButton(id, Math.max(optionalRowIndex, mandatoryRowIndex));
    }

    private void initExecuteButton(UUID id, int rowIndex) {
        executeButton = new Button();
        executeButton.setText("Start!");
        for (TextField textField: mandatoryTextFields) {
            executeButton.disableProperty().bind(
                    Bindings.createBooleanBinding(() -> textField.getText().isEmpty(),
                            textField.textProperty()));
        }
        collectInputsGP.add(executeButton, 7 ,rowIndex);
        executeButton.setOnAction(event -> {
            executeButtonActionListener(id);
            mainAppController.executeListener(id);
            Runnable task = () -> {
                mainAppController.getModel().executeFlow(id);
            };
            mainAppController.getModel().getExecutor().execute(task);
            //mainAppController.setCurrentExecutionDTO(flowExecutionDTO);
            //mainAppController.showFlowExecutionDetails();
        });
    }

    private void executeButtonActionListener(UUID id){
        for (Map.Entry<DataInFlowDTO, Spinner<Integer>> entry: freeInputsSpinnerComponents.entrySet()) {
            mainAppController.getModel().addFreeInputToFlowExecution(id, entry.getKey().getFinalName() +
                    "." + entry.getKey().getOwnerStep().getName(), entry.getValue().getValue());
        }
        for (Map.Entry<DataInFlowDTO, TextField> entry: freeInputsTextFieldComponents.entrySet()) {
            if(!entry.getValue().getText().isEmpty()) {
                mainAppController.getModel().addFreeInputToFlowExecution(id, entry.getKey().getFinalName() +
                        "." + entry.getKey().getOwnerStep().getName(), entry.getValue().getText());
            }
        }
        mainAppController.showFlowExecutionDetails(id);
    }

    public void setMainAppController(MainAppController mainAppController, ExecutionController executionController) {
        this.mainAppController = mainAppController;
        this.executionController = executionController;
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
