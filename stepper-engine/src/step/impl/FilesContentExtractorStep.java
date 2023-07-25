package step.impl;

import dd.RelationData;
import dd.api.AbstractDataDefinition;
import dd.impl.DataDefinitionRegistry;
import dd.impl.list.ListData;
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

public class FilesContentExtractorStep extends AbstractStepDefinition {
    public FilesContentExtractorStep() {
        super("Files Content Extractor", true);
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to extract", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("LINE", DataNecessity.MANDATORY, "Line number to extract", DataDefinitionRegistry.NUMBER));
        addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA, "Data extraction", DataDefinitionRegistry.RELATION));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        ListData<File> filesToExtract = context.getDataValue("FILES_LIST", ListData.class);
        Integer lineNumber = context.getDataValue("LINE", Integer.class);
        RelationData relation = new RelationData("Number", "Original File Name", "Data Extraction");
        int index = 1;
        StepResult result = StepResult.SUCCESS;

        for (File file : filesToExtract.getList()) {
            context.addLogLine(new LogLine(String.format("About to start work on file %s",
                    file.getAbsolutePath()), LocalTime.now()));
            String line = null;
            try {
                line = LineExtractor.extractLine(file.getAbsolutePath(), lineNumber);
                if (line != null) {
                    relation.addRowByColumnsOrder(Integer.toString(index), file.getName(), line);
                } else {
                    relation.addRowByColumnsOrder(Integer.toString(index), file.getName(), "Not such line");
                    context.addLogLine(new LogLine(String.format("Problem extracting line number %d from file %s",
                            lineNumber,file.getAbsolutePath()), LocalTime.now()));
                }
            } catch (IOException e) {
                context.addLogLine(new LogLine(String.format("Problem extracting line number %d from file %s",
                        lineNumber,file.getAbsolutePath()), LocalTime.now()));
                relation.addRowByColumnsOrder(Integer.toString(index), file.getName(), "File not found");
            }
            index++;
        }
        if(filesToExtract.getList().size() == 0){
            context.addSummeryLine("Step result is Success! But no files to extract.");
        }
        else {
            context.addSummeryLine("Step result is Success!");
        }
        context.storeDataValue("DATA" , relation);
        context.storeResult(result);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        context.storeEndTime(endTime);
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        return  result;
    }
    public static class LineExtractor {
        public static String extractLine(String filePath, int lineNumber) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int currentLine = 1;

            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    reader.close();
                    return line;
                }
                currentLine++;
            }

            reader.close();
            return null; // Line number not found
        }
    }
}
