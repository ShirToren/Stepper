package users;

import role.RoleDefinition;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String name;
    private final List<RoleDefinition> roles;
    private int numOfExecutions;
    private boolean isManager;
    public User(String name) {
        this.name = name;
        this.roles = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public synchronized List<RoleDefinition> getRolesDefinitions() {
        return roles;
    }
    public synchronized int getNumOfExecutions() {
        return numOfExecutions;
    }

    public synchronized List<String> getRoles() {
        List<String> list = new ArrayList<>();
        for (RoleDefinition role: roles) {
            list.add(role.getName());
        }
        return list;
    }
    public synchronized void addExecution() { numOfExecutions++; }

    public void addRole(RoleDefinition role){
        roles.add(role);
    }
    public void removeRole(RoleDefinition role){
        roles.remove(role);
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public boolean isManager() {
        return isManager;
    }
}
