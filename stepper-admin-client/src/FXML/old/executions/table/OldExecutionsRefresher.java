package FXML.old.executions.table;

import FXML.utils.Constants;
import FXML.utils.adapter.DataInFlowMapDeserializer;
import FXML.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

public class OldExecutionsRefresher extends TimerTask {
    private final Consumer<List<FlowExecutionDTO>> consumer;

    public OldExecutionsRefresher(Consumer<List<FlowExecutionDTO>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.ALL_HISTORY)
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
