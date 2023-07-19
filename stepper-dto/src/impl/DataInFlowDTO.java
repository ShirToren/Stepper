package impl;

import api.DTO;

import java.util.List;

public class DataInFlowDTO implements DTO {
    private String finalName;
    private String originalName;
    private DataDefinitionDTO dataDefinition;
    private List<StepUsageDeclarationDTO> sourceSteps;
    private  List<StepUsageDeclarationDTO> targetSteps;
    private  String dataNecessity;
    private  String userString;
    private  StepUsageDeclarationDTO ownerStep;

    public DataInFlowDTO(String finalName, String originalName, DataDefinitionDTO dataDefinition, List<StepUsageDeclarationDTO> sourceSteps, List<StepUsageDeclarationDTO> targetSteps, String dataNecessity, String userString, StepUsageDeclarationDTO ownerStep) {
        this.finalName = finalName;
        this.originalName = originalName;
        this.dataDefinition = dataDefinition;
        this.sourceSteps = sourceSteps;
        this.targetSteps = targetSteps;
        this.dataNecessity = dataNecessity;
        this.userString = userString;
        this.ownerStep = ownerStep;
    }

    public String getFinalName() {
        return finalName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public DataDefinitionDTO getDataDefinition() {
        return dataDefinition;
    }

    public List<StepUsageDeclarationDTO> getSourceSteps() {
        return sourceSteps;
    }

    public List<StepUsageDeclarationDTO> getTargetSteps() {
        return targetSteps;
    }

    public String getDataNecessity() {
        return dataNecessity;
    }

    public String getUserString() {
        return userString;
    }

    public StepUsageDeclarationDTO getOwnerStep() {
        return ownerStep;
    }

    @Override
    public int hashCode() {
        int result = 17; // Arbitrary prime number as initial value

        result = 31 * result + (finalName != null ? finalName.hashCode() : 0);
        result = 31 * result + (originalName != null ? originalName.hashCode() : 0);

        result = 31 * result + (dataDefinition != null ? dataDefinition.hashCode() : 0);
        result = 31 * result + (sourceSteps != null ? sourceSteps.hashCode() : 0);
        result = 31 * result + (targetSteps != null ? targetSteps.hashCode() : 0);
        result = 31 * result + (dataNecessity != null ? dataNecessity.hashCode() : 0);
        result = 31 * result + (userString != null ? userString.hashCode() : 0);
        result = 31 * result + (ownerStep != null ? ownerStep.hashCode() : 0);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass()!= this.getClass()) {
            return false;
        }
        return this.getFinalName().equals(((DataInFlowDTO) obj).finalName);
    }

    public void setFinalName(String finalName) {
        this.finalName = finalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setDataDefinition(DataDefinitionDTO dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    public void setSourceSteps(List<StepUsageDeclarationDTO> sourceSteps) {
        this.sourceSteps = sourceSteps;
    }

    public void setTargetSteps(List<StepUsageDeclarationDTO> targetSteps) {
        this.targetSteps = targetSteps;
    }

    public void setDataNecessity(String dataNecessity) {
        this.dataNecessity = dataNecessity;
    }

    public void setUserString(String userString) {
        this.userString = userString;
    }

    public void setOwnerStep(StepUsageDeclarationDTO ownerStep) {
        this.ownerStep = ownerStep;
    }
}
