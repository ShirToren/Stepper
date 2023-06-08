package tasks;

import FXML.execution.UIAdapter;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import dto.StepUsageDeclarationDTO;
import flow.execution.FlowExecution;
import javafx.application.Platform;
import javafx.concurrent.Task;
import stepper.management.StepperEngineManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UpdateExecutionDetailsTask extends Task<Boolean> {
    private final UUID id;
    private final StepperEngineManager manager;
    private final FlowExecution flowExecution;
    private final UIAdapter uiAdapter;

    public UpdateExecutionDetailsTask(UUID id, StepperEngineManager manager, UIAdapter uiAdapter) {
        this.id = id;
        this.manager = manager;
        this.uiAdapter = uiAdapter;
        flowExecution = manager.getExecutionByUUID(id);
    }

    @Override
    protected Boolean call() throws Exception {
        FlowExecutionDTO executionDTO;
       while (!flowExecution.isFinished() && manager.getAllFlowExecutionsList().get(0).equals(flowExecution)) {
           executionDTO = manager.getExecutionDTOByUUID(id);
           if(executionDTO.getStartExecutionTime() != null) {
               uiAdapter.updateFlowStartTime(executionDTO.getStartExecutionTime().toString());
           }
           updateProgress(flowExecution.getExecutedSteps().size(),flowExecution.getFlowDefinition().getFlowSteps().size());
           Map<DataInFlowDTO, Object> allExecutionOutputs = executionDTO.getAllExecutionOutputs();
           uiAdapter.clearOutputsItems();
           for (Map.Entry<DataInFlowDTO, Object> entry : allExecutionOutputs.entrySet()) {
               uiAdapter.addNewOutput(entry.getKey().getFinalName());
           }
           List<StepUsageDeclarationDTO> onlyExecutedSteps = manager.getOnlyExecutedSteps(executionDTO);
           uiAdapter.clearStepsItems();
           for (StepUsageDeclarationDTO step: onlyExecutedSteps) {
               uiAdapter.addNewStep(step.getName());
           }
           Thread.sleep(200);
       }
       if(!manager.getAllFlowExecutionsList().get(0).equals(flowExecution)) {
           updateProgress(0,0);
       }
        executionDTO = manager.getExecutionDTOByUUID(id);
        uiAdapter.updateFlowDuration(Long.toString(executionDTO.getTotalTime().toMillis()));
        uiAdapter.updateFlowEndTime(executionDTO.getEndExecutionTime().toString());
        uiAdapter.updateFlowResult(executionDTO.getExecutionResult().name());
       return true;
    }
}

