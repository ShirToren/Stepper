package FXML.execution.history;
import FXML.execution.details.ExecutionDetailsController;
import FXML.main.AdminMainAppController;
import FXML.old.executions.table.OldExecutionsTableController;
import impl.FlowExecutionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.UUID;

public class ExecutionHistoryController {

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
    }

    public void setMainAppController(AdminMainAppController mainAppController) {
        oldExecutionsTableComponentController.setMainAppController(mainAppController);
        oldExecutionsTableComponentController.setExecutionHistoryController(this);
        executionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void showOldExecutions(){
        oldExecutionsTableComponentController.show();
    }

    public void addFlowExecutionDetails(UUID id, List<FlowExecutionDTO> finishedExecutions) {
        executionDetailsComponentController.addFlowExecutionDetails(id.toString(), finishedExecutions);
    }

    public void addExecutionToTable(){
        oldExecutionsTableComponentController.addExecutionsToTable();
    }

    public void clearAll(){
        executionDetailsComponentController.clearAll();
        oldExecutionsTableComponentController.clearAll();
    }

    public void onClose(){
        oldExecutionsTableComponentController.closeTimer();
    }
}
