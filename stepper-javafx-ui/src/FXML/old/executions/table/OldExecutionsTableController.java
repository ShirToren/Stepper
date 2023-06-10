package FXML.old.executions.table;

import FXML.execution.history.ExecutionHistoryController;
import FXML.main.MainAppController;
import dto.FlowExecutionDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.UUID;

public class OldExecutionsTableController {
    private MainAppController mainAppController;
    private ExecutionHistoryController executionHistoryController;

    @FXML
    private TableView<TargetTable> oldExecutionsTableView;

    @FXML
    private TableColumn<TargetTable, String> flowNameTableColumn;

    @FXML
    private TableColumn<TargetTable, String> executionTimeTableColumn;

    @FXML
    private TableColumn<TargetTable, String> resultTableColumn;
    private final ObservableList<TargetTable> data = FXCollections.observableArrayList();
    private SimpleBooleanProperty isExecutionSelected = new SimpleBooleanProperty(false);
    private String selectedItemName;
    private UUID selectedItemID;

    @FXML public void initialize() {
        flowNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        executionTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        resultTableColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        oldExecutionsTableView.setItems(data);
        oldExecutionsTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == -1) {
                isExecutionSelected.set(false);
            } else {
                isExecutionSelected.set(true);
            }
        });
    }

    public void show() {
        data.clear();
        addExecutionsToTable();
    }

    public void addExecutionsToTable() {
        data.clear();
        List<FlowExecutionDTO> allFlowExecutionsDTO = mainAppController.getModel().getAllFlowExecutionsDTO();
        for (FlowExecutionDTO dto: allFlowExecutionsDTO) {
            if(dto.isFinished()) {
                TargetTable row = new TargetTable(dto.getFlowDefinitionDTO().getName(),
                        dto.getStartExecutionTime().toString(),
                        dto.getExecutionResult().name(), dto.getUuid());
                data.add(row);
            }
        }
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    public UUID getSelectedItemID() {
        return selectedItemID;
    }

    public SimpleBooleanProperty isExecutionSelected() {
        return isExecutionSelected;
    }

    public void setExecutionHistoryController(ExecutionHistoryController executionHistoryController) {
        this.executionHistoryController = executionHistoryController;
    }

    public void clearAll(){
        data.clear();
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
            } else {
                // Invalid index
                System.out.println("Invalid index.");
                //isExecutionSelected.set(false);
            }
        }
    }

    public class TargetTable {
        private final String name;
        private final String time;
        private final String result;
        private final UUID id;

        public TargetTable(String name, String time, String result, UUID id) {
            this.name = name;
            this.time = time;
            this.result = result;
            this.id = id;
        }

        public String getName() { return name; }
        public String getTime() { return time; }
        public String getResult() { return result; }

        public UUID getID() { return id; }


    }
}
