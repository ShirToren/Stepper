package FXML.main;

import FXML.definition.DefinitionController;
import FXML.execution.ExecutionController;
import FXML.execution.UIAdapter;
import dto.FlowDefinitionDTO;
import dto.FlowExecutionDTO;
import dto.XMLDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stepper.management.StepperEngineManager;
import tasks.UpdateExecutionDetailsTask;

import java.io.File;
import java.util.UUID;

public class MainAppController {
    private StepperEngineManager model = new StepperEngineManager();

    @FXML
    private Button loadFileButton;

    @FXML private BorderPane flowsDefinitionComponent;
    @FXML private DefinitionController flowsDefinitionComponentController;

    @FXML private BorderPane flowsExecutionComponent;
    @FXML private ExecutionController flowsExecutionComponentController;

    @FXML
    private Label filePathLabel;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab flowExecutionTab;

    private FlowExecutionDTO currentExecutionDTO;

    private final SimpleStringProperty selectedFileProperty;

    public MainAppController() {
        this.selectedFileProperty = new SimpleStringProperty();
    }

    public FlowExecutionDTO getCurrentExecutionDTO() {
        return currentExecutionDTO;
    }

    public void setCurrentExecutionDTO(FlowExecutionDTO currentExecutionDTO) {
        this.currentExecutionDTO = currentExecutionDTO;
    }

    @FXML
    public void initialize() {
        if (flowsDefinitionComponentController != null &&
                flowsExecutionComponentController != null) {
            flowsDefinitionComponentController.setMainAppController(this);
            flowsExecutionComponentController.setMainAppController(this);
        }
        filePathLabel.textProperty().bind(selectedFileProperty);
    }

    public void switchToExecutionTab() {
        tabPane.getSelectionModel().select(flowExecutionTab);
        //currentExecutionDTO = new FlowExecutionDTO(model.getCurrentFlowExecution(), new FlowDefinitionDTO(model.getCurrentFlowExecution().getFlowDefinition()));
    }

    public UUID executeFlowButtonActionListener() {
        clearFlowExecutionDetails();
        switchToExecutionTab();
        UUID id = model.createFlowExecution(flowsDefinitionComponentController.getSelectedFlowName());
        flowsExecutionComponentController.initFreeInputsComponents(id);
        return id;
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

    public void createFlowExecution(){
        model.createFlowExecution(flowsDefinitionComponentController.getSelectedFlowName());
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
                ////clear old file
                selectedFileProperty.set(filePath);
            } else {
                //message about invalid file
            }
            flowsDefinitionComponentController.show();
        } else {
            System.out.println("No file selected.");
        }
    }

    public void setModel(StepperEngineManager model) {
        this.model = model;
    }

    public StepperEngineManager getModel() {
        return model;
    }
}
