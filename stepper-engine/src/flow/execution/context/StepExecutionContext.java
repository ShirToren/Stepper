package flow.execution.context;

import dd.api.DataDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import logs.LogLine;
import step.api.StepDefinition;
import step.api.StepResult;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);

    <T> T getDataValueByFinalName(String dataName, Class<T> expectedDataType);

    boolean storeDataValue(String dataName, Object value);

    void storeDuration(Duration duration);

    void storeExecutedStep();

    void storeResult(StepResult result);

    void storeDataTypes(FlowExecution flowExecution);

    void copyOutputsValuesForCustomMapping(FlowExecution flowExecution, StepDefinition step);

    void setCurrentStep(StepUsageDeclaration currentStep);

    void addLogLine(LogLine logLine);

    void addSummeryLine(String summeryLine);

    Map<StepUsageDeclaration, List<LogLine>> getLogLines();

    Map<StepUsageDeclaration, String> getSummeryLines();
}


        // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line

