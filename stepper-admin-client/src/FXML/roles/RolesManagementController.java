package FXML.roles;

import utils.Constants;
import utils.http.HttpClientUtil;
import impl.FlowDefinitionDTO;
import impl.RoleDefinitionDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static utils.Constants.GSON_INSTANCE;
import static utils.Constants.REFRESH_RATE;

public class RolesManagementController {
    private Timer timer;
    private TimerTask rolesRefresher;

    @FXML
    private ListView<String> rolesLV;
    private final ObservableList<String> rolesData = FXCollections.observableArrayList();

    @FXML
    private Button newButton;

    @FXML
    private TextField roleNameTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private ListView<String> assignedFlowsLV;
    private final ObservableList<String> assignedFlowsData = FXCollections.observableArrayList();

    @FXML
    private Button saveButton;

    @FXML
    private ListView<CheckBox> assignFlowsLV;
    private final ObservableList<CheckBox> assignFlowsData = FXCollections.observableArrayList();


    @FXML
    private ListView<String> assignedUsersLV;
    private final ObservableList<String> assignedUsersData = FXCollections.observableArrayList();
    private Map<String, RoleDefinitionDTO> currentRoles;
    private String selectedRole = null;
    private List<FlowDefinitionDTO> currentFlows;
    private boolean newRoleMode;

    @FXML public void initialize() {
        rolesLV.setItems(rolesData);
        assignedFlowsLV.setItems(assignedFlowsData);
        assignFlowsLV.setItems(assignFlowsData);
        assignedUsersLV.setItems(assignedUsersData);
        roleNameTextField.setEditable(false);
        descriptionTextArea.setEditable(false);
        startRolesRefresher();
    }

    @FXML
    void newButtonActionListener(ActionEvent event) {
        newRoleMode = true;
        selectedRole = null;
        rolesLV.getSelectionModel().clearSelection();
        rolesLV.disableProperty().set(true);
        roleNameTextField.setEditable(true);
        roleNameTextField.textProperty().set("");
        descriptionTextArea.setEditable(true);
        descriptionTextArea.textProperty().set("");
        assignedFlowsData.clear();
        assignedUsersData.clear();
        for (CheckBox checkBox: assignFlowsData) {
            checkBox.selectedProperty().set(false);
        }
        showAllFlows();
    }

    @FXML
    void saveButtonActionListener(ActionEvent event) {
        if (newRoleMode) {
            if(roleNameTextField.getText().isEmpty() || descriptionTextArea.getText().isEmpty()) {
                showErrorDialog("Error", "Please enter role name and description");
            } else if(rolesData.contains(roleNameTextField.getText())) {
                showErrorDialog("Error", "This role name is already exist");
            }else {
                List<String> flowsList = new ArrayList<>();
                for (CheckBox flow: assignFlowsData) {
                    if(flow.isSelected()) {
                        flowsList.add(flow.getText());
                    }
                }
                httpCallToCreateRole(roleNameTextField.getText(),
                        descriptionTextArea.getText(), flowsList);
                roleNameTextField.setEditable(false);
                descriptionTextArea.setEditable(false);
                roleNameTextField.setText("");
                descriptionTextArea.setText("");
                rolesLV.disableProperty().set(false);
                assignFlowsData.clear();
                assignedFlowsData.clear();
                assignedUsersData.clear();
                newRoleMode = false;
            }
        } else {
            if (selectedRole != null) {
                List<String> flowsToAdd = new ArrayList<>();
                List<String> flowsToRemove = new ArrayList<>();

                for (CheckBox checkBox : assignFlowsData) {
                    if (checkBox.isSelected()) {
                        if (!currentRoles.get(selectedRole).getFlows().contains(checkBox.getText())) {
                            flowsToAdd.add(checkBox.getText());
                        }
                    } else {
                        if (currentRoles.get(selectedRole).getFlows().contains(checkBox.getText())) {
                            flowsToRemove.add(checkBox.getText());
                        }
                    }
                }

                httpCallToAddOrRemoveFlows(flowsToAdd, Constants.ADD_FLOWS_TO_ROLE);
                httpCallToAddOrRemoveFlows(flowsToRemove, Constants.REMOVE_FLOWS_FROM_ROLE);
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void httpCallToCreateRole(String roleName, String description, List<String> flowsList) {
        String jsonBody = GSON_INSTANCE.toJson(flowsList);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        String finalUrl = HttpUrl
                .parse(Constants.CREATE_ROLE)
                .newBuilder()
                .addQueryParameter("roleName", roleName)
                .addQueryParameter("description", description)
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

    private void httpCallToAddOrRemoveFlows(List<String> flowsList, String endpoint) {
        // Serialize the list to JSON
        String jsonBody = GSON_INSTANCE.toJson(flowsList);

        // Set the JSON as the request body
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        String finalUrl = HttpUrl
                .parse(endpoint)
                .newBuilder()
                .addQueryParameter("roleName", selectedRole)
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

    public void startRolesRefresher() {
        rolesRefresher = new RolesRefresher(this::updateRolesInfo);
        timer = new Timer();
        timer.schedule(rolesRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateRolesInfo(Map<String, RoleDefinitionDTO> roles) {
        this.currentRoles = roles;
        if(roles.size() != rolesData.size()) {
            rolesData.clear();
            for (Map.Entry<String, RoleDefinitionDTO> entry: roles.entrySet()) {
                rolesData.add(entry.getKey());
            }
        }
        showRoleDetails();
    }

    private void showRoleDetails() {
        if (selectedRole != null) {
            roleNameTextField.setText(selectedRole);
            descriptionTextArea.setText(currentRoles.get(selectedRole).getDescription());
            List<String> flows = currentRoles.get(selectedRole).getFlows();
            if(assignedFlowsData.size() != flows.size() || !flows.containsAll(assignedFlowsData)) {
                assignedFlowsData.clear();
                assignedFlowsData.addAll(flows);
            }
            showAllFlows();
            showAssignedUsers();
        }
    }

    private void showAllFlows() {
            String finalUrl = HttpUrl
                    .parse(Constants.ALL_FLOWS)
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
                        FlowDefinitionDTO[] flowDefinitionDTOS = GSON_INSTANCE.fromJson(jsonResponse, FlowDefinitionDTO[].class);
                        List<FlowDefinitionDTO> list = Arrays.asList(flowDefinitionDTOS);
                        Platform.runLater(() -> {
                            currentFlows = list;
                            if (assignFlowsData.size() != list.size()) {
                                assignFlowsData.clear();
                                for (FlowDefinitionDTO flowDefinitionDTO: list) {
                                    CheckBox checkBox = new CheckBox(flowDefinitionDTO.getName());
                                        if (selectedRole != null && currentRoles.get(selectedRole).getFlows().contains(flowDefinitionDTO.getName())) {
                                            checkBox.selectedProperty().set(true);
                                        }
                                    assignFlowsData.add(checkBox);
                                }
                            }
                        });
                    }
                }
            });

    }

    private void showAssignedUsers(){
        if (selectedRole != null) {
            String finalUrl = HttpUrl
                    .parse(Constants.ASSIGNED_USERS_BY_ROLE)
                    .newBuilder()
                    .addQueryParameter("roleName", selectedRole)
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
                        String[] users = GSON_INSTANCE.fromJson(jsonResponse, String[].class);
                        List<String> usersList = Arrays.asList(users);
                        Platform.runLater(() -> {
                            if (assignedUsersData.size() != usersList.size()) {
                                assignedUsersData.clear();
                                assignedUsersData.addAll(usersList);
                            }
                        });
                    }
                }
            });
        }
    }

    @FXML
    void roleClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {
            int selectedIndex = rolesLV.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < rolesData.size()) {
                selectedRole = rolesLV.getSelectionModel().getSelectedItem();
                assignedFlowsData.clear();
                assignFlowsData.clear();
                showRoleDetails();
/*                assignedRolesData.clear();
                addRolesData.clear();
                showUserDetails();*/
            }
        }
    }

}

