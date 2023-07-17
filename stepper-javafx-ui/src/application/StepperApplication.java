package application;

import FXML.main.MainAppController;
import flow.definition.api.StepUsageDeclaration;
import flow.definition.api.StepUsageDeclarationImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import step.api.AbstractStepDefinition;
import step.api.StepDefinition;
import step.api.StepResult;
import step.impl.ZipperStep;
import stepper.management.StepperEngineManager;
import java.net.URL;

public class StepperApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/FXML/main/mainApp.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        MainAppController mainAppController = fxmlLoader.getController();

            primaryStage.setOnCloseRequest(event -> {
                mainAppController.getModel().getExecutor().shutdown();
            });

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stepper");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
