package step.impl;

import dd.ListData;
import dd.StringList;
import dd.impl.DataDefinitionRegistry;
import dd.impl.list.FileList;
import dd.impl.mapping.Mapping;
import dd.impl.mapping.NumberMapping;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

public class FilesDeleterStep extends AbstractStepDefinition {
    public FilesDeleterStep() {
        super("Files Deleter", false);
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to delete", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA, "Files failed to be deleted", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STATS", DataNecessity.NA, "Deletion summary results", DataDefinitionRegistry.MAPPING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        ListData<String> failedToDelete = new StringList();
        Mapping<Integer, Integer> deletionState = new NumberMapping(0,0);
        StepResult result;

        ListData<File> filesToDelete = context.getDataValue("FILES_LIST", ListData.class);
        context.addLogLine(new LogLine(String.format("About to start delete %d files",
                filesToDelete.getList().size()), LocalTime.now()));
        for(File file : filesToDelete.getList()) {
            if(file.delete()) {
            deletionState.setFirst(deletionState.getFirst() + 1);
             } else {
            failedToDelete.addToList(file.getAbsolutePath());
            context.addLogLine(new LogLine(String.format("Failed to delete file %s",
                    file.getAbsolutePath()), LocalTime.now()));
            deletionState.setSecond(deletionState.getSecond() + 1);
            }
        }

        context.storeDataValue("DELETED_LIST", failedToDelete);
        context.storeDataValue("DELETION_STATS", deletionState);

        if(failedToDelete.getList().size() == 0) {
            result = StepResult.SUCCESS;
            context.addSummeryLine("Step result is Success! All files deleted successfully!");
        } else if (failedToDelete.getList().size() == filesToDelete.getList().size()) {
            result = StepResult.FAILURE;
            context.addSummeryLine("Step result is Failure! All files failed to delete");
            context.addLogLine(new LogLine("Step result is Failure! All files failed to delete", LocalTime.now()));
        } else {
            result = StepResult.WARNING;
            context.addSummeryLine("Step result is Warning. Not all files deleted successfully");
            context.addLogLine(new LogLine("Step result is Warning. Not all files deleted successfully", LocalTime.now()));
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
