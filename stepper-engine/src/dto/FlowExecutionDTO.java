package dto;

import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import logs.LogLine;
import java.time.LocalTime;
import java.util.*;

public class FlowExecutionDTO implements DTO{
    private final UUID uuid;
    private final FlowDefinitionDTO flowDefinitionDTO;
    private final String executionResult;
    private final Map<DataInFlowDTO, Object> executionFormalOutputs;
    private final Map<DataInFlowDTO, Object> allExecutionOutputs;
    private final Map<DataInFlowDTO, Object> allExecutionInputs;

    private final long totalTime;

    private final LocalTime startExecutionTime;
    private final String endExecutionTime;

    private final Map<String, Long> stepsTotalTimes;
    private final List<StepUsageDeclarationDTO> executedSteps;
    private final Map<String, String> stepsResults;
    private final Map<String, List<LogLine>> logLines;
    private final Map<String, String> summeryLines;
    private final boolean isFinished;
    private final Map<String, Object> freeInputs;
    private final Map<String, LocalTime> stepsStartTimes;
    private final Map<String, LocalTime> stepsEndTimes;


    public FlowExecutionDTO(FlowExecution flowExecution, FlowDefinitionDTO flowDefinitionDTO) {
        this.uuid = flowExecution.getUuid();
        if(flowExecution.getFlowExecutionResult() != null) {
            this.executionResult = flowExecution.getFlowExecutionResult().name();
        } else { this.executionResult = null; }
        this.flowDefinitionDTO = flowDefinitionDTO;
        if(flowExecution.getTotalTime() != null) {
            this.totalTime = flowExecution.getTotalTime().toMillis();
        } else { this.totalTime = 0; }
            this.startExecutionTime = flowExecution.getStartExecutionTime();
        if(flowExecution.getEndExecutionTime() != null) {
            this.endExecutionTime = flowExecution.getEndExecutionTime().toString();
        } else { this.endExecutionTime = null; }

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

        readStepsData(flowExecution);
        readOutputsData(flowExecution);
        readInputsData(flowExecution);
        readLogsAndSummeryData(flowExecution);

        stepsStartTimes.putAll(flowExecution.getStepsStartTimes());
        stepsEndTimes.putAll(flowExecution.getStepsEndTimes());
    }

    private void readLogsAndSummeryData(FlowExecution flowExecution){
        for (Map.Entry<StepUsageDeclaration, List<LogLine>> entry : flowExecution.getLogLines().entrySet()) {
            this.logLines.put(entry.getKey().getFinalStepName(), entry.getValue());
        }
        for (Map.Entry<StepUsageDeclaration,String> entry : flowExecution.getSummeryLines().entrySet()) {
            this.summeryLines.put(entry.getKey().getFinalStepName(), entry.getValue());
        }
    }

    private void readStepsData(FlowExecution flowExecution){
        for (StepUsageDeclaration step : flowExecution.getExecutedSteps()) {
            this.stepsTotalTimes.put(step.getFinalStepName(), flowExecution.getStepsTotalTimes().get(step.getFinalStepName()).toMillis());
            this.stepsResults.put(step.getFinalStepName(), flowExecution.getStepsResults().get(step.getFinalStepName()).name());
            this.executedSteps.add(new StepUsageDeclarationDTO(step));
        }
    }

    private void readInputsData(FlowExecution flowExecution){
        for (DataInFlowDTO input: this.flowDefinitionDTO.getFlowsInputs()) {
            if (flowExecution.getAllExecutionInputs().containsKey(input.getFinalName())) {
                this.allExecutionInputs.put(input, flowExecution.getAllExecutionInputs().get(input.getFinalName()));
            }
        }
    }

    private void readOutputsData(FlowExecution flowExecution){
        for (DataInFlowDTO dataInFlowDTO : this.flowDefinitionDTO.getFlowsOutputs()) {
            if (flowExecution.getExecutionFormalOutputs().containsKey(dataInFlowDTO.getFinalName())) {
                this.executionFormalOutputs.put(dataInFlowDTO, flowExecution.getExecutionFormalOutputs().get(dataInFlowDTO.getFinalName()));
            }
            if (flowExecution.getAllExecutionOutputs().containsKey(dataInFlowDTO.getFinalName())) {
                this.allExecutionOutputs.put(dataInFlowDTO, flowExecution.getAllExecutionOutputs().get(dataInFlowDTO.getFinalName()));
            }
        }
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

    public Map<String, Long> getStepsTotalTimes() {
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

    public Map<String, String> getStepsResults() {
        return stepsResults;
    }

    public List<StepUsageDeclarationDTO> getExecutedSteps() {
        return executedSteps;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getEndExecutionTime() {
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

    public String getExecutionResult() {
        return executionResult;
    }

    public Map<DataInFlowDTO, Object> getExecutionFormalOutputs() {
        return executionFormalOutputs;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public LocalTime getStartExecutionTime() {
        return startExecutionTime;
    }
}
