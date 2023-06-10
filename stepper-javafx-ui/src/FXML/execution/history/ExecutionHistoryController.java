package FXML.execution.history;
import FXML.execution.details.ExecutionDetailsController;
import FXML.main.MainAppController;
import FXML.old.executions.table.OldExecutionsTableController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import java.util.UUID;

public class ExecutionHistoryController {
    private MainAppController mainAppController;
    @FXML
    private Button executeAgainButton;
    @FXML
    private GridPane oldExecutionsTableComponent;
    @FXML
    private BorderPane executionDetailsComponent;
    @FXML
    private ExecutionDetailsController executionDetailsComponentController;
    @FXML
    private OldExecutionsTableController oldExecutionsTableComponentController;

    @FXML
    public void initialize() {
        executeAgainButton.disableProperty().bind(oldExecutionsTableComponentController.isExecutionSelected().not());
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        oldExecutionsTableComponentController.setMainAppController(mainAppController);
        oldExecutionsTableComponentController.setExecutionHistoryController(this);
        executionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void showOldExecutions(){
        oldExecutionsTableComponentController.show();
    }

    public void addFlowExecutionDetails(UUID id) {
        executionDetailsComponentController.addFlowExecutionDetails(id);
        executionDetailsComponentController.updateFinalDetails(id);
        executionDetailsComponentController.addExecutedSteps(id);
    }

    public void addExecutionToTable(){
        oldExecutionsTableComponentController.addExecutionsToTable();
    }

    @FXML
    void executeAgainActionListener(ActionEvent event) {
        mainAppController.prepareToReExecution(oldExecutionsTableComponentController.getSelectedItemID(),
                oldExecutionsTableComponentController.getSelectedItemName());
    }
    public void clearAll(){
        executionDetailsComponentController.clearAll();
        oldExecutionsTableComponentController.clearAll();
    }
}
