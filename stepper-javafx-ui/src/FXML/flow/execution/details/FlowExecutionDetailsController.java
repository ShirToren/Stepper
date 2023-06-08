package FXML.flow.execution.details;

import FXML.main.MainAppController;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import step.api.DataNecessity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FlowExecutionDetailsController {
    private MainAppController mainAppController;


    @FXML
    private Label flowNameLabel;

    @FXML
    private Label flowIDLabel;

    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label resultLabel;
    @FXML
    private ListView<String> freeInputsLV;

    private final ObservableList<String> freeInputsLVItems = FXCollections.observableArrayList();

    @FXML
    private Label freeInputTypeLabel;

    @FXML
    private Label freeInputValueLabel;

    @FXML
    private Label freeInputNecessityLabel;

    @FXML
    private ListView<String> outputsLV;
    private final ObservableList<String> outputsLVItems = FXCollections.observableArrayList();

    @FXML
    private Label outputTypeLabel;

    @FXML
    private Label outputValueLabel;

    @FXML
    private Label outputStepOwnerLabel;

    private List<DataInFlowDTO> freeInputsList;

    @FXML
    public void initialize() {
        freeInputsLV.setItems(freeInputsLVItems);
        outputsLV.setItems(outputsLVItems);
    }

    public void addFlowExecutionDetails(UUID id) {
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        flowNameLabel.setText(currentExecutionDTO.getFlowDefinitionDTO().getName());
        flowIDLabel.setText(currentExecutionDTO.getUuid().toString());
        LocalTime startExecutionTime = currentExecutionDTO.getStartExecutionTime();
        if(startExecutionTime != null) {
            startTimeLabel.setText(startExecutionTime.toString());
        }
        //endTimeLabel.setText(currentExecutionDTO.getEndExecutionTime().toString());
        //durationLabel.setText(Long.toString(currentExecutionDTO.getTotalTime().toMillis()));
        //resultLabel.setText(currentExecutionDTO.getExecutionResult().name());
        showFreeInputsDetails(id);
        //showAllFlowOutputsDetails();
    }

    public void updateEndTime(String endTime) { endTimeLabel.setText(endTime); }
    public void updateStartTime(String startTime) { startTimeLabel.setText(startTime); }

    public void updateDuration(String duration) { durationLabel.setText(duration); }
    public void updateResult(String result) { resultLabel.setText(result); }
    public void addOutput(String outputName) { outputsLVItems.add(outputName); }

    public void clearAllOutputs(){
        outputsLVItems.clear();
    }


    private void showFreeInputsDetails(UUID id) {
        Map<String, Object> actualFreeInputs = mainAppController.getModel().getActualFreeInputsList(id);
        List<DataInFlowDTO> optionalInput = new ArrayList<>();
        freeInputsList = mainAppController.getModel().getExecutionDTOByUUID(id).getFlowDefinitionDTO().getFreeInputs();
        for (DataInFlowDTO freeInput : freeInputsList) {
            if (freeInput.getDataNecessity().equals(DataNecessity.MANDATORY) &&
                    actualFreeInputs.containsKey(freeInput.getFinalName() + "." + freeInput.getOwnerStep().getName())) {
                freeInputsLVItems.add(freeInput.getFinalName());
            } else if (actualFreeInputs.containsKey(freeInput.getFinalName() + "." + freeInput.getOwnerStep().getName())) {
                optionalInput.add(freeInput);
            }
        }
        for (DataInFlowDTO optional : optionalInput) {
            freeInputsLVItems.add(optional.getFinalName());
        }
    }

    @FXML
    void inputClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {

            int selectedIndex = freeInputsLV.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < freeInputsLVItems.size()) {
                String item = freeInputsLVItems.get(selectedIndex);
                for (DataInFlowDTO freeInput: freeInputsList) {
                    if(freeInput.getFinalName().equals(item)) {
                        freeInputTypeLabel.setText("Type: " + freeInput.getDataDefinition().getName());
                        freeInputValueLabel.setText("Value: " + mainAppController.getModel().getActualFreeInputsList().get(item + "." + freeInput.getOwnerStep().getName()).toString());
                        freeInputNecessityLabel.setText("Necessity: " + freeInput.getDataNecessity().name());
                    }
                }
            } else {
                // Invalid index
                //System.out.println("Invalid index.");
            }
        }
    }

    @FXML
    void outputClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {

            int selectedIndex = outputsLV.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < outputsLVItems.size()) {
                String item = outputsLVItems.get(selectedIndex);
                for (Map.Entry<DataInFlowDTO, Object> entry : mainAppController.getCurrentExecutionDTO().getAllExecutionOutputs().entrySet()) {
                    if(entry.getKey().getFinalName().equals(item)) {
                        outputTypeLabel.setText("Type: " + entry.getKey().getDataDefinition().getName());
                        outputValueLabel.setText("Value: " + entry.getValue().toString());
                        outputStepOwnerLabel.setText("From step: " + entry.getKey().getOwnerStep().getName());
                    }
                }
            } else {
                // Invalid index
                //System.out.println("Invalid index.");
            }
        }
    }

    private void showAllFlowOutputsDetails() {
        for (Map.Entry<DataInFlowDTO, Object> entry : mainAppController.getCurrentExecutionDTO().getAllExecutionOutputs().entrySet()) {
            outputsLVItems.add(entry.getKey().getFinalName());
        }
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void clearAll(){
        flowNameLabel.setText("");
        flowIDLabel.setText("");
        startTimeLabel.setText("");
        endTimeLabel.setText("");
        durationLabel.setText("");
        resultLabel.setText("");
        freeInputsLVItems.clear();
        freeInputTypeLabel.setText("");
        freeInputValueLabel.setText("");
        freeInputNecessityLabel.setText("");
        outputsLVItems.clear();
        outputTypeLabel.setText("");
        outputValueLabel.setText("");
        outputStepOwnerLabel.setText("");
    }
}
