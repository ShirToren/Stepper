package FXML.statistics;

import FXML.main.AdminMainAppController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Map;

public class StatisticsController {
    private AdminMainAppController mainAppController;
    @FXML
    private GridPane statisticsGP;

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

    public void setMainAppController(AdminMainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void show() {
        addStatisticsToTable();
    }

    public void clearAll(){
        flowsData.clear();
        stepsData.clear();
        statisticsGP.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= 5);
    }

    private void createTimesChart(String text, Map<String, Integer> executedTimes, int rowIndex, int colIndex){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(text);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> time: executedTimes.entrySet()) {
            series.getData().add(new XYChart.Data<>(time.getKey(), time.getValue()));
        }
        chart.getData().add(series);
        chart.setPrefHeight(250);
        chart.setPrefWidth(200);
        statisticsGP.add(chart, colIndex, rowIndex);
    }
    private void createAvgChart(String text, Map<String, Long> executedTotalMillis,Map<String, Integer> executedTimes,  int rowIndex, int colIndex){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(text);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Long> millis: executedTotalMillis.entrySet()) {
            series.getData().add(new XYChart.Data<>(millis.getKey(), millis.getValue() / executedTimes.get(millis.getKey())));
        }
        chart.getData().add(series);
        chart.setPrefHeight(250);
        chart.setPrefWidth(200);
        statisticsGP.add(chart, colIndex, rowIndex);
    }
    public void addStatisticsToTable() {
        clearAll();
        Map<String, Integer> flowExecutedTimes = mainAppController.getModel().getFlowExecutedTimes();
        Map<String, Integer> stepExecutedTimes = mainAppController.getModel().getStepExecutedTimes();
        Map<String, Long> flowExecutedTotalMillis = mainAppController.getModel().getFlowExecutedTotalMillis();
        Map<String, Long> stepExecutedTotalMillis = mainAppController.getModel().getStepExecutedTotalMillis();

        for (Map.Entry<String, Integer> times: flowExecutedTimes.entrySet()) {
            TargetTable row = new TargetTable(times.getKey(), times.getValue(), (double) flowExecutedTotalMillis.get(times.getKey()) / times.getValue());
            flowsData.add(row);
        }
        for (Map.Entry<String, Integer> times: stepExecutedTimes.entrySet()) {
            TargetTable row = new TargetTable(times.getKey(), times.getValue(), (double)stepExecutedTotalMillis.get(times.getKey()) / times.getValue());
            stepsData.add(row);
        }
        createTimesChart("Flows executions", flowExecutedTimes, 5, 0);
        createAvgChart("Flows average time",flowExecutedTotalMillis, flowExecutedTimes, 5, 1);
        createTimesChart("Steps executions", stepExecutedTimes, 6, 0);
        createAvgChart("Steps average time", stepExecutedTotalMillis, stepExecutedTimes, 6, 1);
    }

    public class TargetTable {
        private final String name;
        private final int times;
        private final double avg;

        public TargetTable(String name, int times, double avg) {
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

        public double getAvg() {
            return avg;
        }
    }

}
