package FXML.definition.model;

import impl.FlowDefinitionDTO;

import java.util.List;

public class DefinitionsListWithVersion {
    private int version;
    private List<FlowDefinitionDTO> entries;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<FlowDefinitionDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<FlowDefinitionDTO> entries) {
        this.entries = entries;
    }
}
