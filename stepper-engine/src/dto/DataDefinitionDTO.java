package dto;

import dd.api.DataDefinition;

public class DataDefinitionDTO implements DTO{

    private  String name;
    private  boolean isUserFriendly;
    private  String type;
    public DataDefinitionDTO(DataDefinition dataDefinition) {
        this.name = dataDefinition.getName();
        this.isUserFriendly = dataDefinition.isUserFriendly();
        this.type = dataDefinition.getType().getName();
    }
    public String getName() {
        return name;
    }

    public boolean isUserFriendly() {
        return isUserFriendly;
    }

    public Class<?> getType(){
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

/*    public void setName(String name) {
        this.name = name;
    }

    public void setUserFriendly(boolean userFriendly) {
        isUserFriendly = userFriendly;
    }

    public void setType(String type) {
        this.type = type;
    }*/
}
