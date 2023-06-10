package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.file.FileData;
import dd.impl.list.FileList;
import dd.impl.list.ListData;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectFilesInFolder extends AbstractStepDefinition {
    public CollectFilesInFolder() {
        super("Collect Files In Folder", true);
        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.NUMBER));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        String folderPathString = context.getDataValue("FOLDER_NAME", String.class);
        String filter = context.getDataValue("FILTER", String.class);
        List<File> filesList = new ArrayList<>();
        StepResult result = StepResult.FAILURE;

        try
        {
            Path folderPath = Paths.get(folderPathString);
            if(Files.exists(folderPath) && Files.isDirectory(folderPath)) {
                context.addLogLine(new LogLine(String.format("Reading folder %s content with filter %s",
                        folderPath.toString(), filter != null? filter: "no filter"),LocalTime.now()));
                File folderFile = new File(folderPathString);
                File[] files = folderFile.listFiles();
                Stream<File> stream = Stream.of(files);
                if (filter != null) {
                    filesList = stream.filter(file -> file.isFile() && file.getName().endsWith(filter)).
                            collect(Collectors.toList());
                } else {
                    filesList = stream.filter(file -> file.isFile()).
                            collect(Collectors.toList());
                }
                context.addLogLine(new LogLine(String.format("Found %d files in folder matching the filter",
                        filesList.size()),LocalTime.now()));
                if (filesList.size() == 0) {
                    result = StepResult.WARNING;
                    context.addSummeryLine("Step result is Warning because no files were collected");
                    context.addLogLine(new LogLine("No files were collected.", LocalTime.now()));
                } else {
                    result = StepResult.SUCCESS;
                    context.addSummeryLine("Step result is Success!");
                }
            } else {
                if(!Files.exists(folderPath))
                {
                    context.addLogLine(new LogLine("The folder doesn't exist.", LocalTime.now()));
                    context.addSummeryLine("Step result is Failure because the folder doesn't exist.");
                } else {
                    context.addLogLine(new LogLine("Step result is Failure because the path is not a folder.", LocalTime.now()));
                }
                context.storeResult(StepResult.FAILURE);
                Instant end = Instant.now();
                LocalTime endTime = LocalTime.now();
                context.storeEndTime(endTime);
                Duration duration = Duration.between(start, end);
                context.storeDuration(duration);
                return StepResult.FAILURE;
            }
        } catch (InvalidPathException ex) {
            context.addLogLine(new LogLine("The folder doesn't exist.", LocalTime.now()));
            context.addSummeryLine("Step result is Failure because the folder doesn't exist.");
            context.storeResult(StepResult.FAILURE);
            Instant end = Instant.now();
            LocalTime endTime = LocalTime.now();
            context.storeEndTime(endTime);
            Duration duration = Duration.between(start, end);
            context.storeDuration(duration);
            return StepResult.FAILURE;
        }

        ListData<File> listData = new FileList(filesList);

        context.storeDataValue("FILES_LIST", listData);
        context.storeDataValue("TOTAL_FOUND", listData.getList().size());
        context.storeResult(result);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        context.storeEndTime(endTime);
        return result;
    }
}
