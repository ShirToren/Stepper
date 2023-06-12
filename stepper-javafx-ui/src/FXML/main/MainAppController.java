package FXML.main;

import FXML.definition.DefinitionController;
import FXML.execution.ExecutionController;
import FXML.execution.history.ExecutionHistoryController;
import FXML.statistics.StatisticsController;
import dto.FlowExecutionDTO;
import dto.XMLDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stepper.management.StepperEngineManager;

import java.io.File;
import java.util.UUID;

public class MainAppController {
    private StepperEngineManager model = new StepperEngineManager();

    @FXML
    private Button loadFileButton;

    @FXML private BorderPane flowsDefinitionComponent;
    @FXML private DefinitionController flowsDefinitionComponentController;
    @FXML private GridPane statisticsComponent;
    @FXML private StatisticsController statisticsComponentController;

    @FXML private BorderPane flowsExecutionComponent;
    @FXML private ExecutionController flowsExecutionComponentController;
    @FXML private BorderPane executionHistoryComponent;
    @FXML private ExecutionHistoryController executionHistoryComponentController;

    @FXML
    private Label filePathLabel;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab flowExecutionTab;
    @FXML
    private Tab executionHistoryTab;
    @FXML
    private Tab statisticsTab;
    private final SimpleBooleanProperty isFlowSelected;

    private FlowExecutionDTO currentExecutionDTO;

    private final SimpleStringProperty selectedFileProperty;

    public MainAppController() {
        this.isFlowSelected = new SimpleBooleanProperty(false);
        this.selectedFileProperty = new SimpleStringProperty();
    }

    public FlowExecutionDTO getCurrentExecutionDTO() {
        return currentExecutionDTO;
    }

    @FXML
    public void initialize() {
        if (flowsDefinitionComponentController != null &&
                flowsExecutionComponentController != null &&
        executionHistoryComponentController != null &&
                statisticsComponentController != null) {
            flowsDefinitionComponentController.setMainAppController(this);
            flowsExecutionComponentController.setMainAppController(this);
            executionHistoryComponentController.setMainAppController(this);
            statisticsComponentController.setMainAppController(this);
        }
        filePathLabel.textProperty().bind(selectedFileProperty);
        executionHistoryTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                executionHistoryComponentController.showOldExecutions();
            }
        });
        statisticsTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                statisticsComponentController.show();
            }
        });
         flowsDefinitionComponentController.getExecuteButtonDisableProperty().bind(isFlowSelected.not());
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                if(oldTab.equals(flowExecutionTab) || newTab.equals((flowExecutionTab))) {
                    flowsExecutionComponentController.clearAll();
                }
                if(oldTab.equals(executionHistoryTab)) {
                    executionHistoryComponentController.clearAll();
                }
            }
        });
    }

    private void clearAll(){
        flowsDefinitionComponentController.clearAll();
        isFlowSelected.set(false);
        flowsExecutionComponentController.clearAll();
        executionHistoryComponentController.clearAll();
        statisticsComponentController.clearAll();
    }

    public void setSelectedFlow() {
        isFlowSelected.set(true);
    }

    public void switchToExecutionTab() {
        tabPane.getSelectionModel().select(flowExecutionTab);
    }

    public void addExecutionToTable() {
        executionHistoryComponentController.addExecutionToTable();
    }

    public void executeFlowButtonActionListener() {
        prepareToExecution(flowsDefinitionComponentController.getSelectedFlowName());
    }

    public void prepareToExecution(String flowName){
        clearFlowExecutionDetails();
        switchToExecutionTab();
        UUID id = model.createFlowExecution(flowName);
        flowsExecutionComponentController.initFreeInputsComponents(id);
    }

    public void prepareToReExecution(UUID prevID, String flowName){
        clearFlowExecutionDetails();
        switchToExecutionTab();
        UUID id = model.createFlowExecution(flowName);
        model.copyFreeInputsValues(prevID, id);
        flowsExecutionComponentController.initFreeInputsComponents(id);
    }

    public void prepareToContinuation(UUID prevID, String targetFlowName){
        clearFlowExecutionDetails();
        switchToExecutionTab();
        UUID id = model.createFlowExecution(targetFlowName);
        model.copyContinuationValues(prevID, id);
        flowsExecutionComponentController.initFreeInputsComponents(id);
    }

    public void clearFlowExecutionDetails(){
        flowsExecutionComponentController.clearFlowExecutionDetails();
    }

    public void showFlowExecutionDetails(UUID id){
        flowsExecutionComponentController.addFlowExecutionDetails(id);
    }

    public void executeListener(UUID id) {
       flowsExecutionComponentController.executeListener(id);
    }


    @FXML
    void loadFileButtonActionListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            String filePath = selectedFile.getPath();
            XMLDTO xmldto = model.readSystemInformationFile(filePath);
            if(xmldto.getFileState().equals("The file is valid and fully loaded.")) {
                clearAll();
                selectedFileProperty.set(filePath);
            } else {
                showErrorDialog("Invalid file.", xmldto.getFileState());
            }
            flowsDefinitionComponentController.show();
        } else {
            System.out.println("No file selected.");
        }
    }

    public StepperEngineManager getModel() {
        return model;
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
