package FXML.statistics;

import FXML.main.AdminMainAppController;
import impl.StatisticsDTO;
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
import java.util.Timer;
import java.util.TimerTask;

import static utils.Constants.REFRESH_RATE;

public class StatisticsController {
    private AdminMainAppController mainAppController;
    private Timer timer;
    private TimerTask statisticsRefresher;
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

    public void startStatisticsRefresher() {
        statisticsRefresher = new StatisticsRefresher(this::updateStatistics);
        timer = new Timer();
        timer.schedule(statisticsRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateStatistics(StatisticsDTO statisticsDTO) {
        if (flowsData.size() != statisticsDTO.getFlowExecutedTimes().size()) {
            clearAll();
            for (Map.Entry<String, Integer> times : statisticsDTO.getFlowExecutedTimes().entrySet()) {
                TargetTable row = new TargetTable(times.getKey(), times.getValue(), (double) statisticsDTO.getFlowExecutedTotalMillis().get(times.getKey()) / times.getValue());
                flowsData.add(row);
            }
            for (Map.Entry<String, Integer> times : statisticsDTO.getStepExecutedTimes().entrySet()) {
                TargetTable row = new TargetTable(times.getKey(), times.getValue(), (double) statisticsDTO.getStepExecutedTotalMillis().get(times.getKey()) / times.getValue());
                stepsData.add(row);
            }
            createTimesChart("Flows executions", statisticsDTO.getFlowExecutedTimes(), 5, 0);
            createAvgChart("Flows average time", statisticsDTO.getFlowExecutedTotalMillis(), statisticsDTO.getFlowExecutedTimes(), 5, 1);
            createTimesChart("Steps executions", statisticsDTO.getStepExecutedTimes(), 6, 0);
            createAvgChart("Steps average time", statisticsDTO.getStepExecutedTotalMillis(), statisticsDTO.getStepExecutedTimes(), 6, 1);
        }
    }
    public void addStatisticsToTable() {
        //clearAll();
        startStatisticsRefresher();
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
