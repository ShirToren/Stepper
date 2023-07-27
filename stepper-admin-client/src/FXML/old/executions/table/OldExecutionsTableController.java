package FXML.old.executions.table;

import FXML.execution.history.ExecutionHistoryController;
import FXML.main.AdminMainAppController;
import impl.FlowExecutionDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.*;
import java.util.function.Predicate;

import static utils.Constants.REFRESH_RATE;

public class OldExecutionsTableController {
    private AdminMainAppController mainAppController;
    private Timer timer;
    private TimerTask executionHistoryRefresher;
    private ExecutionHistoryController executionHistoryController;

    @FXML
    private TableView<TargetTable> oldExecutionsTableView;

    @FXML
    private TableColumn<TargetTable, String> flowNameTableColumn;

    @FXML
    private TableColumn<TargetTable, String> executionTimeTableColumn;

    @FXML
    private TableColumn<TargetTable, String> resultTableColumn;
    @FXML
    private TableColumn<TargetTable, String> userTableColumn;
    @FXML
    private TableColumn<TargetTable, String> roleTableColumn;
    @FXML
    private CheckBox successCB;

    @FXML
    private CheckBox failureCB;

    @FXML
    private CheckBox warningCB;
    private final ObservableList<TargetTable> data = FXCollections.observableArrayList();
    private final FilteredList<TargetTable> filteredData = new FilteredList<>(data, p -> true);
    private final SimpleBooleanProperty isExecutionSelected = new SimpleBooleanProperty(false);
    private String selectedItemName;
    private UUID selectedItemID;

    @FXML public void initialize() {
        flowNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        executionTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        resultTableColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        userTableColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        roleTableColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        oldExecutionsTableView.setItems(data);
        oldExecutionsTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == -1) {
                isExecutionSelected.set(false);
            } else {
                isExecutionSelected.set(true);
            }
        });
        successCB.selectedProperty().set(false);
        failureCB.selectedProperty().set(false);
        warningCB.selectedProperty().set(false);

        successCB.selectedProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(createSuccessFilterPredicate(newValue)));
        failureCB.selectedProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(createFailureFilterPredicate(newValue)));
        warningCB.selectedProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(createWarningFilterPredicate(newValue)));

        SortedList<TargetTable> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(oldExecutionsTableView.comparatorProperty());
        oldExecutionsTableView.setItems(sortedData);
    }

    private Predicate<TargetTable> createSuccessFilterPredicate(boolean selected) {
        return person -> {
            if (!selected) {
                return true; // Show all data
            } else {
                return person.getResult().equals("SUCCESS");
            }
        };
    }
    private Predicate<TargetTable> createFailureFilterPredicate(boolean selected) {
        return person -> {
            if (!selected) {
                return true; // Show all data
            } else {
                return person.getResult().equals("FAILURE");
            }
        };
    }
    private Predicate<TargetTable> createWarningFilterPredicate(boolean selected) {
        return person -> {
            if (!selected) {
                return true; // Show all data
            } else {
                return person.getResult().equals("WARNING");
            }
        };
    }
    public void show() {
        addExecutionsToTable();
    }

    public void addExecutionsToTable() {
        startExecutionHistoryRefresher();
    }

    public void startExecutionHistoryRefresher() {
        executionHistoryRefresher = new OldExecutionsRefresher(this::updateExecutionHistory);
        timer = new Timer();
        timer.schedule(executionHistoryRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateExecutionHistory(List<FlowExecutionDTO> flowExecutionDTOList) {
        List<FlowExecutionDTO> finishedExecutions = new ArrayList<>();
        for (FlowExecutionDTO flowExecutionDTO: flowExecutionDTOList) {
            if(flowExecutionDTO.isFinished()) {
                finishedExecutions.add(flowExecutionDTO);
            }
        }
        if(finishedExecutions.size() != data.size()){
            data.clear();
            for (FlowExecutionDTO dto: flowExecutionDTOList) {
                if(dto.isFinished()) {
                    TargetTable row = new TargetTable(dto.getFlowDefinitionDTO().getName(),
                            dto.getStartExecutionTime(),
                            dto.getExecutionResult(), dto.getUuid(), dto.getUserName(),
                            dto.isManager()? "manager" : "not manager");
                    data.add(row);
                }
            }
        }
    }

    public void setMainAppController(AdminMainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void setExecutionHistoryController(ExecutionHistoryController executionHistoryController) {
        this.executionHistoryController = executionHistoryController;
    }

    public void clearAll(){
        data.clear();
        successCB.selectedProperty().set(false);
        failureCB.selectedProperty().set(false);
        warningCB.selectedProperty().set(false);
    }

    @FXML
    void rowClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {
            int selectedIndex = oldExecutionsTableView.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < data.size()) {
                TargetTable item = data.get(selectedIndex);
                selectedItemName = item.name;
                selectedItemID = item.id;

                executionHistoryController.addFlowExecutionDetails(item.id);
            }
        }

    }
    public void closeTimer(){
        if(timer != null) {
            this.timer.cancel();
        }
        if(executionHistoryRefresher != null){
            executionHistoryRefresher.cancel();
        }
    }

    public class TargetTable {
        private final String name;
        private final String time;
        private final String result;
        private final UUID id;
        private final String user;
        private final String role;

        public TargetTable(String name, String time, String result, UUID id, String user, String role) {
            this.name = name;
            this.time = time;
            this.result = result;
            this.id = id;
            this.user = user;
            this.role = role;
        }

        public String getName() { return name; }
        public String getTime() { return time; }
        public String getResult() { return result; }

        public UUID getID() { return id; }

        public String getUser() {
            return user;
        }

        public String getRole() {
            return role;
        }
    }
}
