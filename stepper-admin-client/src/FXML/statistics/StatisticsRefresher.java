package FXML.statistics;

import utils.Constants;
import utils.http.HttpClientUtil;
import impl.StatisticsDTO;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class StatisticsRefresher extends TimerTask {
    private final Consumer<StatisticsDTO> consumer;

    public StatisticsRefresher(Consumer<StatisticsDTO> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.STATISTICS)
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
                    StatisticsDTO statisticsDTO = GSON_INSTANCE.fromJson(jsonResponse, StatisticsDTO.class);
                    Platform.runLater(() -> {
                        consumer.accept(statisticsDTO);
                    });
                }
            }
        });
    }
}
