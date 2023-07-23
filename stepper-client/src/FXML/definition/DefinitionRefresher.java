package FXML.definition;

import FXML.definition.model.DefinitionsListWithVersion;
import impl.FlowDefinitionDTO;
import javafx.beans.property.IntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class DefinitionRefresher extends TimerTask {
    private final Consumer<List<FlowDefinitionDTO>> flowsListConsumer;
    private final List<String> roles;
    private final String paramValue;
    private final IntegerProperty definitionVersion;
    private static final Object listLock = new Object();

    public DefinitionRefresher(Consumer<List<FlowDefinitionDTO>> flowsListConsumer, IntegerProperty definitionVersion, List<String> roles) {
        this.flowsListConsumer = flowsListConsumer;
        this.definitionVersion = definitionVersion;
        //synchronized (listLock){
            this.roles = roles;
            this.paramValue = String.join(",", roles);
       // }
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder;
        String finalUrl;
            urlBuilder = HttpUrl.parse(Constants.FLOW_DEFINITIONS_LIST)
                    .newBuilder();
            //urlBuilder.addQueryParameter("roles", paramValue);
            //urlBuilder.addQueryParameter("definitionVersion", String.valueOf(definitionVersion.get()));
            finalUrl = urlBuilder.build().toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                if (response.isSuccessful()) {
                    FlowDefinitionDTO[] flowDefinitionDTOS = GSON_INSTANCE.fromJson(rawBody, FlowDefinitionDTO[].class);
                    //DefinitionsListWithVersion definitionsListWithVersion = GSON_INSTANCE.fromJson(rawBody, DefinitionsListWithVersion.class);
                    //flowsListConsumer.accept(definitionsListWithVersion);
                    flowsListConsumer.accept(Arrays.asList(flowDefinitionDTOS));
                }
            }
        });
    }
}
