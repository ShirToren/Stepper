package FXML.main;

import FXML.execution.history.ExecutionHistoryController;
import FXML.statistics.StatisticsController;
import dto.XMLDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stepper.management.StepperEngineManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.*;

public class AdminMainAppController {
    private StepperEngineManager model = new StepperEngineManager();
    @FXML
    private Label nameLabel;
    @FXML
    private Label filePathLabel;
    @FXML
    private Button loadFileButton;

    @FXML private BorderPane executionHistoryComponent;
    @FXML private ExecutionHistoryController executionHistoryComponentController;

    @FXML private GridPane statisticsComponent;
    @FXML private StatisticsController statisticsComponentController;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab executionHistoryTab;
    @FXML
    private Tab statisticsTab;
    private final SimpleStringProperty selectedFileProperty;

    public AdminMainAppController() {
        this.selectedFileProperty = new SimpleStringProperty();
    }

    public StepperEngineManager getModel() {
        return model;
    }
    @FXML
    public void initialize() {
        if (  executionHistoryComponentController != null &&
                statisticsComponentController != null) {
            executionHistoryComponentController.setMainAppController(this);
            statisticsComponentController.setMainAppController(this);
        }
        filePathLabel.textProperty().bind(selectedFileProperty);
        executionHistoryTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                executionHistoryComponentController.showOldExecutions();
            }
        });
        statisticsTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                statisticsComponentController.show();
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
    /*            if(oldTab.equals(flowExecutionTab) || newTab.equals((flowExecutionTab))) {
                    flowsExecutionComponentController.clearAll();
                }*/
                if(oldTab.equals(executionHistoryTab)) {
                    executionHistoryComponentController.clearAll();
                }
            }
        });

/*        borderPane.getStylesheets().add("/FXML/css/default.css");
        // Add items to the ChoiceBox
        cssChoiceBox.getItems().addAll("Default skin", "Skin 2", "Skin 3");

        // Set a default selection
        //cssChoiceBox.setValue("Skin 1");

        // Add a listener to handle selection changes
        cssChoiceBox.setOnAction(event -> {
            String selectedValue = cssChoiceBox.getValue();
            changeCss(selectedValue);
        });*/
    }

    @FXML
    void loadFileButtonActionListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //File selectedFile = fileChooser.showOpenDialog(stage);
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files.size() != 0) {
            try {
                String response = uploadFile(files);
                String filePath = files.get(files.size() - 1).getAbsolutePath();
            if(response.startsWith("The file is valid and fully loaded.")) {
                //clearAll();
                selectedFileProperty.set(filePath);
            } else {
                showErrorDialog("Invalid file.", response);
            }
                //flowsDefinitionComponentController.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private String uploadFile(List<File> files) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String BASE_URL = "http://localhost:8080/stepper_web";
        String RESOURCE = "/upload-file";
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (File xmlFile : files) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/xml"), xmlFile);
            multipartBuilder.addFormDataPart("xmlFiles", xmlFile.getName(), fileBody);
        }

        RequestBody requestBody = multipartBuilder.build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(requestBody)
                .build();


        Call call = client.newCall(request);

        Response response = call.execute();

        return  response.body().string();
       // System.out.println(response.body().string());
    }
}
