import FXML.main.AdminMainAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import okhttp3.*;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;

import java.net.URL;

public class AdminApplication extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/FXML/main/adminMainApp.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        AdminMainAppController mainAppController = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> {
            mainAppController.onClose();
        });

        try {
            boolean success = httpCallToConnectAdmin();
            if (success) {
                Scene scene = new Scene(root, 1000, 600);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Stepper - Administrator");
                primaryStage.show();
            }
        } catch (IOException e) {
            showErrorDialog("Error", e.getMessage());
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

    private boolean httpCallToConnectAdmin() throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.CONNECT_ADMIN)
                .newBuilder()
                .build()
                .toString();

        Response response = HttpClientUtil.runSync(finalUrl);
        if(response.code() != 200) {
            showErrorDialog("Error", "Admin is already connected");
            return false;
        }
        return true;
    }
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
