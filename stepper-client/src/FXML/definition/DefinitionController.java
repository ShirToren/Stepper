package FXML.definition;

import FXML.definition.model.DefinitionsListWithVersion;
import FXML.flow.definition.details.FlowDefinitionDetailsController;
import FXML.main.MainAppController;
import impl.FlowDefinitionDTO;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static utils.Constants.REFRESH_RATE;


public class DefinitionController {
    private MainAppController mainAppController;
    private Timer timer;
    private TimerTask listRefresher;
    private final IntegerProperty flowDefinitionsVersion;
    //private final List<FlowDefinitionDTO> availableFlows;
    @FXML
    private BorderPane flowDetailsComponent;
    @FXML
    private FlowDefinitionDetailsController flowDetailsComponentController;


    private final ObservableList<TargetTable> data = FXCollections.observableArrayList();
    private final List<String> availableFlows;

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

    public DefinitionController() {
        this.flowDefinitionsVersion = new SimpleIntegerProperty();
        this.availableFlows = new ArrayList<>();
    }

    public String getSelectedFlowName() {
        return selectedFlowName;
    }

    @FXML public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        stepsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfSteps"));
        freeInputsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfFreeInputs"));
        continuationsColumn.setCellValueFactory(new PropertyValueFactory<>("numOfContinuations"));
        //startDefinitionRefresher();
        flowsTable.setItems(data);
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

    public void startDefinitionRefresher() {
        listRefresher = new DefinitionRefresher(this::updateFlowsTable,
                flowDefinitionsVersion, mainAppController.getRoles());
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateFlowsTable(List<FlowDefinitionDTO> flowDefinitionDTO) {
        List<TargetTable> flowsToDelete = new ArrayList<>();
        //if (flowDefinitionsWithVersion.getVersion() != flowDefinitionsVersion.get()) {
            Platform.runLater(() -> {
                //flowDefinitionsVersion.set(flowDefinitionsWithVersion.getVersion());
                    for (FlowDefinitionDTO flow : flowDefinitionDTO) {
                       if(!availableFlows.contains(flow.getName())) {
                           TargetTable row = new TargetTable(flow.getName(),
                                   flow.getDescription(),
                                   flow.getSteps().size(),
                                   flow.getFreeInputs().size(),
                                   flow.getNumberOfContinuations());
                           data.add(row);
                           availableFlows.add(flow.getName());
                       }
                    }
                for (TargetTable flowTargetTable: data) {
                    boolean isExist = false;
                    for (FlowDefinitionDTO flow : flowDefinitionDTO) {
                        if (flowTargetTable.name.equals(flow.getName())) {
                            isExist = true;
                            break;
                        }
                    }
                    if(!isExist) {
                        availableFlows.remove(flowTargetTable.name);
                        flowsToDelete.add(flowTargetTable);
                    }
                }
                data.removeAll(flowsToDelete);
            });
        //}
/*        Platform.runLater(() -> {
            data.clear();
            for (FlowDefinitionDTO flow : flowDefinitionDTOS) {
                TargetTable row = new TargetTable(flow.getName(),
                        flow.getDescription(),
                        flow.getSteps().size(),
                        flow.getFreeInputs().size(),
                        flow.getNumberOfContinuations());
                data.add(row);
            }
            flowsTable.setItems(data);
        });*/
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
