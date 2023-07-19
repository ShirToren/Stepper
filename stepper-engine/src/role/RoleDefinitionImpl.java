package role;

import java.util.List;

public class RoleDefinitionImpl implements RoleDefinition{
    private final String name;
    private final String description;
    private final List<String> flows;

    public RoleDefinitionImpl(String name, String description, List<String> flows) {
        this.name = name;
        this.description = description;
        this.flows = flows;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getFlows() {
        return flows;
    }
}
