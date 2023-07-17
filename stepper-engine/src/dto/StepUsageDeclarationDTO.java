package dto;

import flow.definition.api.StepUsageDeclaration;

public class StepUsageDeclarationDTO implements DTO {
    private  String originalName;
    private  String name;
    private  boolean isReadOnly;

    public StepUsageDeclarationDTO(StepUsageDeclaration stepUsageDeclaration) {
        this.originalName = stepUsageDeclaration.getStepDefinition().getName();
        this.name = stepUsageDeclaration.getFinalStepName();
        this.isReadOnly = stepUsageDeclaration.getStepDefinition().isReadonly();
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

/*    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }*/
}
