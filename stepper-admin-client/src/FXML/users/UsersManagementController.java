package FXML.users;

import utils.Constants;
import utils.adapter.RolesMapDeserializer;
import utils.http.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import impl.RoleDefinitionDTO;
import impl.UserDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static utils.Constants.GSON_INSTANCE;
import static utils.Constants.REFRESH_RATE;

public class UsersManagementController {

    @FXML
    private ListView<String> usersLV;
    private final ObservableList<String> usersData = FXCollections.observableArrayList();

    @FXML
    private Label userNameLabel;
    @FXML
    private ListView<CheckBox> addRolesLV;
    private final ObservableList<CheckBox> addRolesData = FXCollections.observableArrayList();


    @FXML
    private ListView<String> assignedRolesLV;
    private final ObservableList<String> assignedRolesData = FXCollections.observableArrayList();

    @FXML
    private Label availableFlowsLabel;

    @FXML
    private Label usersExecutionsLabel;

    @FXML
    private CheckBox setManagerCB;

    @FXML
    private Button saveButton;
    private Timer timer;
    private TimerTask usersRefresher;
    private Map<String, UserDTO> currentUsers;
    private String selectedUser = null;
    Map<String, RoleDefinitionDTO> currentRoles;

    @FXML
    void saveButtonActionListener(ActionEvent event) {
        List<RoleDefinitionDTO> rolesToAdd = new ArrayList<>();
        List<RoleDefinitionDTO> rolesToRemove = new ArrayList<>();
        boolean isManager = false;
        for (CheckBox checkBox: addRolesData) {
            if(checkBox.isSelected()) {
                if(!currentUsers.get(selectedUser).getRolesName().contains(checkBox.getText())) {
                    rolesToAdd.add(currentRoles.get(checkBox.getText()));
                    if(checkBox.getText().equals("All Flows")){
                        isManager = true;
                    }
                }
            } else {
                if(currentUsers.get(selectedUser).getRolesName().contains(checkBox.getText())){
                    rolesToRemove.add(currentRoles.get(checkBox.getText()));
                }
            }
        }
        if(setManagerCB.isSelected() && !isManager && !currentUsers.get(selectedUser).getRolesName().contains("All Flows")) {
            rolesToAdd.add(currentRoles.get("All Flows"));
        } else if (!setManagerCB.isSelected() && isManager && currentUsers.get(selectedUser).getRolesName().contains("All Flows")){
            rolesToRemove.add(currentRoles.get("All Flows"));
        }

        httpCallToAddOrRemoveRoles(rolesToAdd, Constants.ADD_ROLES_TO_USER);
        httpCallToAddOrRemoveRoles(rolesToRemove, Constants.REMOVE_ROLES_FROM_USER);
    }

    private void httpCallToAddOrRemoveRoles(List<RoleDefinitionDTO> rolesList, String endpoint) {
        // Serialize the list to JSON
        String jsonBody = GSON_INSTANCE.toJson(rolesList);

        // Set the JSON as the request body
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        String finalUrl = HttpUrl
                .parse(endpoint)
                .newBuilder()
                .addQueryParameter("userName", selectedUser)
                .build()
                .toString();


        HttpClientUtil.runPostAsync(finalUrl, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Process the response as needed
                String responseBody = response.body().string();
                if(response.isSuccessful()) {
                }
            }
        });
    }

    @FXML public void initialize() {
        usersLV.setItems(usersData);
        assignedRolesLV.setItems(assignedRolesData);
        addRolesLV.setItems(addRolesData);
        startUsersRefresher();
    }
    @FXML
    void userClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {
            int selectedIndex = usersLV.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < usersData.size()) {
                this.selectedUser = usersLV.getSelectionModel().getSelectedItem();
                assignedRolesData.clear();
                addRolesData.clear();
                showUserDetails();
                if(currentUsers.get(selectedUser).isManager()){
                    setManagerCB.selectedProperty().set(true);
                } else {
                    setManagerCB.selectedProperty().set(false);
                }
            }
        }
    }

    private void showUserDetails() {
        boolean updateRoles;
        if(selectedUser != null) {
            List<String> flows = new ArrayList<>();
            userNameLabel.setText(selectedUser);
            usersExecutionsLabel.setText(Integer.toString(currentUsers.get(selectedUser).getNumOfExecutions()));
            List<RoleDefinitionDTO> userRoles = currentUsers.get(selectedUser).getRoles();
            List<String> userRolesName = new ArrayList<>();
            for (RoleDefinitionDTO role: userRoles) {
                userRolesName.add(role.getName());
            }
            updateRoles = userRoles.size() != assignedRolesData.size() || !userRolesName.containsAll(assignedRolesData);
            if(updateRoles) {
                assignedRolesData.clear();
            }
            for (RoleDefinitionDTO role: userRoles) {
                if(updateRoles) {
                    assignedRolesData.add(role.getName());
                }
                for (String flow: role.getFlows()) {
                    if(!flows.contains(flow)) {
                        flows.add(flow);
                    }
                }
            }
            availableFlowsLabel.setText(Integer.toString(flows.size()));
            showAllRolesList();
        }
    }

    public void startUsersRefresher() {
        usersRefresher = new UsersRefresher(this::updateUsersInfo);
        timer = new Timer();
        timer.schedule(usersRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateUsersInfo(Map<String, UserDTO> users) {
        currentUsers = users;
        if(usersData.size() != users.size()) {
                usersData.clear();
                for (Map.Entry<String, UserDTO> entry : users.entrySet()) {
                    usersData.add(entry.getKey());

                }
        }
        showUserDetails();
    }

    private void showAllRolesList() {
        if (selectedUser != null) {
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
                            currentRoles = roles;
                            if (addRolesData.size() != roles.size()) {
                                addRolesData.clear();
                                for (Map.Entry<String, RoleDefinitionDTO> entry : roles.entrySet()) {
                                    CheckBox checkBox = new CheckBox(entry.getKey());
                                    if(currentUsers.get(selectedUser).getRolesName().contains(entry.getKey())){
                                        checkBox.selectedProperty().set(true);
                                    }
                                    addRolesData.add(checkBox);
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    public void closeTimer(){
        if(timer != null){
            timer.cancel();
        }
        if(usersRefresher != null){
            usersRefresher.cancel();
        }
    }
}