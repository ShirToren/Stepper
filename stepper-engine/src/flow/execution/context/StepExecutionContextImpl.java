package flow.execution.context;

import dd.api.DataDefinition;
import dd.api.DataDirection;
import flow.definition.api.DataInFlow;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import logs.LogLine;
import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;
import step.api.StepResult;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {

    private final Map<String, Object> dataValues;
    private final Map<String, DataDefinition> dataTypes;
    private final FlowExecution flowExecution;
    private StepUsageDeclaration currentStep;
    private final Map<StepUsageDeclaration, List<LogLine>> logLines;
    private final Map<StepUsageDeclaration, String> summeryLines;



    public StepExecutionContextImpl(FlowExecution flowExecution) {
        this.dataValues = new HashMap<>();
        this.dataTypes = new HashMap<>();
        this.flowExecution = flowExecution;
        this.logLines = new HashMap<>();
        this.summeryLines = new HashMap<>();
        initLogsAndSummeryLines();
    }
    private void initLogsAndSummeryLines(){
        for (StepUsageDeclaration step : flowExecution.getFlowDefinition().getFlowSteps()) {
            logLines.put(step, new ArrayList<>());
            summeryLines.put(step, "Step is failure because some step before is failure.");
        }
    }
    @Override
    public void setCurrentStep(StepUsageDeclaration currentStep) {
        this.currentStep = currentStep;
    }

    @Override
    public void addLogLine(LogLine logLine) {
/*        if(!logLines.containsKey(currentStep)) {
            logLines.put(currentStep, new ArrayList<>());
        }
        logLines.get(currentStep).add(logLine);*/
        if(!flowExecution.getLogLines().containsKey(currentStep)) {
            flowExecution.getLogLines().put(currentStep, new ArrayList<>());
        }
        flowExecution.getLogLines().get(currentStep).add(logLine);
    }

    @Override
    public void addSummeryLine(String summeryLine) {
        //summeryLines.put(currentStep, summeryLine);
        flowExecution.getSummeryLines().put(currentStep, summeryLine);
    }

    @Override
    public void storeDataTypes(FlowExecution flowExecution) {
        List<StepUsageDeclaration> flowSteps = flowExecution.getFlowDefinition().getFlowSteps();
        String dataInstanceName;

        for(StepUsageDeclaration step : flowSteps) {
            for(DataDefinitionDeclaration input : step.getStepDefinition().inputs()) {
                dataInstanceName = flowExecution.getFlowDefinition().
                        findDataInstanceName(input.getName(), step.getFinalStepName());
                storeOriginalAndCurrentTypes(input.getName(),
                        dataInstanceName, input.dataDefinition());
            }
            for(DataDefinitionDeclaration output : step.getStepDefinition().outputs()) {
                dataInstanceName = flowExecution.getFlowDefinition().
                        findDataInstanceName(output.getName(), step.getFinalStepName());
                storeOriginalAndCurrentTypes(output.getName(),
                        dataInstanceName, output.dataDefinition());
            }
        }
    }

    @Override
    public Map<StepUsageDeclaration, List<LogLine>> getLogLines() {
        return logLines;
    }

    @Override
    public Map<StepUsageDeclaration, String> getSummeryLines() {
        return summeryLines;
    }

    private void storeOriginalAndCurrentTypes(String dataOriginalInstanceName, String dataInstanceName, DataDefinition dataDefinition) {
        dataTypes.put(dataOriginalInstanceName, dataDefinition);
        dataTypes.put(dataInstanceName, dataDefinition);
    }


    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) {
        // assuming that from the data name we can get to its data definition
        DataDefinition theExpectedDataDefinition = dataTypes.get(dataName);

        if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            String finalDataName = flowExecution.getFlowDefinition().findDataInstanceName(dataName, currentStep.getFinalStepName());
            Object aValue = dataValues.get(finalDataName);
            // what happens if it does not exist ?

            return expectedDataType.cast(aValue);
        } else {
            // error handling of some sort...
        }

        return null;
    }

    @Override
    public <T> T getDataValueByFinalName(String dataName, Class<T> expectedDataType) {
        DataDefinition theExpectedDataDefinition = dataTypes.get(dataName);

        if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            Object aValue = dataValues.get(dataName);
            // what happens if it does not exist ?

            return expectedDataType.cast(aValue);
        } else {
            // error handling of some sort...
        }

        return null;
    }

    @Override
    public boolean storeDataValue(String dataName, Object value) {
        // assuming that from the data name we can get to its data definition
        DataDefinition theData = dataTypes.get(dataName);

        // we have the DD type so we can make sure that its from the same type
        if (theData.getType().isAssignableFrom(value.getClass())) {
            String finalDataName = flowExecution.getFlowDefinition().findDataInstanceName(dataName, currentStep.getFinalStepName());
            dataValues.put(finalDataName, value);
        } else {
            //error handling of some sort...
        }

        return false;
    }

    @Override
    public void storeDuration(Duration duration) {
        flowExecution.getStepsTotalTimes().put(currentStep.getFinalStepName(), duration);
    }

    @Override
    public void storeStartTime(LocalTime startTime) {
        flowExecution.getStepsStartTimes().put(currentStep.getFinalStepName(), startTime);
    }

    @Override
    public void storeEndTime(LocalTime endTime) {
        flowExecution.getStepsEndTimes().put(currentStep.getFinalStepName(), endTime);
    }

    @Override
    public void storeExecutedStep() {
        flowExecution.getExecutedSteps().add(currentStep);
    }

    @Override
    public void storeResult(StepResult result) {
        flowExecution.getStepsResults().put(currentStep.getFinalStepName(), result);
    }


    @Override
    public void copyOutputsValuesForCustomMapping(FlowExecution flowExecution, StepDefinition step) {
        for (DataInFlow dataInFlow : flowExecution.getFlowDefinition().getAllDataInFlow()) {
            if(dataInFlow.getOwnerStepUsageDeclaration().getFinalStepName().
                    equals(step.getName()) &&
            dataInFlow.getDataDirection() == DataDirection.OUTPUT) {
                for (DataInFlow target: dataInFlow.getTargetDataInFlow()) {
                    dataValues.put(target.getDataInstanceName(),
                            dataValues.get(dataInFlow.getDataInstanceName()));
                }
            }
        }
    }
}
