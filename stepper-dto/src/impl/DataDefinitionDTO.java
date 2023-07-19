package impl;

import api.DTO;

public class DataDefinitionDTO implements DTO {
    private final String name;
    private final boolean isUserFriendly;
    private final String type;

    public DataDefinitionDTO(String name, boolean isUserFriendly, String type) {
        this.name = name;
        this.isUserFriendly = isUserFriendly;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isUserFriendly() {
        return isUserFriendly;
    }

    public String getType() {
        return type;
    }
}
