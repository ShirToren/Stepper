package impl;

import api.DTO;

public class StepUsageDeclarationDTO implements DTO {
    private final String originalName;
    private final String name;
    private final boolean isReadOnly;

    public StepUsageDeclarationDTO(String originalName, String name, boolean isReadOnly) {
        this.originalName = originalName;
        this.name = name;
        this.isReadOnly = isReadOnly;
    }
    public String getOriginalName() {
        return originalName;
    }

    public String getName() {
        return name;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }
}
