package dto;

import dd.api.DataDefinition;
import flow.definition.api.DataInFlow;
import flow.definition.api.StepUsageDeclaration;
import step.api.DataNecessity;

import java.util.ArrayList;
import java.util.List;

public class DataInFlowDTO implements DTO {
    private final String finalName;
    private final DataDefinition dataDefinition;
    private final List<StepUsageDeclarationDTO> sourceSteps;
    private final List<StepUsageDeclarationDTO> targetSteps;
    private final DataNecessity dataNecessity;
    private final String userString;
    private final StepUsageDeclarationDTO ownerStep;

    public DataInFlowDTO(DataInFlow dataInFlow) {
        this.sourceSteps = new ArrayList<>();
        this.targetSteps = new ArrayList<>();
        this.finalName = dataInFlow.getDataInstanceName();
        this.dataNecessity = dataInFlow.getDataDefinitionDeclaration().necessity();
        this.dataDefinition = dataInFlow.getDataDefinition();
        this.ownerStep = new StepUsageDeclarationDTO(dataInFlow.getOwnerStepUsageDeclaration());
        this.userString = dataInFlow.getDataDefinitionDeclaration().userString();
        for (DataInFlow sourceData : dataInFlow.getSourceDataInFlow()) {
            sourceSteps.add(new StepUsageDeclarationDTO(sourceData.getOwnerStepUsageDeclaration()));
        }
        for (DataInFlow targetData : dataInFlow.getTargetDataInFlow()) {
            sourceSteps.add(new StepUsageDeclarationDTO(targetData.getOwnerStepUsageDeclaration()));
        }
    }

    public String getFinalName() {
        return finalName;
    }

    public DataDefinition getDataDefinition() {
        return dataDefinition;
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
}
