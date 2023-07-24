package step.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import dd.impl.DataDefinitionRegistry;
import dd.impl.json.JsonData;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

public class ToJsonStep extends AbstractStepDefinition {
    public ToJsonStep() {
        super("To Json", true);
        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY,
                "Content", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.NA,
                "Json representation", DataDefinitionRegistry.JSON));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        StepResult result = StepResult.SUCCESS;

        String content = context.getDataValue("CONTENT", String.class);

        Gson gson = new Gson();
        try {
            Object o = gson.fromJson(content, Object.class);
            context.addLogLine(new LogLine("Content is JSON string. Converting it to json…", LocalTime.now()));
            JsonElement jsonElement = gson.toJsonTree(content);
            JsonData jsonData = new JsonData(jsonElement);
            context.storeDataValue("JSON", jsonData);
            context.addSummeryLine("Success");
        } catch (JsonSyntaxException exception) {
            context.addSummeryLine("Failure: Content is not a valid JSON representation");
           context.addLogLine(new LogLine("Content is not a valid JSON representation", LocalTime.now()));
            result = StepResult.FAILURE;
        } finally {
            context.storeResult(result);
            Instant end = Instant.now();
            LocalTime endTime = LocalTime.now();
            context.storeEndTime(endTime);
            Duration duration = Duration.between(start, end);
            context.storeDuration(duration);
        }
        return result;
    }
}
