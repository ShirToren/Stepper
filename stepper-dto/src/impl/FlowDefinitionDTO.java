package impl;

import api.DTO;
import impl.continuations.ContinuationsDTO;

import java.util.List;
import java.util.Map;

public class FlowDefinitionDTO implements DTO {
    private final String name;
    private final String description;
    private final List<DataInFlowDTO> flowsFormalOutputs;
    private final boolean isReadOnly;
    private final List<StepUsageDeclarationDTO> steps;
    private final List<DataInFlowDTO> freeInputs;
    private final List<DataInFlowDTO> flowsOutputs;
    private final List<DataInFlowDTO> flowsInputs;
    private final Map<String, List<String>> freeInputsStepTarget;
    private final ContinuationsDTO continuations;

    public FlowDefinitionDTO(String name, String description, List<DataInFlowDTO> flowsFormalOutputs, boolean isReadOnly, List<StepUsageDeclarationDTO> steps, List<DataInFlowDTO> freeInputs, List<DataInFlowDTO> flowsOutputs, List<DataInFlowDTO> flowsInputs, Map<String, List<String>> freeInputsStepTarget, ContinuationsDTO continuations) {
        this.name = name;
        this.description = description;
        this.flowsFormalOutputs = flowsFormalOutputs;
        this.isReadOnly = isReadOnly;
        this.steps = steps;
        this.freeInputs = freeInputs;
        this.flowsOutputs = flowsOutputs;
        this.flowsInputs = flowsInputs;
        this.freeInputsStepTarget = freeInputsStepTarget;
        this.continuations = continuations;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<DataInFlowDTO> getFlowsFormalOutputs() {
        return flowsFormalOutputs;
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

    public Map<String, List<String>> getFreeInputsStepTarget() {
        return freeInputsStepTarget;
    }

    public ContinuationsDTO getContinuations() {
        return continuations;
    }

    public int getNumberOfContinuations(){
        if(continuations == null) {
            return 0;
        } else {
            return continuations.getContinuations().size();
        }
    }
}
