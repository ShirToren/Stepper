package FXML.step.execution.details;

import FXML.main.MainAppController;
import dd.FileList;
import dd.ListData;
import dd.RelationData;
import dd.StringList;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import impl.LogLineDTO;
import impl.StepUsageDeclarationDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.*;

public class StepExecutionDetailsController {
    private MainAppController mainAppController;

    @FXML
    private GridPane stepDetailsGP;
    @FXML
    private Label stepNameLabel;
    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Label DurationLabel;

    @FXML
    private Label resultLabel;

    @FXML
    private Label summaryLabel;
    private int rowIndex = 8;

    public void addStepDetails(FlowExecutionDTO flowExecutionDTO, StepUsageDeclarationDTO step) {
        clearAll();
        stepNameLabel.setText(step.getName());
        if(flowExecutionDTO.getStepsTotalTimes().containsKey(step.getName())){
            DurationLabel.setText(Long.toString(flowExecutionDTO.getStepsTotalTimes().get(step.getName())));
        }
        resultLabel.setText(flowExecutionDTO.getStepsResults().get(step.getName()));
        summaryLabel.setText(flowExecutionDTO.getSummeryLines().get(step.getName()));
        startTimeLabel.setText(flowExecutionDTO.getStepsStartTimes().get(step.getName()));
        endTimeLabel.setText(flowExecutionDTO.getStepsEndTimes().get(step.getName()));

        List<DataInFlowDTO> flowsInputs = flowExecutionDTO.getFlowDefinitionDTO().getFlowsInputs();
        Map<DataInFlowDTO, Object> allExecutionInputs = flowExecutionDTO.getAllExecutionInputs();
        addDataDetails(step, flowsInputs, allExecutionInputs);
        addLabel("Outputs:", rowIndex, 1);
        rowIndex++;
        List<DataInFlowDTO> flowsOutputs = flowExecutionDTO.getFlowDefinitionDTO().getFlowsOutputs();
        Map<DataInFlowDTO, Object> allExecutionOutputs = flowExecutionDTO.getAllExecutionOutputs();
        addDataDetails(step, flowsOutputs, allExecutionOutputs);
        addLogLines(flowExecutionDTO, step);
    }

    private void addLogLines(FlowExecutionDTO currentExecutionDTO, StepUsageDeclarationDTO step){
        addLabel("Logs:", rowIndex, 1);
        StringBuilder stringBuilder = new StringBuilder();
       List<LogLineDTO> logLines = currentExecutionDTO.getLogLines().get(step.getName());
       if(logLines != null){
           for (LogLineDTO log: logLines) {
               stringBuilder.append(log.getTime()).append(": ").append(log.getLine()).append("\n");
           }
           addTextArea(stringBuilder.toString(), rowIndex, 2);
       }
    }

    private void addDataDetails(StepUsageDeclarationDTO step, List<DataInFlowDTO> allData,
                                Map<DataInFlowDTO, Object> allExecutionData) {
        for (DataInFlowDTO data : allData) {
            if (data.getOwnerStep().getName().equals(step.getName())) {
                ////if input is exist
                for (Map.Entry<DataInFlowDTO, Object> entry : allExecutionData.entrySet()) {
                    if (entry.getKey().getOwnerStep().getName().equals(step.getName())) {
                        if (entry.getKey().getFinalName().equals(data.getFinalName())) {
                            addLabel(data.getFinalName(), rowIndex, 1);
                            if (allExecutionData.get(entry.getKey()).equals("Not created due to failure in flow")) {
                                addTextArea(allExecutionData.get(entry.getKey()).toString(), rowIndex, 2);
                            } else {
                                if (data.getDataDefinition().getType().equals(Integer.class.getName()) ||
                                        data.getDataDefinition().getType().equals(Double.class.getName())) {
                                    addLabel(allExecutionData.get(entry.getKey()).toString(), rowIndex, 2);
                                } else if (data.getDataDefinition().getType().equals(ListData.class.getName())) {
                                    addListView((List<Object>) allExecutionData.get(entry.getKey()), rowIndex, 2);
                                } else if (data.getDataDefinition().getType().equals(RelationData.class.getName())) {
                                    addTableView((RelationData) allExecutionData.get(entry.getKey()), rowIndex, 2);
                                } else {
                                    // if (data.getDataDefinition().getType().equals(String.class)) {
                                    addTextArea(allExecutionData.get(entry.getKey()).toString(), rowIndex, 2);
                                    //}
                                }
                            }
                            rowIndex++;
                        }
                    }
                }
            }
        }

    }



    private void addLabel(String text, int rowIndex, int colIndex) {
        Label label = new Label(text);
        stepDetailsGP.add(label, colIndex,rowIndex);
    }

    private void addTextArea(String text, int rowIndex, int colIndex) {
        TextArea textArea = new TextArea(text);
        textArea.setPrefWidth(200);
        textArea.setPrefHeight(50);
        stepDetailsGP.add(textArea, colIndex,rowIndex);
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
        stepDetailsGP.add(listView, colIndex,rowIndex);
    }
    private void addStringListView(StringList values, int rowIndex, int colIndex) {
        ObservableList<String> items = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>();
        listView.setItems(items);
        listView.setPrefHeight(100);
        listView.setPrefWidth(200);
        items.addAll(values.getList());
        stepDetailsGP.add(listView, colIndex,rowIndex);
    }
    private void addListView(List<Object> values, int rowIndex, int colIndex) {
        ObservableList<String> items = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>();
        listView.setItems(items);
        listView.setPrefHeight(100);
        listView.setPrefWidth(200);
        if(values.size() != 0 ) {
            Object obj = values.get(0);
            if(obj instanceof File){
                for (Object file: values) {
                    items.add(((File)file).getAbsolutePath());
                }
            } else {
                for (Object string: values) {
                    items.add((String)string);
                }
            }
        }
        stepDetailsGP.add(listView, colIndex,rowIndex);
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
        stepDetailsGP.add(tableView, colIndex,rowIndex);
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void clearAll() {
        rowIndex = 8;
        stepNameLabel.setText("");
        DurationLabel.setText("");
        resultLabel.setText("");
        summaryLabel.setText("");
        startTimeLabel.setText("");
        endTimeLabel.setText("");
        stepDetailsGP.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= rowIndex);
    }
}

