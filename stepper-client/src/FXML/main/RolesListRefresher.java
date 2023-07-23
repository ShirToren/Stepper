package FXML.main;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class RolesListRefresher extends TimerTask {
    private final Consumer<List<String>> rolesListConsumer;
    private final String userName;

    public RolesListRefresher(Consumer<List<String>> rolesListConsumer, String userName) {
        this.rolesListConsumer = rolesListConsumer;
        this.userName = userName;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.USER_ROLES)
                .newBuilder()
                .addQueryParameter("userName", userName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
               // httpRequestLoggerConsumer.accept("Something went wrong with Chat Request # " + finalRequestNumber);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfRoles = response.body().string();
                if (response.isSuccessful()) {
                    //String jsonArrayOfRoles = response.body().string();
                    //httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);
                    String[] roles = GSON_INSTANCE.fromJson(jsonArrayOfRoles, String[].class);
                    rolesListConsumer.accept(Arrays.asList(roles));
                }
            }
        });

    }
}
