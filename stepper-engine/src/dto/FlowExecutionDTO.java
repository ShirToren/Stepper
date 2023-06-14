package dto;

import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import flow.execution.FlowExecutionResult;
import logs.LogLine;
import step.api.StepResult;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class FlowExecutionDTO implements DTO{
    private final UUID uuid;
    private final FlowDefinitionDTO flowDefinitionDTO;
    private final FlowExecutionResult executionResult;
    private final Map<DataInFlowDTO, Object> executionFormalOutputs;
    private final Map<DataInFlowDTO, Object> allExecutionOutputs;
    private final Map<DataInFlowDTO, Object> allExecutionInputs;

    private final Duration totalTime;

    private final LocalTime startExecutionTime;
    private final LocalTime endExecutionTime;

    private final Map<String, Duration> stepsTotalTimes;
    private final List<StepUsageDeclarationDTO> executedSteps;
    private final Map<String, StepResult> stepsResults;
    private Map<String, List<LogLine>> logLines;
    private Map<String, String> summeryLines;
    private boolean isFinished;
    private final Map<String, Object> freeInputs;
    private final Map<String, LocalTime> stepsStartTimes;
    private final Map<String, LocalTime> stepsEndTimes;


    public FlowExecutionDTO(FlowExecution flowExecution, FlowDefinitionDTO flowDefinitionDTO) {
        this.uuid = flowExecution.getUuid();
        this.executionResult = flowExecution.getFlowExecutionResult();
        this.flowDefinitionDTO = flowDefinitionDTO;
        this.totalTime = flowExecution.getTotalTime();
        this.startExecutionTime = flowExecution.getStartExecutionTime();
        this.endExecutionTime = flowExecution.getEndExecutionTime();
        this.executionFormalOutputs = new HashMap<>();
        this.allExecutionOutputs = new HashMap<>();
        this.allExecutionInputs = new HashMap<>();
        this.stepsTotalTimes = new HashMap<>();
        this.stepsResults = new HashMap<>();
        this.logLines = new HashMap<>();
        this.summeryLines = new HashMap<>();
        this.executedSteps = new ArrayList<>();
        this.freeInputs = new HashMap<>(flowExecution.getFreeInputs());
        this.isFinished = flowExecution.isFinished();
        this.stepsStartTimes = new HashMap<>();
        this.stepsEndTimes = new HashMap<>();
        ///for (StepUsageDeclarationDTO step : flowDefinitionDTO.getSteps())
        for (StepUsageDeclaration step : flowExecution.getExecutedSteps()) {
            stepsTotalTimes.put(step.getFinalStepName(), flowExecution.getStepsTotalTimes().get(step.getFinalStepName()));
            stepsResults.put(step.getFinalStepName(), flowExecution.getStepsResults().get(step.getFinalStepName()));
            executedSteps.add(new StepUsageDeclarationDTO(step));
        }
        for (DataInFlowDTO dataInFlowDTO : flowDefinitionDTO.getFlowsOutputs()) {
            if (flowExecution.getExecutionFormalOutputs().containsKey(dataInFlowDTO.getFinalName())) {
                executionFormalOutputs.put(dataInFlowDTO, flowExecution.getExecutionFormalOutputs().get(dataInFlowDTO.getFinalName()));
            }
            if (flowExecution.getAllExecutionOutputs().containsKey(dataInFlowDTO.getFinalName())) {
                allExecutionOutputs.put(dataInFlowDTO, flowExecution.getAllExecutionOutputs().get(dataInFlowDTO.getFinalName()));
            }
        }
        for (DataInFlowDTO input: flowDefinitionDTO.getFlowsInputs()) {
            if (flowExecution.getAllExecutionInputs().containsKey(input.getFinalName())) {
                allExecutionInputs.put(input, flowExecution.getAllExecutionInputs().get(input.getFinalName()));
            }
        }
        for (Map.Entry<StepUsageDeclaration, List<LogLine>> entry : flowExecution.getLogLines().entrySet()) {
            logLines.put(entry.getKey().getFinalStepName(), entry.getValue());
        }
        for (Map.Entry<StepUsageDeclaration,String> entry : flowExecution.getSummeryLines().entrySet()) {
            summeryLines.put(entry.getKey().getFinalStepName(), entry.getValue());
        }
        stepsStartTimes.putAll(flowExecution.getStepsStartTimes());
        stepsEndTimes.putAll(flowExecution.getStepsEndTimes());

/*        for (StepUsageDeclaration step: flowExecution.getExecutedSteps()) {
            executedSteps.add(new StepUsageDeclarationDTO(step));
        }*/

    }

    public boolean isFlowName(String name) {
        if(flowDefinitionDTO.getName().equals(name)){
            return true;
        }
        return false;
    }

    public Map<String, List<LogLine>> getLogLines() {
        return logLines;
    }

    public Map<String, String> getSummeryLines() {
        return summeryLines;
    }

    public Map<String, Duration> getStepsTotalTimes() {
        return stepsTotalTimes;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Map<String, Object> getFreeInputs() {
        return freeInputs;
    }

    public Map<String, LocalTime> getStepsStartTimes() {
        return stepsStartTimes;
    }

    public Map<String, LocalTime> getStepsEndTimes() {
        return stepsEndTimes;
    }

    public Map<String, StepResult> getStepsResults() {
        return stepsResults;
    }

    public List<StepUsageDeclarationDTO> getExecutedSteps() {
        return executedSteps;
    }

    public UUID getUuid() {
        return uuid;
    }

    public LocalTime getEndExecutionTime() {
        return endExecutionTime;
    }

    public FlowDefinitionDTO getFlowDefinitionDTO() {
        return flowDefinitionDTO;
    }

    public Map<DataInFlowDTO, Object> getAllExecutionOutputs() {
        return allExecutionOutputs;
    }

    public Map<DataInFlowDTO, Object> getAllExecutionInputs() {
        return allExecutionInputs;
    }

    public FlowExecutionResult getExecutionResult() {
        return executionResult;
    }

    public Map<DataInFlowDTO, Object> getExecutionFormalOutputs() {
        return executionFormalOutputs;
    }

    public Duration getTotalTime() {
        return totalTime;
    }

    public LocalTime getStartExecutionTime() {
        return startExecutionTime;
    }
}
