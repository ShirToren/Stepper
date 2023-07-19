import FXML.login.LoginController;
import FXML.main.MainAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class StepperApplication extends Application{
////hey
        @Override
        public void start(Stage primaryStage) throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader();
            //URL url = getClass().getResource("/FXML/main/adminMainApp.fxml");
            URL url = getClass().getResource("/FXML/login/login.fxml");
            fxmlLoader.setLocation(url);
            Parent root = fxmlLoader.load(url.openStream());
            LoginController loginController = fxmlLoader.getController();


            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Stepper - Client");
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }

}
