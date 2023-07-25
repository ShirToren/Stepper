package step.impl;

import dd.RelationData;
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
import java.util.List;

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
        super("CSV Exporter", true);
        addInput(new DataDefinitionDeclarationImpl("SOURCE" , DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result" , DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        RelationData relationData = context.getDataValue("SOURCE" , RelationData.class);
        String CSVData = new String();
        StepResult result = StepResult.SUCCESS;

        context.addLogLine(new LogLine(String.format("About to process %d lines of data",
                relationData.getNumOfRows()), LocalTime.now()));
        for (String col: relationData.getColumns()) {
            CSVData = CSVData + col + ", ";
        }
        CSVData = CSVData.substring(0, CSVData.length() -2) + "\n";
        for (int i = 0; i < relationData.getNumOfRows(); i++) {
            List<String> rowData = relationData.getRowDataByColumnsOrder(i);
            for (String data: rowData) {
                CSVData = CSVData + data + ", ";
            }
            CSVData = CSVData.substring(0, CSVData.length() -2) + "\n";
        }

        if(relationData.getNumOfRows() == 0){
            result = StepResult.WARNING;
            context.addLogLine(new LogLine("Step result is Warning. Relation source is empty.", LocalTime.now()));
            context.addSummeryLine("Step result is Warning. Relation source is empty.");
        }
        else {
            context.addSummeryLine("Step result is Success!");
        }
        context.storeDataValue("RESULT", CSVData);
        context.storeResult(result);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        context.storeEndTime(endTime);
        return result;
    }
}
