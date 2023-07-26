package FXML.execution.history;
import FXML.execution.details.ExecutionDetailsController;
import FXML.main.MainAppController;
import FXML.old.executions.table.OldExecutionsTableController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import java.util.UUID;

public class ExecutionHistoryController {
    private MainAppController mainAppController;
    @FXML
    private Button executeAgainButton;
    @FXML
    private GridPane oldExecutionsTableComponent;
    @FXML
    private ScrollPane executionDetailsComponent;
    @FXML
    private ExecutionDetailsController executionDetailsComponentController;
    @FXML
    private OldExecutionsTableController oldExecutionsTableComponentController;
    private final SimpleBooleanProperty ableToReRun = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        executeAgainButton.disableProperty().bind(ableToReRun.not());

        //executeAgainButton.disableProperty().bind(oldExecutionsTableComponentController.isExecutionSelected().not());
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
        oldExecutionsTableComponentController.setMainAppController(mainAppController);
        oldExecutionsTableComponentController.setExecutionHistoryController(this);
        executionDetailsComponentController.setMainAppController(mainAppController);
    }

    public void enAbleReRun() {
        ableToReRun.set(true);
    }
    public void disAbleReRun() {
        ableToReRun.set(false);
    }

    public void showOldExecutions(){
        oldExecutionsTableComponentController.show();
    }

    public void addFlowExecutionDetails(UUID id) {
        executionDetailsComponentController.addFlowExecutionDetails(id.toString());
        executionDetailsComponentController.addContinuations(id.toString());
    }

    public void addExecutionToTable(){
        oldExecutionsTableComponentController.addExecutionsToTable();
    }

    public void startRefresher(){
        oldExecutionsTableComponentController.startExecutionHistoryRefresher();
    }

    @FXML
    void executeAgainActionListener(ActionEvent event) {
        mainAppController.prepareToReExecution(oldExecutionsTableComponentController.getSelectedItemID().toString());
    }
    public void clearAll(){
        executionDetailsComponentController.clearAll();
        oldExecutionsTableComponentController.clearAll();
    }

    public void closeTimer() {
        oldExecutionsTableComponentController.closeTimer();
    }
}