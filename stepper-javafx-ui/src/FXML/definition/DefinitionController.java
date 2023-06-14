package FXML.definition;

import FXML.flow.definition.details.FlowDefinitionDetailsController;
import FXML.main.MainAppController;
import dto.FlowDefinitionDTO;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableColumn;

import java.util.List;


public class DefinitionController {
    private MainAppController mainAppController;
    @FXML
    private BorderPane flowDetailsComponent;
    @FXML
    private FlowDefinitionDetailsController flowDetailsComponentController;


    private final ObservableList<TargetTable> data = FXCollections.observableArrayList();

    @FXML
    private TableView<TargetTable> flowsTable;

    @FXML
    private TableColumn<TargetTable, String> nameColumn;

    @FXML
    private TableColumn<TargetTable, String> descriptionColumn;

    @FXML
    private TableColumn<TargetTable, Integer> stepsColumn;

    @FXML
    private TableColumn<TargetTable, Integer> freeInputsColumn;

    @FXML
    private TableColumn<TargetTable, Integer> continuationsColumn;
    private String selectedFlowName;

    public String getSelectedFlowName() {
        return selectedFlowName;
    }

    @FXML public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        stepsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfSteps"));
        freeInputsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfFreeInputs"));
        continuationsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfContinuations"));
    }

    public void clearAll(){
        data.clear();
        flowDetailsComponentController.clearPrevDetails();
    }

    @FXML
    void rowClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {
            flowDetailsComponentController.clearPrevDetails();
            int selectedIndex = flowsTable.getSelectionModel().getSelectedIndex();
            ObservableList<TargetTable> items = flowsTable.getItems();

            if (selectedIndex >= 0 && selectedIndex < items.size()) {
                mainAppController.setSelectedFlow();
                TargetTable item = items.get(selectedIndex);
                flowDetailsComponentController.addFlowDetails(item.getName());
                this.selectedFlowName = item.getName();

            }
        }
    }

    public BooleanProperty getExecuteButtonDisableProperty(){
        return  flowDetailsComponentController.getExecuteButtonDisableProperty();
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        flowDetailsComponentController.setMainAppController(mainAppController);
    }
    public void show() {
        addFlowsToTable();
    }
    public void addFlowsToTable() {
        data.clear();
        List<FlowDefinitionDTO> flowDefinitions = mainAppController.getModel().getAllFlowDefinitionsInStepper();
        for (FlowDefinitionDTO flow : flowDefinitions) {
            TargetTable row = new TargetTable(flow.getName(),
                    flow.getDescription(),
                    flow.getSteps().size(),
                    flow.getFreeInputs().size(),
                    flow.getNumberOfContinuations());
            data.add(row);
        }
        flowsTable.setItems(data);
    }


    public class TargetTable {
        private final String name;
        private final String description;
        private final int numOfSteps;
        private final int numOfFreeInputs;
        private final int numOfContinuations;

        public TargetTable(String name, String description, int numOfSteps, int numOfFreeInputs, int numOfContinuations) {
            this.name = name;
            this.description = description;
            this.numOfSteps = numOfSteps;
            this.numOfFreeInputs = numOfFreeInputs;
            this.numOfContinuations = numOfContinuations;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getNumOfSteps() {
            return numOfSteps;
        }

        public int getNumOfFreeInputs() {
            return numOfFreeInputs;
        }

        public int getNumOfContinuations() {
            return numOfContinuations;
        }
    }

}
