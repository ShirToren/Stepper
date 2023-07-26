package impl;

import api.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class FlowExecutionDTO implements DTO {
    private final UUID uuid;
    private final FlowDefinitionDTO flowDefinitionDTO;
    private final String executionResult;
    private final Map<DataInFlowDTO, Object> executionFormalOutputs;
    private final Map<DataInFlowDTO, Object> allExecutionOutputs;
    private final Map<DataInFlowDTO, Object> allExecutionInputs;

    private final long totalTime;

    private final String startExecutionTime;
    private final String endExecutionTime;

    private final Map<String, Long> stepsTotalTimes;
    private final List<StepUsageDeclarationDTO> executedSteps;
    private final Map<String, String> stepsResults;
    private final Map<String, List<LogLineDTO>> logLines;
    private final Map<String, String> summeryLines;
    private final boolean isFinished;
    private final Map<String, Object> freeInputs;
    private final Map<String, String> stepsStartTimes;
    private final Map<String, String> stepsEndTimes;
    private final String userName;
    private final boolean isManager;
    private final boolean isUsersLastExecution;

    public FlowExecutionDTO(UUID uuid, FlowDefinitionDTO flowDefinitionDTO, String executionResult, Map<DataInFlowDTO, Object> executionFormalOutputs, Map<DataInFlowDTO, Object> allExecutionOutputs, Map<DataInFlowDTO, Object> allExecutionInputs, long totalTime, String startExecutionTime, String endExecutionTime, Map<String, Long> stepsTotalTimes, List<StepUsageDeclarationDTO> executedSteps, Map<String, String> stepsResults, Map<String, List<LogLineDTO>> logLines, Map<String, String> summeryLines, boolean isFinished, Map<String, Object> freeInputs, Map<String, String> stepsStartTimes, Map<String, String> stepsEndTimes, String userName, boolean isManager, boolean isUsersLastExecution) {
        this.uuid = uuid;
        this.flowDefinitionDTO = flowDefinitionDTO;
        this.executionResult = executionResult;
        this.executionFormalOutputs = executionFormalOutputs;
        this.allExecutionOutputs = allExecutionOutputs;
        this.allExecutionInputs = allExecutionInputs;
        this.totalTime = totalTime;
        this.startExecutionTime = startExecutionTime;
        this.endExecutionTime = endExecutionTime;
        this.stepsTotalTimes = stepsTotalTimes;
        this.executedSteps = executedSteps;
        this.stepsResults = stepsResults;
        this.logLines = logLines;
        this.summeryLines = summeryLines;
        this.isFinished = isFinished;
        this.freeInputs = freeInputs;
        this.stepsStartTimes = stepsStartTimes;
        this.stepsEndTimes = stepsEndTimes;
        this.userName = userName;
        this.isManager = isManager;
        this.isUsersLastExecution = isUsersLastExecution;
    }

    public UUID getUuid() {
        return uuid;
    }

    public FlowDefinitionDTO getFlowDefinitionDTO() {
        return flowDefinitionDTO;
    }

    public String getExecutionResult() {
        return executionResult;
    }

    public Map<DataInFlowDTO, Object> getExecutionFormalOutputs() {
        return executionFormalOutputs;
    }

    public Map<DataInFlowDTO, Object> getAllExecutionOutputs() {
        return allExecutionOutputs;
    }

    public Map<DataInFlowDTO, Object> getAllExecutionInputs() {
        return allExecutionInputs;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public String getStartExecutionTime() {
        return startExecutionTime;
    }

    public String getEndExecutionTime() {
        return endExecutionTime;
    }

    public Map<String, Long> getStepsTotalTimes() {
        return stepsTotalTimes;
    }

    public List<StepUsageDeclarationDTO> getExecutedSteps() {
        return executedSteps;
    }

    public Map<String, String> getStepsResults() {
        return stepsResults;
    }

    public Map<String, List<LogLineDTO>> getLogLines() {
        return logLines;
    }

    public Map<String, String> getSummeryLines() {
        return summeryLines;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Map<String, Object> getFreeInputs() {
        return freeInputs;
    }

    public Map<String, String> getStepsStartTimes() {
        return stepsStartTimes;
    }

    public Map<String, String> getStepsEndTimes() {
        return stepsEndTimes;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isFlowName(String name) {
        if(flowDefinitionDTO.getName().equals(name)){
            return true;
        }
        return false;
    }

    public List<StepUsageDeclarationDTO> getOnlyExecutedSteps() {
        List<StepUsageDeclarationDTO> stepsInExecution = flowDefinitionDTO.getSteps();
        List<StepUsageDeclarationDTO> executedSteps = new ArrayList<>();
        for (StepUsageDeclarationDTO step : stepsInExecution) {
            if (stepsTotalTimes.get(step.getName()) != null) {
                executedSteps.add(step);
            }
        }
        return  executedSteps;
    }

    public boolean isManager() {
        return isManager;
    }

    public boolean isUsersLastExecution() {
        return isUsersLastExecution;
    }
}
