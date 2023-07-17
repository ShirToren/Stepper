package dto;


import dd.api.DataDefinition;
import flow.definition.api.DataInFlow;
import flow.definition.api.StepUsageDeclaration;
import step.api.DataNecessity;

import java.util.ArrayList;
import java.util.List;

public class DataInFlowDTO implements DTO {
    private  String finalName;
    private  String originalName;
    private  DataDefinitionDTO dataDefinition;
    private  List<StepUsageDeclarationDTO> sourceSteps;
    private  List<StepUsageDeclarationDTO> targetSteps;
    private  DataNecessity dataNecessity;
    private  String userString;
    private  StepUsageDeclarationDTO ownerStep;


    public DataInFlowDTO(DataInFlow dataInFlow) {
        this.sourceSteps = new ArrayList<>();
        this.targetSteps = new ArrayList<>();
        this.originalName = dataInFlow.getOriginalDataInstanceNameInStep();
        this.finalName = dataInFlow.getDataInstanceName();
        this.dataNecessity = dataInFlow.getDataDefinitionDeclaration().necessity();
        this.dataDefinition = new DataDefinitionDTO(dataInFlow.getDataDefinition());
        //this.dataDefinition = dataInFlow.getDataDefinition();

        this.ownerStep = new StepUsageDeclarationDTO(dataInFlow.getOwnerStepUsageDeclaration());
        this.userString = dataInFlow.getDataDefinitionDeclaration().userString();
        for (DataInFlow sourceData : dataInFlow.getSourceDataInFlow()) {
            sourceSteps.add(new StepUsageDeclarationDTO(sourceData.getOwnerStepUsageDeclaration()));
        }
        for (DataInFlow targetData : dataInFlow.getTargetDataInFlow()) {
            targetSteps.add(new StepUsageDeclarationDTO(targetData.getOwnerStepUsageDeclaration()));
        }
    }

    public String getFinalName() {
        return finalName;
    }

    public DataDefinitionDTO getDataDefinitionDTO() {
        return dataDefinition;
    }

    public DataDefinitionDTO getDataDefinition() {
        return dataDefinition;
    }


    public String getOriginalName() {
        return originalName;
    }

    public List<StepUsageDeclarationDTO> getSourceSteps() {
        return sourceSteps;
    }

    public List<StepUsageDeclarationDTO> getTargetSteps() {
        return targetSteps;
    }

    public DataNecessity getDataNecessity() {
        return dataNecessity;
    }

    public String getUserString() {
        return userString;
    }

    public StepUsageDeclarationDTO getOwnerStep() {
        return ownerStep;
    }

/*    public void setFinalName(String finalName) {
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

    public void setDataNecessity(DataNecessity dataNecessity) {
        this.dataNecessity = dataNecessity;
    }

    public void setUserString(String userString) {
        this.userString = userString;
    }

    public void setOwnerStep(StepUsageDeclarationDTO ownerStep) {
        this.ownerStep = ownerStep;
    }*/
}
