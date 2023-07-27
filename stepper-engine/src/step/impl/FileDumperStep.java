package step.impl;

import dd.impl.DataDefinitionRegistry;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

public class FileDumperStep extends AbstractStepDefinition {

    public FileDumperStep() {
        super("File Dumper", true);
        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY,"Content", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILE_NAME", DataNecessity.MANDATORY, "Target file path", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "File Creation Result" , DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        String content = context.getDataValue("CONTENT", String.class);
        String filePath = context.getDataValue("FILE_NAME" , String.class);
        StepResult result;


        if(content.isEmpty()) {
            result = StepResult.WARNING;
            context.addLogLine(new LogLine("Step result is Warning. Content is empty.", LocalTime.now()));
            context.addSummeryLine("Step result is Warning. Content is empty.");
        }
        else {
            result = StepResult.SUCCESS;
            context.addSummeryLine("Step result is Success!");
        }
        try {
            context.addLogLine(new LogLine(String.format("About to create file named %s",
                filePath), LocalTime.now()));
            writeToFile(content,filePath);
        } catch (IOException e) {
            context.storeDataValue("RESULT" , "failure: " + e.getMessage());
            context.storeResult(StepResult.FAILURE);
            context.addLogLine(new LogLine(String.format("Step result is Failure. Error: %s",
                    e.getMessage()), LocalTime.now()));
            context.addSummeryLine(String.format("Step result is Failure. Error: %s",
                    e.getMessage()));
            Instant end = Instant.now();
            LocalTime endTime = LocalTime.now();
            context.storeEndTime(endTime);
            Duration duration = Duration.between(start, end);
            context.storeDuration(duration);
            return StepResult.FAILURE;
        }
        context.storeDataValue("RESULT", "SUCCESS");
        context.storeResult(result);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        context.storeEndTime(endTime);
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        return result;
    }

    public void writeToFile(String content, String filePath) throws IOException {
        try(Writer out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), "UTF-8"))) {
            out.write(content);
        }
    }
}
