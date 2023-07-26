package FXML.main;

import FXML.execution.history.ExecutionHistoryController;
import FXML.roles.RolesManagementController;
import FXML.statistics.StatisticsController;
import FXML.users.UsersManagementController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

public class AdminMainAppController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label filePathLabel;
    @FXML
    private Button loadFileButton;
    @FXML
    private GridPane rolesManagementComponent;
    @FXML
    private RolesManagementController rolesManagementComponentController;
    @FXML
    private GridPane usersManagementComponent;
    @FXML
    private UsersManagementController usersManagementComponentController;

    @FXML
    private BorderPane executionHistoryComponent;
    @FXML
    private ExecutionHistoryController executionHistoryComponentController;

    @FXML
    private GridPane statisticsComponent;
    @FXML
    private StatisticsController statisticsComponentController;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab executionHistoryTab;
    @FXML
    private Tab statisticsTab;
    @FXML
    private BorderPane borderPane;
    private final SimpleStringProperty selectedFileProperty;

    public AdminMainAppController() {
        this.selectedFileProperty = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        if (executionHistoryComponentController != null &&
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
        borderPane.getStylesheets().add("/FXML/css/default.css");
    }

    @FXML
    void loadFileButtonActionListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null && files.size() != 0) {
            try {
                String response = uploadFile(files);
                String filePath = files.get(files.size() - 1).getAbsolutePath();
                if (response.startsWith("The file is valid and fully loaded.")) {
                    selectedFileProperty.set(filePath);
                } else {
                    showErrorDialog("Invalid file.", response);
                }
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

        return response.body().string();
    }

    public void onClose() {
        executionHistoryComponentController.onClose();
        rolesManagementComponentController.closeTimer();
        statisticsComponentController.closeTimer();
        usersManagementComponentController.closeTimer();
        //httpCallShutdownExecutor();
        HttpClientUtil.shutdown();
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
                HttpClientUtil.shutdown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                HttpClientUtil.shutdown();
            }
        });
    }
}
