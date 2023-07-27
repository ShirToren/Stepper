package step.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import dd.JsonData;
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
import java.util.Arrays;
import java.util.List;

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
        String[] jsonPaths = jsonPath.split("\\|");

        StringBuilder stringBuilder = new StringBuilder();
        for (String path: jsonPaths) {
            String string = null;
            try {
                string = JsonPath.read(json.getJsonElement().getAsString(), path);
            }catch (JsonPathException ex){
                result = StepResult.FAILURE;
                context.addSummeryLine("Error while trying to extract");
                context.addLogLine(new LogLine("Error while trying to extract", LocalTime.now()));
                context.storeResult(result);
                Instant end = Instant.now();
                LocalTime endTime = LocalTime.now();
                context.storeEndTime(endTime);
                Duration duration = Duration.between(start, end);
                context.storeDuration(duration);
                return result;
            }
            if(string != null) {
                context.addLogLine(new LogLine("Extracting data " + path + ". Value: " + string, LocalTime.now()));
                if(stringBuilder.length() != 0){
                    stringBuilder.append(",");
                }
                stringBuilder.append(string);
            }else{
                context.addLogLine(new LogLine("No value found for json path <" + path + ">", LocalTime.now()));
            }
        }
        context.storeDataValue("VALUE", stringBuilder.toString());
        context.addSummeryLine("Success");
        context.storeResult(result);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        context.storeEndTime(endTime);
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        return result;
    }
}
