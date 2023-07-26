package FXML.old.executions.table;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.adapter.DataInFlowMapDeserializer;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

public class OldExecutionsRefresher extends TimerTask {
    private final Consumer<List<FlowExecutionDTO>> consumer;
    private final SimpleBooleanProperty isManager;

    public OldExecutionsRefresher(Consumer<List<FlowExecutionDTO>> consumer, SimpleBooleanProperty isManager) {
        this.consumer = consumer;
        this.isManager = isManager;
    }

    @Override
    public void run() {
        String endPoint;
        if(isManager.get()) {
            endPoint = Constants.ALL_HISTORY;
        } else {
            endPoint = Constants.HISTORY;
        }
        String finalUrl = HttpUrl
                .parse(endPoint)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonResponse = response.body().string();
                if (response.isSuccessful()) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<DataInFlowDTO, Object>>(){}.getType(), new DataInFlowMapDeserializer());
                    Gson gson = gsonBuilder.create();
                    FlowExecutionDTO[] flowExecutionDTOS = gson.fromJson(jsonResponse, FlowExecutionDTO[].class);
                    List<FlowExecutionDTO> executionsList = Arrays.asList(flowExecutionDTOS);
                    Platform.runLater(() -> {
                        consumer.accept(executionsList);
                    });
                }
            }
        });
    }
}
