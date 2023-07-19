package users;

import role.RoleDefinition;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String name;
    private final List<RoleDefinition> roles;

    public User(String name) {
        this.name = name;
        this.roles = new ArrayList<>();
    }

/*    public List<RoleDefinition> getRoles() {
        return roles;
    }*/

    public synchronized List<String> getRoles() {
        List<String> list = new ArrayList<>();
        for (RoleDefinition role: roles) {
            list.add(role.getName());
        }
        return list;
    }

    public void addRole(RoleDefinition role){
        roles.add(role);
    }
}
