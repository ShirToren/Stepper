package FXML.execution.history;
import FXML.execution.details.ExecutionDetailsController;
import FXML.main.AdminMainAppController;
import FXML.old.executions.table.OldExecutionsTableController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import java.util.UUID;

public class ExecutionHistoryController {
    private AdminMainAppController mainAppController;

    @FXML
    private GridPane oldExecutionsTableComponent;
    @FXML
    private ScrollPane executionDetailsComponent;
    @FXML
    private ExecutionDetailsController executionDetailsComponentController;
    @FXML
    private OldExecutionsTableController oldExecutionsTableComponentController;

    @FXML
    public void initialize() {
       // executeAgainButton.disableProperty().bind(oldExecutionsTableComponentController.isExecutionSelected().not());
    }

    public void setMainAppController(AdminMainAppController mainAppController) {
        this.mainAppController = mainAppController;
        oldExecutionsTableComponentController.setMainAppController(mainAppController);
        oldExecutionsTableComponentController.setExecutionHistoryController(this);
        executionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void showOldExecutions(){
        oldExecutionsTableComponentController.show();
    }

    public void addFlowExecutionDetails(UUID id) {
        executionDetailsComponentController.addFlowExecutionDetails(id.toString());
    }

    public void addExecutionToTable(){
        oldExecutionsTableComponentController.addExecutionsToTable();
    }
    @FXML
    void executeAgainActionListener(ActionEvent event) {
        /*mainAppController.prepareToReExecution(oldExecutionsTableComponentController.getSelectedItemID(),
                oldExecutionsTableComponentController.getSelectedItemName());*/
    }
    public void clearAll(){
        executionDetailsComponentController.clearAll();
        oldExecutionsTableComponentController.clearAll();
    }
}
