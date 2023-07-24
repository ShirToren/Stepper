package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.json.JsonData;
import flow.execution.context.StepExecutionContext;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.time.Instant;
import java.time.LocalTime;

public class JsonDataExtractorStep extends AbstractStepDefinition {
    public JsonDataExtractorStep() {
        super("Json Data Extractor", true);
        addInput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.MANDATORY,
                "Json source", DataDefinitionRegistry.JSON));
        addInput(new DataDefinitionDeclarationImpl("JSON_PATH", DataNecessity.MANDATORY,
                "data", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("VALUE", DataNecessity.NA,
                "Data value", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        StepResult result = StepResult.SUCCESS;

        JsonData json = context.getDataValue("JSON", JsonData.class);
        String jsonPath = context.getDataValue("JSON_PATH", String.class);

        StringBuilder stringBuilder = new StringBuilder();
        return result;
    }
}
