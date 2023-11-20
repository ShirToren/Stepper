package users;

import role.RoleDefinition;

import java.util.*;

public class UserManager {
    private final Map<String, User> usersMap;

    public UserManager() {
        usersMap = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        usersMap.put(username, new User(username));
    }

    public synchronized void removeUser(String username) {
        usersMap.remove(username);
    }

    public synchronized Map<String, User> getUsers() {
        return usersMap;
    }

    public boolean isUserExists(String username) {
        return usersMap.containsKey(username);
    }
}
