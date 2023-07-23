package FXML.roles;

import FXML.users.UsersRefresher;
import impl.RoleDefinitionDTO;
import impl.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static FXML.utils.Constants.REFRESH_RATE;

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

    }

    @FXML
    void saveButtonActionListener(ActionEvent event) {

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
            if(assignedFlowsData.size() != flows.size()) {
                assignedFlowsData.clear();
                assignedFlowsData.addAll(flows);
            }
        }
    }

    @FXML
    void roleClickedActionListener(MouseEvent event) {
        if(event.getClickCount() == 1) {
            int selectedIndex = rolesLV.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < rolesData.size()) {
                selectedRole = rolesLV.getSelectionModel().getSelectedItem();
                showRoleDetails();
/*                assignedRolesData.clear();
                addRolesData.clear();
                showUserDetails();
                if(currentUsers.get(selectedUser).isManager()){
                    setManagerCB.selectedProperty().set(true);
                } else {
                    setManagerCB.selectedProperty().set(false);
                }*/
            }
        }
    }

}

