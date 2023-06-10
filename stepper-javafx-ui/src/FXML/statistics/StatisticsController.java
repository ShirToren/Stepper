package FXML.statistics;

import FXML.main.MainAppController;
import FXML.old.executions.table.OldExecutionsTableController;
import dto.FlowExecutionDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatisticsController {
    private MainAppController mainAppController;

    @FXML
    private TableView<TargetTable> flowsTV;
    @FXML
    private TableColumn<TargetTable, String> flowNameTableColumn;

    @FXML
    private TableColumn<TargetTable, Integer> flowTimesTableColumn;

    @FXML
    private TableColumn<TargetTable, Long> flowAvgTableColumn;

    @FXML
    private TableView<TargetTable> stepsTV;
    @FXML
    private TableColumn<TargetTable, String> stepNameTableColumn;

    @FXML
    private TableColumn<TargetTable, Integer> stepTimesTableColumn;

    @FXML
    private TableColumn<TargetTable, Long> stepAvgTableColumn;
    private final ObservableList<TargetTable> flowsData = FXCollections.observableArrayList();
    private final ObservableList<TargetTable> stepsData = FXCollections.observableArrayList();

    @FXML public void initialize() {
        flowNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        flowTimesTableColumn.setCellValueFactory(new PropertyValueFactory<>("times"));
        flowAvgTableColumn.setCellValueFactory(new PropertyValueFactory<>("avg"));

        stepNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stepTimesTableColumn.setCellValueFactory(new PropertyValueFactory<>("times"));
        stepAvgTableColumn.setCellValueFactory(new PropertyValueFactory<>("avg"));
        flowsTV.setItems(flowsData);
        stepsTV.setItems(stepsData);
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void show() {
        addStatisticsToTable();
    }

    public void clearAll(){
        flowsData.clear();
        stepsData.clear();
    }

    public void addStatisticsToTable() {
        clearAll();
        Map<String, Integer> flowExecutedTimes = mainAppController.getModel().getFlowExecutedTimes();
        Map<String, Integer> stepExecutedTimes = mainAppController.getModel().getStepExecutedTimes();
        Map<String, Long> flowExecutedTotalMillis = mainAppController.getModel().getFlowExecutedTotalMillis();
        Map<String, Long> stepExecutedTotalMillis = mainAppController.getModel().getStepExecutedTotalMillis();

        for (Map.Entry<String, Integer> times: flowExecutedTimes.entrySet()) {
            TargetTable row = new TargetTable(times.getKey(), times.getValue(), flowExecutedTotalMillis.get(times.getKey()));
            flowsData.add(row);
        }
        for (Map.Entry<String, Integer> times: stepExecutedTimes.entrySet()) {
            TargetTable row = new TargetTable(times.getKey(), times.getValue(), stepExecutedTotalMillis.get(times.getKey()));
            stepsData.add(row);
        }
    }

    public class TargetTable {
        private final String name;
        private final int times;
        private final long avg;

        public TargetTable(String name, int times, long avg) {
            this.name = name;
            this.times = times;
            this.avg = avg;
        }

        public String getName() {
            return name;
        }

        public int getTimes() {
            return times;
        }

        public long getAvg() {
            return avg;
        }
    }

}
