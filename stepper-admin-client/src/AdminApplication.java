import FXML.main.AdminMainAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
            mainAppController.getModel().getExecutor().shutdown();
        });

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stepper - Administrator");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
