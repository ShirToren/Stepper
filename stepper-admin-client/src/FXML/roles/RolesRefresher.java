package FXML.roles;

import FXML.utils.Constants;
import FXML.utils.adapter.RolesMapDeserializer;
import FXML.utils.adapter.UsersMapDeserializer;
import FXML.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.RoleDefinitionDTO;
import impl.UserDTO;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RolesRefresher extends TimerTask {
    private final Consumer<Map<String, RoleDefinitionDTO>> consumer;

    public RolesRefresher(Consumer<Map<String, RoleDefinitionDTO>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.ROLES_LIST)
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
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, RoleDefinitionDTO>>() {
                    }.getType(), new RolesMapDeserializer());
                    Gson gson = gsonBuilder.create();
                    Map<String, RoleDefinitionDTO> roles = gson.fromJson(jsonResponse, new TypeToken<Map<String, RoleDefinitionDTO>>() {
                    }.getType());
                    Platform.runLater(() -> {
                        consumer.accept(roles);
                    });
                }
            }
        });
    }
}
