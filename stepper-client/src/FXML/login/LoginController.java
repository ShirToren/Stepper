package FXML.login;

import FXML.main.MainAppController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    private MainAppController mainAppController;
    private Parent root;

    @FXML
    private TextField userNameTF;

    @FXML
    private Button loginButton;
    @FXML
    private BorderPane borderPane;

    @FXML
    public void initialize() throws IOException {
        borderPane.getStylesheets().add("/FXML/css/default.css");
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/FXML/main/mainApp.fxml");
        fxmlLoader.setLocation(url);
        this.root = fxmlLoader.load(url.openStream());
        this.mainAppController = fxmlLoader.getController();
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    @FXML
    void loginButtonActionListener(ActionEvent event) throws IOException {

        String userName = userNameTF.getText();
        if (userName.isEmpty()) {
            showErrorDialog("Error", "User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        //updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                                showErrorDialog("Error", "Something went wrong: " + e.getMessage())
                        //errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                                    showErrorDialog("Error", "Something went wrong: " + responseBody));
                } else {
                    Platform.runLater(() -> {
                        try {
                            mainAppController.updateUserName(userName);
                            switchToMainApp(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
/*        if(userNameTF.getText().isEmpty()) {
            showErrorDialog("Error", "Please enter user name");
            ////check name doesn't exist
        } else {
            switchToMainApp(event);
        }*/
    }

    private void switchToMainApp(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
/*        stage.setOnCloseRequest(event1 -> {
            httpCallShutdownExecutor();
        });*/
        mainAppController.setActive();
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Stepper - Client");
        stage.show();
    }

    private void httpCallShutdownExecutor() {
        String finalUrl = HttpUrl
                .parse(Constants.SHUT_DOWN)
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
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
