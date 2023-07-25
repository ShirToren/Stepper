package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.json.JsonData;
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
        String protocol = context.getDataValue("PROTOCOL", String.class);
        String method = context.getDataValue("METHOD", String.class);
        JsonData body = context.getDataValue("BODY", JsonData.class);
        protocol = "http";

        if(method != null) {
            if ((method.equals("PUT") || method.equals("POST")) && body == null) {
                result = StepResult.FAILURE;
                context.addSummeryLine("Can't send this request without body");
                context.addLogLine(new LogLine("Can't send this request without body", LocalTime.now()));
                context.storeResult(result);
                Instant end = Instant.now();
                LocalTime endTime = LocalTime.now();
                Duration duration = Duration.between(start, end);
                context.storeDuration(duration);
                context.storeEndTime(endTime);
                return result;
            }
        }

        Request request = null;

        String finalUrl = HttpUrl
                .parse(protocol + "://" + address+ "/" + resource)
                .newBuilder()
                .build()
                .toString();
        Request.Builder url = new Request.Builder().url(finalUrl);
        if(method != null) {
            if(method.equals("GET")){
                request = url.build();
            } else if(method.equals("POST")) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.getJsonElement().getAsString());
                request = url.post(requestBody).build();
            } else if(method.equals("PUT")){
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.getJsonElement().getAsString());
                request = url.put(requestBody).build();
            } else if(method.equals("DELETE")){
                if(body != null) {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.getJsonElement().getAsString());
                    request = url.delete(requestBody).build();
                } else {
                    request = url.delete().build();
                }
            }
        } else {
            request = url.build();
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        context.addLogLine(new LogLine("About to invoke http request <request details: \n" + protocol + " | " + (method != null? method : "GET") + " | " + address + " | " + resource + ">\n",
                LocalTime.now()));
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
            }
        });
        return result;
    }
}
