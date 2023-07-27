package step.impl;

import dd.impl.DataDefinitionRegistry;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

public class SpendSomeTimeStep extends AbstractStepDefinition {
    public SpendSomeTimeStep() {
        super("Spend Some Time", true);
        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY, "Total sleeping time (sec)", DataDefinitionRegistry.NUMBER));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        int numOfSec = context.getDataValue("TIME_TO_SPEND", Integer.class);
        StepResult result = StepResult.SUCCESS;
        if (numOfSec <= 0) {
            result = StepResult.FAILURE;
            context.addLogLine(new LogLine(String.format("Can't sleep for %d seconds :(", numOfSec), LocalTime.now()));
            context.addSummeryLine("Step result is Failure because number of seconds to sleep is less then 1");
        } else {
            context.addLogLine(new LogLine(String.format("About to sleep for %d seconds..." , numOfSec), LocalTime.now()));
            try {
                Thread.sleep(numOfSec * 1000L);
                context.addLogLine(new LogLine("Done sleeping...", LocalTime.now()));
                context.addSummeryLine("Step result is Success!");
            } catch (InterruptedException e) {
                result = StepResult.FAILURE;
            }
        }
        context.storeResult(result);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        context.storeEndTime(endTime);
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        return result;
    }
}
