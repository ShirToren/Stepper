package FXML.step.execution.details;

import FXML.main.MainAppController;
import dto.FlowExecutionDTO;
import dto.StepUsageDeclarationDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class StepExecutionDetailsController {
    private MainAppController mainAppController;

    @FXML
    private GridPane stepDetailsGP;
    @FXML
    private Label stepNameLabel;

    @FXML
    private Label DurationLabel;

    @FXML
    private Label resultLabel;

    @FXML
    private Label summaryLabel;

    public void addStepDetails(StepUsageDeclarationDTO step) {
        FlowExecutionDTO currentExecutionDTO = mainAppController.getCurrentExecutionDTO();
        stepNameLabel.setText(step.getName());
        DurationLabel.setText(Long.toString(currentExecutionDTO.getStepsTotalTimes().get(step.getName()).toMillis()));
        resultLabel.setText(currentExecutionDTO.getStepsResults().get(step.getName()).name());
        summaryLabel.setText(currentExecutionDTO.getSummeryLines().get(step.getName()));
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

}

