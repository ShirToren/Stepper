package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.relation.RelationData;
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

public class PropertiesExporterStep extends AbstractStepDefinition {
    public PropertiesExporterStep() {
        super("Properties Exporter", true);
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Properties export result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        RelationData relationData = context.getDataValue("SOURCE" , RelationData.class);
        String propertiesData = new String();
        StepResult result = StepResult.SUCCESS;

        context.addLogLine(new LogLine(String.format("About to process %d lines of data",
                relationData.getNumOfRows()), LocalTime.now()));
        for (int i = 0; i < relationData.getNumOfRows(); i++) {
            for (int j = 0; j < relationData.getColumns().size(); j++) {
                List<String> rowData = relationData.getRowDataByColumnsOrder(i);
                propertiesData = propertiesData + "row-" + (i+1) + "." + relationData.getColumns().get(j) + "=" + rowData.get(j) + " ";
            }
            propertiesData = propertiesData.substring(0, propertiesData.length() - 1) + "\n";
        }
        context.addLogLine(new LogLine(String.format("Extracted total of %d",
                relationData.getNumOfRows()), LocalTime.now()));

        if(relationData.getNumOfRows() == 0) {
            result = StepResult.WARNING;
            context.addLogLine(new LogLine("Step result is Warning. Relation source is empty.", LocalTime.now()));
            context.addSummeryLine("Step result is Warning. Relation source is empty.");

        }else {
            context.addSummeryLine("Step result is Success!");
        }
        context.storeDataValue("RESULT" , propertiesData);
        context.storeResult(result);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        return result;
    }
}
