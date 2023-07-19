package step.impl;

import dd.impl.DataDefinitionRegistry;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CommandLineStep extends AbstractStepDefinition {
    public CommandLineStep() {
        super("Command Line", false);
        addInput(new DataDefinitionDeclarationImpl("COMMAND", DataNecessity.MANDATORY, "Command", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("ARGUMENTS", DataNecessity.OPTIONAL, "Command arguments", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Command output", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        String command = context.getDataValue("COMMAND", String.class);
        String arguments = context.getDataValue("ARGUMENTS", String.class);
        StringBuilder output = new StringBuilder();
        List<String> command2 = new ArrayList<>();
        command2.add("cmd");
        command2.add("/c");
        command2.add(command);

            try {
                if(arguments != null){
                    command2.add(arguments);
                    context.addLogLine(new LogLine("About to invoke " + command + " " + arguments,
                            LocalTime.now()));
                }else{
                    context.addLogLine(new LogLine("About to invoke " + command,
                            LocalTime.now()));
                }

                ProcessBuilder processBuilder = new ProcessBuilder(command2);
                // Start the process
                Process process = processBuilder.start();
                // Read the output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                    output.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        context.storeDataValue("RESULT", output.toString());
        context.addSummeryLine("Step result is Success!");
        context.storeResult(StepResult.SUCCESS);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        context.storeEndTime(endTime);
        return StepResult.SUCCESS;
    }
}
