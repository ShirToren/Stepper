package dto;

import flow.definition.api.StepUsageDeclaration;

public class StepUsageDeclarationDTO implements DTO {
    private final String originalName;
    private final String name;
    private final boolean isReadOnly;


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
}
