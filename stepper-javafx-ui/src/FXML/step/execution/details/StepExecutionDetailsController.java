package FXML.step.execution.details;

import FXML.main.MainAppController;
import dd.impl.list.FileList;
import dd.impl.list.ListData;
import dd.impl.list.StringList;
import dd.impl.relation.RelationData;
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
import logs.LogLine;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public void addStepDetails(UUID id, StepUsageDeclarationDTO step) {
        clearAll();
        FlowExecutionDTO currentExecutionDTO = mainAppController.getModel().getExecutionDTOByUUID(id.toString());
        stepNameLabel.setText(step.getName());
        if(currentExecutionDTO.getStepsTotalTimes().containsKey(step.getName())){
            DurationLabel.setText(Long.toString(currentExecutionDTO.getStepsTotalTimes().get(step.getName())));
        }
        resultLabel.setText(currentExecutionDTO.getStepsResults().get(step.getName()));
        summaryLabel.setText(currentExecutionDTO.getSummeryLines().get(step.getName()));
        startTimeLabel.setText(currentExecutionDTO.getStepsStartTimes().get(step.getName()));
        endTimeLabel.setText(currentExecutionDTO.getStepsEndTimes().get(step.getName()));

        List<DataInFlowDTO> flowsInputs = currentExecutionDTO.getFlowDefinitionDTO().getFlowsInputs();
        Map<DataInFlowDTO, Object> allExecutionInputs = currentExecutionDTO.getAllExecutionInputs();
        addDataDetails(step, flowsInputs, allExecutionInputs);
        addLabel("Outputs:", rowIndex, 1);
        rowIndex++;
        List<DataInFlowDTO> flowsOutputs = currentExecutionDTO.getFlowDefinitionDTO().getFlowsOutputs();
        Map<DataInFlowDTO, Object> allExecutionOutputs = currentExecutionDTO.getAllExecutionOutputs();
        addDataDetails(step, flowsOutputs, allExecutionOutputs);
        addLogLines(currentExecutionDTO, step);
    }

    private void addLogLines(FlowExecutionDTO currentExecutionDTO, StepUsageDeclarationDTO step){
        addLabel("Logs:", rowIndex, 1);
        StringBuilder stringBuilder = new StringBuilder();
       List<LogLineDTO> logLines = currentExecutionDTO.getLogLines().get(step.getName());
        for (LogLineDTO log: logLines) {
            stringBuilder.append(log.getTime()).append(": ").append(log.getLine()).append("\n");
        }
        addTextArea(stringBuilder.toString(), rowIndex, 2);
    }

    private void addDataDetails(StepUsageDeclarationDTO step, List<DataInFlowDTO> allData,
                                Map<DataInFlowDTO, Object> allExecutionData){
        for (DataInFlowDTO data: allData) {
            if(data.getOwnerStep().getName().equals(step.getName())){
                ////if input is exist
                if(allExecutionData.containsKey(data)) {
                    addLabel(data.getFinalName(), rowIndex, 1);
                    if (allExecutionData.get(data).equals("Not created due to failure in flow")) {
                        addTextArea(allExecutionData.get(data).toString(), rowIndex, 2);
                    } else {
                         if (data.getDataDefinition().getType().equals(Integer.class.getName()) ||
                                data.getDataDefinition().getType().equals(Double.class.getName())) {
                            addLabel(allExecutionData.get(data).toString(), rowIndex, 2);
                        } else if (data.getDataDefinition().getType().equals(ListData.class.getName())) {
                            if (allExecutionData.get(data).getClass().isAssignableFrom(FileList.class)) {
                                addFilesListView((FileList) allExecutionData.get(data), rowIndex, 2);
                            } else if (allExecutionData.get(data).getClass().isAssignableFrom(StringList.class)) {
                                addStringListView((StringList) allExecutionData.get(data), rowIndex, 2);
                            }
                        } else if (data.getDataDefinition().getType().equals(RelationData.class.getName())) {
                            addTableView((RelationData) allExecutionData.get(data), rowIndex, 2);
                        } else {
                            // if (data.getDataDefinition().getType().equals(String.class)) {
                                 addTextArea(allExecutionData.get(data).toString(), rowIndex, 2);
                             //}
                         }
                    }
                    rowIndex++;
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

