package tasks;

import FXML.execution.UIAdapter;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import dto.StepUsageDeclarationDTO;
import flow.execution.FlowExecution;
import javafx.application.Platform;
import javafx.concurrent.Task;
import step.api.DataNecessity;
import stepper.management.StepperEngineManager;
import sun.management.VMOptionCompositeData;

import java.util.ArrayList;
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
                if (executionDTO.getStartExecutionTime() != null) {
                    uiAdapter.updateFlowStartTime(executionDTO.getStartExecutionTime().toString());
                }
                uiAdapter.updateFlowName(executionDTO.getFlowDefinitionDTO().getName());
                uiAdapter.updateFlowID(executionDTO.getUuid().toString());
                updateProgress(flowExecution.getExecutedSteps().size(), flowExecution.getFlowDefinition().getFlowSteps().size());
                Map<DataInFlowDTO, Object> allExecutionOutputs = executionDTO.getAllExecutionOutputs();
                showFreeInputsDetails(id);
                uiAdapter.clearOutputsItems();
                for (Map.Entry<DataInFlowDTO, Object> entry : allExecutionOutputs.entrySet()) {
                    if (!entry.getValue().equals("Not created due to failure in flow")) {
                        uiAdapter.addNewOutput(entry);
                    }
                }
                List<StepUsageDeclarationDTO> onlyExecutedSteps = manager.getOnlyExecutedSteps(executionDTO);
                uiAdapter.clearStepsItems();
                for (StepUsageDeclarationDTO step : onlyExecutedSteps) {
                    uiAdapter.addNewStep(step.getName());
                }
                Thread.sleep(200);
            }

            executionDTO = manager.getExecutionDTOByUUID(id);
            uiAdapter.updateFlowStartTime(executionDTO.getStartExecutionTime().toString());
            uiAdapter.clearStepsItems();
            for (StepUsageDeclarationDTO step : manager.getOnlyExecutedSteps(executionDTO)) {
            uiAdapter.addNewStep(step.getName());
            }
            uiAdapter.updateFlowDuration(Long.toString(executionDTO.getTotalTime().toMillis()));
            uiAdapter.updateFlowEndTime(executionDTO.getEndExecutionTime().toString());
            uiAdapter.updateFlowResult(executionDTO.getExecutionResult().name());
            uiAdapter.clearOutputsItems();
            for (Map.Entry<DataInFlowDTO, Object> entry : executionDTO.getAllExecutionOutputs().entrySet()) {
                uiAdapter.addNewOutput(entry);
            }

        updateProgress(0,0);
       return true;
    }

    private void showFreeInputsDetails(UUID id) {
        uiAdapter.clearInputsItems();
        Map<String, Object> actualFreeInputs = manager.getActualFreeInputsList(id);
        List<DataInFlowDTO> optionalInput = new ArrayList<>();
        List<DataInFlowDTO> freeInputs = manager.getExecutionDTOByUUID(id).getFlowDefinitionDTO().getFreeInputs();
        for (DataInFlowDTO freeInput : freeInputs) {
            if (freeInput.getDataNecessity().equals(DataNecessity.MANDATORY) &&
                    actualFreeInputs.containsKey(freeInput.getFinalName())) {
                uiAdapter.addNewInput(freeInput.getFinalName());
            } else if (actualFreeInputs.containsKey(freeInput.getFinalName())) {
                optionalInput.add(freeInput);
            }
        }
        for (DataInFlowDTO optional : optionalInput) {
            uiAdapter.addNewInput(optional.getFinalName());
        }
    }
}

