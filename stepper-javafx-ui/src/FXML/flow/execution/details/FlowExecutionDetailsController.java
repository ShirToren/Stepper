package FXML.flow.execution.details;

import FXML.main.MainAppController;
import dd.impl.list.FileList;
import dd.impl.list.ListData;
import dd.impl.list.StringList;
import dd.impl.relation.RelationData;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import step.api.DataNecessity;
import java.io.File;
import java.util.*;

public class FlowExecutionDetailsController {
    private MainAppController mainAppController;
    @FXML
    private GridPane flowExecutionDetailsGP;
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
    private List<DataInFlowDTO> freeInputsList;
    private int rowIndex = 11;

    @FXML
    public void initialize() {
        freeInputsLV.setItems(freeInputsLVItems);
    }
    public void addFlowExecutionDetails(UUID id) {
        FlowExecutionDTO executionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        flowNameLabel.setText(executionDTO.getFlowDefinitionDTO().getName());
        flowIDLabel.setText(executionDTO.getUuid().toString());
        showFreeInputsDetails(id);
        showAllFlowOutputsDetails(id);
    }
    public void updateEndTime(String endTime) { endTimeLabel.setText(endTime); }
    public void updateName(String name) { flowNameLabel.setText(name); }
    public void updateID(String id) { flowIDLabel.setText(id); }
    public void updateStartTime(String startTime) {
        startTimeLabel.setText(startTime);
    }
    public void updateDuration(String duration) { durationLabel.setText(duration); }
    public void updateResult(String result) { resultLabel.setText(result); }
    public void addOutput(Map.Entry<DataInFlowDTO, Object> entry) {
        addLabel(entry.getKey().getFinalName(), rowIndex, 1);
        if(entry.getValue().equals("Not created due to failure in flow")) {
            addTextArea(entry.getValue().toString(), rowIndex, 2);
        } else {
            if(entry.getKey().getDataDefinition().getType().equals(String.class)){
                addTextArea(entry.getValue().toString(), rowIndex, 2);
            } else if(entry.getKey().getDataDefinition().getType().equals(Integer.class)||
                    entry.getKey().getDataDefinition().getType().equals(Double.class)) {
                addLabel(entry.getValue().toString(), rowIndex, 2);
            } else if (entry.getKey().getDataDefinition().getType().equals(ListData.class)) {
                if (entry.getValue().getClass().isAssignableFrom(FileList.class)) {
                    addFilesListView((FileList) entry.getValue(), rowIndex, 2);
                } else if (entry.getValue().getClass().isAssignableFrom(StringList.class)){
                    addStringListView((StringList) entry.getValue(), rowIndex, 2);
                }
            }  else if(entry.getKey().getDataDefinition().getType().equals(RelationData.class)) {
                addTableView((RelationData)entry.getValue(), rowIndex, 2);
            }
        }
        /*} else if(entry.getKey().getDataDefinition().getType().equals(StringList.class)){
            addStringListView((StringList)entry.getValue(), rowIndex, 2);
        }*/
        rowIndex++;
    }
    public void addInput(String inputName) { freeInputsLVItems.add(inputName); }
    public void clearAllOutputs(){
        //outputsLVItems.clear();
        rowIndex = 11;
        flowExecutionDetailsGP.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= rowIndex);
    }
    public void clearAllInputs(){
        freeInputsLVItems.clear();
    }
    private void showFreeInputsDetails(UUID id) {
        freeInputsLVItems.clear();
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
        freeInputsLV.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                int selectedIndex = freeInputsLV.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < freeInputsLVItems.size()) {
                    String item = freeInputsLVItems.get(selectedIndex);
                    for (DataInFlowDTO freeInput: freeInputsList) {
                        if(freeInput.getFinalName().equals(item)) {
                            freeInputTypeLabel.setText("Type: " + freeInput.getDataDefinition().getName());
                            freeInputValueLabel.setText("Value: " + mainAppController.getModel().getActualFreeInputsList(id).get(item + "." + freeInput.getOwnerStep().getName()).toString());
                            freeInputNecessityLabel.setText("Necessity: " + freeInput.getDataNecessity().name());
                        }
                    }
                }
            }
        });
    }

    private void addLabel(String text, int rowIndex, int colIndex) {
        Label label = new Label(text);
        flowExecutionDetailsGP.add(label, colIndex,rowIndex);
    }

    private void addTextArea(String text, int rowIndex, int colIndex) {
        TextArea textArea = new TextArea(text);
        textArea.setPrefWidth(200);
        textArea.setPrefHeight(50);
        flowExecutionDetailsGP.add(textArea, colIndex,rowIndex);
    }

    private void addFilesListView(FileList values, int rowIndex, int colIndex) {
        ObservableList<String> items = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>();
        listView.setItems(items);
        listView.setPrefHeight(50);
        listView.setPrefWidth(200);
        for (File file: values.getList()) {
            items.add(file.getAbsolutePath());
        }
        flowExecutionDetailsGP.add(listView, colIndex,rowIndex);
    }
    private void addStringListView(StringList values, int rowIndex, int colIndex) {
        ObservableList<String> items = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>();
        listView.setItems(items);
        listView.setPrefHeight(100);
        listView.setPrefWidth(200);
        items.addAll(values.getList());
        flowExecutionDetailsGP.add(listView, colIndex,rowIndex);
    }
    private void addTableView(RelationData relation, int rowIndex, int colIndex) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        tableView.setItems(data);
        tableView.setPrefHeight(100);
        tableView.setPrefWidth(200);

        for (int i = 0; i < relation.getColumns().size(); i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(relation.getColumns().get(i));
            column.setCellValueFactory(param ->
                    new SimpleStringProperty(param.getValue().get(columnIndex)));
            tableView.getColumns().add(column);
        }

        for (int i = 0; i < relation.getNumOfRows(); i++) {
            ObservableList<String> rowData = FXCollections.observableArrayList();
            for (int j = 0; j < relation.getColumns().size(); j++) {
                rowData.add(relation.getRowDataByColumnsOrder(i).get(j));
            }
            data.add(rowData);
        }
        flowExecutionDetailsGP.add(tableView, colIndex,rowIndex);
    }

    private void showAllFlowOutputsDetails(UUID id) {
        clearAllOutputs();
        //outputsLVItems.clear();
        FlowExecutionDTO executionDTO = mainAppController.getModel().getExecutionDTOByUUID(id);
        for (Map.Entry<DataInFlowDTO , Object> entry : executionDTO.getAllExecutionOutputs().entrySet()) {
            addOutput(entry);
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
        clearAllInputs();
        clearAllOutputs();
    }
}
