package step.impl;

import dd.JsonData;
import dd.impl.DataDefinitionRegistry;
import dd.impl.enumeration.EnumeratorData;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import okhttp3.*;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class HttpCallStep extends AbstractStepDefinition {
    public HttpCallStep() {
        super("HTTP Call", false);
        addInput(new DataDefinitionDeclarationImpl("RESOURCE", DataNecessity.MANDATORY, "Resource Name (include query parameters)",
                DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("ADDRESS", DataNecessity.MANDATORY,
                "Domain:Port", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("PROTOCOL", DataNecessity.MANDATORY,
                "protocol", DataDefinitionRegistry.ENUMERATION));
        addInput(new DataDefinitionDeclarationImpl("METHOD", DataNecessity.OPTIONAL,
                "Method", DataDefinitionRegistry.ENUMERATION));
        addInput(new DataDefinitionDeclarationImpl("BODY", DataNecessity.OPTIONAL,
                "Request Body", DataDefinitionRegistry.JSON));
        addOutput(new DataDefinitionDeclarationImpl("CODE", DataNecessity.NA,
                "Response code", DataDefinitionRegistry.NUMBER));
        addOutput(new DataDefinitionDeclarationImpl("RESPONSE_BODY", DataNecessity.NA,
                "Response body", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        StepResult result = StepResult.SUCCESS;

        String resource = context.getDataValue("RESOURCE", String.class);
        String address = context.getDataValue("ADDRESS", String.class);
        EnumeratorData protocolEnumeratorData = new EnumeratorData(context.getDataValue("PROTOCOL", String.class));
        List<String> protocolValues = Arrays.asList("http", "https");
        protocolEnumeratorData.getPossibleValues().addAll(protocolValues);
        String protocol = protocolEnumeratorData.getValue();
        EnumeratorData methodEnumeratorData = new EnumeratorData(context.getDataValue("METHOD", String.class));
        List<String> methodValues = Arrays.asList("GET", "PUT", "POST", "DELETE");
        methodEnumeratorData.getPossibleValues().addAll(methodValues);
        String method = methodEnumeratorData.getValue();
        JsonData body = context.getDataValue("BODY", JsonData.class);

        if(!protocolValues.contains(protocol) || (method != null && !methodValues.contains(method))) {
            result = StepResult.FAILURE;
            context.addSummeryLine("Failure. Got invalid input/s");
            context.addLogLine(new LogLine("Failure. Got invalid input/s", LocalTime.now()));
            context.storeResult(result);
            Instant end = Instant.now();
            LocalTime endTime = LocalTime.now();
            Duration duration = Duration.between(start, end);
            context.storeDuration(duration);
            context.storeEndTime(endTime);
            return result;

        }

        Request request = null;

        String finalUrl = HttpUrl
                .parse(protocol + "://" + address + resource)
                .newBuilder()
                .build()
                .toString();
        RequestBody requestBody;
        Request.Builder url = new Request.Builder().url(finalUrl);
        if(body != null) {
            requestBody = RequestBody.create(MediaType.parse("application/json"), body.getJsonElement().getAsString());
        } else {
            requestBody = null;
        }
        request = url.method(method != null ? method : "GET", requestBody).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        context.addLogLine(new LogLine("About to invoke http request <request details: \n" + protocol + " | " + (method != null? method : "GET") + " | " + address + " | " + resource + ">\n",
                LocalTime.now()));
        Call call = okHttpClient.newCall(request);

        try {
            Response response = call.execute();
            context.addSummeryLine("Success");
            context.addLogLine(new LogLine("Received Response. Status code: <" + response.code() + ">", LocalTime.now()));
            String responseBody = response.body().string();
            context.storeDataValue("CODE", response.code());
            context.storeDataValue("RESPONSE_BODY", responseBody);
            context.storeResult(StepResult.SUCCESS);
            Instant end = Instant.now();
            LocalTime endTime = LocalTime.now();
            Duration duration = Duration.between(start, end);
            context.storeDuration(duration);
            context.storeEndTime(endTime);
        } catch (IOException e) {
            result = StepResult.FAILURE;
            context.addSummeryLine("Can't send this request");
            context.addLogLine(new LogLine("Can't send this request", LocalTime.now()));
            context.storeResult(result);
            Instant end = Instant.now();
            LocalTime endTime = LocalTime.now();
            Duration duration = Duration.between(start, end);
            context.storeDuration(duration);
            context.storeEndTime(endTime);
        }
        return result;
    }
}
