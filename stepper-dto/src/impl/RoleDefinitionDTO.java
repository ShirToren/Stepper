package impl;

import api.DTO;

import java.util.List;

public class RoleDefinitionDTO implements DTO {
    private final String name;
    private final String description;
    private final List<String> flows;

    public RoleDefinitionDTO(String name, String description, List<String> flows) {
        this.name = name;
        this.description = description;
        this.flows = flows;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getFlows() {
        return flows;
    }
}
