package dto;

import flow.definition.api.DataInFlow;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.definition.api.continuations.Continuations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowDefinitionDTO implements DTO {
    private  String name;
    private  String description;
    private  List<DataInFlowDTO> flowsFormalOutputs;
    private  boolean isReadOnly;
    private  List<StepUsageDeclarationDTO> steps;
    private  List<DataInFlowDTO> freeInputs;
    private  List<DataInFlowDTO> flowsOutputs;
    private  List<DataInFlowDTO> flowsInputs;
    private  Map<String, List<String>> freeInputsStepTarget;
    private  Continuations continuations;

    public FlowDefinitionDTO(FlowDefinition flowDefinition) {
        this.steps = new ArrayList<>();
        this.freeInputs = new ArrayList<>();
        this.flowsOutputs = new ArrayList<>();
        this.flowsInputs = new ArrayList<>();
        this.flowsFormalOutputs = new ArrayList<>();
        this.name = flowDefinition.getName();
        this.freeInputsStepTarget = flowDefinition.getFreeInputsStepTarget();
        this.description = flowDefinition.getDescription();
        this.isReadOnly = flowDefinition.isReadOnly();
        this.continuations = flowDefinition.getContinuations();
        for (StepUsageDeclaration step : flowDefinition.getFlowSteps()) {
            this.steps.add(new StepUsageDeclarationDTO(step));
        }
        for (DataInFlow freeInput : flowDefinition.getFlowFreeInputs()) {
            this.freeInputs.add(new DataInFlowDTO(freeInput));
        }
        for (DataInFlow output : flowDefinition.getFlowOutputs()) {
            this.flowsOutputs.add(new DataInFlowDTO(output));
        }
        for (DataInFlow input : flowDefinition.getFlowInputs()) {
            this.flowsInputs.add(new DataInFlowDTO(input));
        }
        for (DataInFlow formalOutput : flowDefinition.getFormalOutputsDataInFlow()) {
            this.flowsFormalOutputs.add(new DataInFlowDTO(formalOutput));
        }
    }

    public List<DataInFlowDTO> getFlowsFormalOutputs() {
        return flowsFormalOutputs;
    }

    public String getName() {
        return name;
    }

    public Continuations getContinuations() {
        return continuations;
    }

    public int getNumberOfContinuations(){
        if(continuations == null) {
            return 0;
        } else {
            return continuations.getContinuations().size();
        }
    }

    public Map<String, List<String>> getFreeInputsStepTarget() {
        return freeInputsStepTarget;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public List<StepUsageDeclarationDTO> getSteps() {
        return steps;
    }

    public List<DataInFlowDTO> getFreeInputs() {
        return freeInputs;
    }

    public List<DataInFlowDTO> getFlowsOutputs() {
        return flowsOutputs;
    }
    public List<DataInFlowDTO> getFlowsInputs() {
        return flowsInputs;
    }

/*    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFlowsFormalOutputs(List<DataInFlowDTO> flowsFormalOutputs) {
        this.flowsFormalOutputs = flowsFormalOutputs;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public void setSteps(List<StepUsageDeclarationDTO> steps) {
        this.steps = steps;
    }

    public void setFreeInputs(List<DataInFlowDTO> freeInputs) {
        this.freeInputs = freeInputs;
    }

    public void setFlowsOutputs(List<DataInFlowDTO> flowsOutputs) {
        this.flowsOutputs = flowsOutputs;
    }

    public void setFlowsInputs(List<DataInFlowDTO> flowsInputs) {
        this.flowsInputs = flowsInputs;
    }

    public void setFreeInputsStepTarget(Map<String, List<String>> freeInputsStepTarget) {
        this.freeInputsStepTarget = freeInputsStepTarget;
    }

    public void setContinuations(Continuations continuations) {
        this.continuations = continuations;
    }*/
}
