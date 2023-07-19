package FXML.execution.details;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.DataInFlowDTO;
import impl.FlowExecutionDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.adapter.MapDeserializer;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ExecutionDetailsRefresher extends TimerTask {
    private final Consumer<FlowExecutionDTO> consumer;
    private final String id;

    public ExecutionDetailsRefresher(Consumer<FlowExecutionDTO> consumer, String id) {
        this.consumer = consumer;
        this.id = id;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.FLOW_EXECUTION)
                .newBuilder()
                .addQueryParameter(Constants.EXECUTION_ID_PARAMETER, id)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                if (response.isSuccessful()) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<DataInFlowDTO, Object>>(){}.getType(), new MapDeserializer());
                    Gson gson = gsonBuilder.create();
                    FlowExecutionDTO executionDTO = gson.fromJson(rawBody, FlowExecutionDTO.class);
                    consumer.accept(executionDTO);
                }
            }
        });
    }
}
