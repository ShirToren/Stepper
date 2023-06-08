package flow.execution;

import flow.definition.api.DataInFlow;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import logs.LogLine;
import step.api.StepResult;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class FlowExecution {

    private final UUID uuid;
    private final FlowDefinition flowDefinition;
    private Duration totalTime;
    private FlowExecutionResult flowExecutionResult;

    // lots more data that needed to be stored while flow is being executed...
    private final Map<String, Object> freeInputs;
    private final Map<String, Object> allExecutionOutputs;

    private final Map<String, Object> executionFormalOutputs;
    private final Map<String, Duration> stepsTotalTimes;
    private final Map<String, StepResult> stepsResults;
    private Map<StepUsageDeclaration, List<LogLine>> logLines;
    private Map<StepUsageDeclaration, String> summeryLines;
    private final List<StepUsageDeclaration> executedSteps;
    private LocalTime startExecutionTime;
    private LocalTime endExecutionTime;
    private boolean isFinished;

    ///all outputs?


    public FlowExecution( UUID uuid, FlowDefinition flowDefinition) {
        this.uuid = uuid;
        this.flowDefinition = flowDefinition;
        this.freeInputs = new HashMap<>();
        this.executionFormalOutputs = new HashMap<>();
        this.stepsTotalTimes = new HashMap<>();
        this.stepsResults = new HashMap<>();
        this.allExecutionOutputs = new HashMap<>();
        this.executedSteps = new ArrayList<>();
        initTotalTimesAndResults();
        initAllExecutionOutputs();
        this.summeryLines = new HashMap<>();
        this.logLines = new HashMap<>();
    }

    private void initTotalTimesAndResults(){
        for (StepUsageDeclaration step : flowDefinition.getFlowSteps()) {
            //stepsTotalTimes.put(step.getFinalStepName(), Duration.ZERO);
            stepsResults.put(step.getFinalStepName(), StepResult.FAILURE);
        }
    }

    private void initAllExecutionOutputs(){
        for (DataInFlow output: flowDefinition.getFlowOutputs()) {
            allExecutionOutputs.put(output.getDataInstanceName(), "Not created due to failure in flow");
        }
        for (DataInFlow output: flowDefinition.getFormalOutputsDataInFlow()) {
            executionFormalOutputs.put(output.getDataInstanceName(), "Not created due to failure in flow");
        }
    }

    public List<StepUsageDeclaration> getExecutedSteps() {
        return executedSteps;
    }

    public Map<StepUsageDeclaration, List<LogLine>> getLogLines() {
        return logLines;
    }

    public Map<StepUsageDeclaration, String> getSummeryLines() {
        return summeryLines;
    }

    public Map<String, Object> getAllExecutionOutputs() {
        return allExecutionOutputs;
    }

    public void setStartExecutionTime(LocalTime startExecutionTime) {
        this.startExecutionTime = startExecutionTime;
    }

    public void setEndExecutionTime(LocalTime endExecutionTime) {
        this.endExecutionTime = endExecutionTime;
    }

    public LocalTime getEndExecutionTime() {
        return endExecutionTime;
    }

    public void setLogLines(Map<StepUsageDeclaration, List<LogLine>> logLines) {
        this.logLines = logLines;
    }

    public void setSummeryLines(Map<StepUsageDeclaration, String> summeryLines) {
        this.summeryLines = summeryLines;
    }

    public LocalTime getStartExecutionTime() {
        return startExecutionTime;
    }

    public UUID getUuid() { return uuid; }

    public Map<String, Object> getExecutionFormalOutputs() { return executionFormalOutputs; }

    public Map<String, Object> getFreeInputs() {
        return freeInputs;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public void setFlowExecutionResult(FlowExecutionResult flowResult) {
        this.flowExecutionResult = flowResult;
    }

    public void addFreeInput(String name, Object input) {
        freeInputs.put(name, input);
    }

    public void setTotalTime(Duration totalTime) {
        this.totalTime = totalTime;
    }

    public Duration getTotalTime() {
        return totalTime;
    }

    public Map<String, Duration> getStepsTotalTimes() {
        return stepsTotalTimes;
    }

    public Map<String, StepResult> getStepsResults() {
        return stepsResults;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
