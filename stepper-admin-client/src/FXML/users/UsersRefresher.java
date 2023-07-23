package FXML.users;

import FXML.utils.Constants;
import FXML.utils.adapter.UsersMapDeserializer;
import FXML.utils.http.HttpClientUtil;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import impl.UserDTO;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class UsersRefresher extends TimerTask {
    private final Consumer<Map<String, UserDTO>> consumer;

    public UsersRefresher(Consumer<Map<String, UserDTO>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.USERS_LIST)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // httpRequestLoggerConsumer.accept("Something went wrong with Chat Request # " + finalRequestNumber);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonResponse = response.body().string();
                if (response.isSuccessful()) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, UserDTO>>(){}.getType(), new UsersMapDeserializer());
                    Gson gson = gsonBuilder.create();
                    Map<String, UserDTO> users = gson.fromJson(jsonResponse, new TypeToken<Map<String, UserDTO>>(){}.getType());
                    Platform.runLater(() -> {
                        consumer.accept(users);
                    });
                }
            }
        });
    }
}
