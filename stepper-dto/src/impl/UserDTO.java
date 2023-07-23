package impl;

import api.DTO;

import java.util.ArrayList;
import java.util.List;

public class UserDTO implements DTO {
    private final String name;
    private final List<RoleDefinitionDTO> roles;
    private final int numOfExecutions;
    private final boolean isManager;

    public UserDTO(String name, List<RoleDefinitionDTO> roles, int numOfExecutions, boolean isManager) {
        this.name = name;
        this.roles = roles;
        this.numOfExecutions = numOfExecutions;
        this.isManager = isManager;
    }

    public String getName() {
        return name;
    }

    public List<RoleDefinitionDTO> getRoles() {
        return roles;
    }

    public List<String> getRolesName() {
        List<String> list = new ArrayList<>();
        for (RoleDefinitionDTO role: roles) {
            list.add(role.getName());
        }
        return list;
    }

    public int getNumOfExecutions() {
        return numOfExecutions;
    }

    public boolean isManager() {
        return isManager;
    }
}
