package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.list.ListData;
import dd.impl.relation.RelationData;
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

public class FilesRenamerStep extends AbstractStepDefinition {
    private File renamedFile;
    public FilesRenamerStep() {
        super("Files Renamer", false);
        addInput(new DataDefinitionDeclarationImpl("FILES_TO_RENAME", DataNecessity.MANDATORY, "Files to rename", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL, "Add this prefix", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL, "Append this suffix", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT", DataNecessity.NA, "Rename operation summary", DataDefinitionRegistry.RELATION));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        ListData<File> filesToRename = context.getDataValue("FILES_TO_RENAME", ListData.class);
        String prefix = context.getDataValue("PREFIX", String.class);
        String suffix = context.getDataValue("SUFFIX", String.class);
        RelationData relation = new RelationData("Number", "Original File Name", "New File Name");
        int index = 1;
        StepResult result = StepResult.SUCCESS;
        StringBuilder notRename = new StringBuilder();

        context.addLogLine(new LogLine(String.format("About to start rename %d files. Adding prefix: %s; adding suffix: %s",
                filesToRename.getList().size(),
                prefix != null? prefix : "no prefix",
                suffix != null? suffix : "no suffix"), LocalTime.now()));
        for (File file : filesToRename.getList()) {
            String originalName = file.getName();
            renamedFile = file;
            if (prefix != null) {
                if (!addPrefixToFileName(file, prefix)) {
                    result = StepResult.WARNING;
                    notRename.append(file.getAbsolutePath());
                    notRename.append("\n");
                    context.addLogLine(new LogLine(String.format("Problem renaming file %s",
                            file.getAbsolutePath()), LocalTime.now()));
                }
            }
            if (suffix != null) {
                if (!addSuffixToFileName(file, suffix)) {
                    result = StepResult.WARNING;
                    notRename.append(file.getAbsolutePath());
                    notRename.append("\n");
                    context.addLogLine(new LogLine(String.format("Problem renaming file %s",
                            file.getAbsolutePath()), LocalTime.now()));
                }
            }
            relation.addRowByColumnsOrder(Integer.toString(index), originalName, renamedFile.getName());
            index++;
        }
        if(result.equals(StepResult.WARNING)) {
            context.addSummeryLine("Step result is Warning. Can't rename files: " + notRename.toString());
        }
        else {
            if(filesToRename.getList().size() == 0) {
                context.addSummeryLine("Step result is Success! No files to rename.!");
            }
            else {
                context.addSummeryLine("Step result is Success!");
            }
        }
        context.storeDataValue("RENAME_RESULT", relation);
        context.storeResult(result);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        return result;
    }

    private boolean addPrefixToFileName(File file, String prefix){
        String newName;
        String originalName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\") + 1, file.getAbsolutePath().length());
        newName = prefix.concat(originalName);
        String newPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\") + 1).concat(newName);
        File rename = new File(newPath);
        if(file.renameTo(rename)) {
            renamedFile = rename;
            return true;
        } else { return  false; }
    }

    private boolean addSuffixToFileName(File file, String suffix){
        String newName;
        File fileToRename = renamedFile != null ? renamedFile : file;
        int dotIndex = fileToRename.getAbsolutePath().lastIndexOf(".");
        newName = fileToRename.getAbsolutePath().substring(0, dotIndex) + suffix + fileToRename.getAbsolutePath().substring(dotIndex);
        File rename = new File(newName);
        if(fileToRename.renameTo(rename)) {
            renamedFile = rename;
            return true;
        } else { return false; }
    }
}
