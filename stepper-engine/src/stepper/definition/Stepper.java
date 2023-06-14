package stepper.definition;

import flow.definition.api.DataInFlow;
import flow.definition.api.FlowDefinition;
import flow.definition.api.continuations.Continuation;
import flow.definition.api.continuations.ContinuationMapping;

import java.util.ArrayList;
import java.util.List;

public class Stepper {
    private List<FlowDefinition> flows;
    private final int threadPool;

    public Stepper(int threadPool) {
        this.threadPool = threadPool;
        this.flows = new ArrayList<>();
    }

    public List<FlowDefinition> getFlows() {
        return flows;
    }

    public int getThreadPool() {
        return threadPool;
    }

    public void addFlowToStepper(FlowDefinition flow) { this.flows.add(flow); }

    public boolean validateStepperStructure() {
        for (FlowDefinition flow: flows) {
            if(!flow.validateFlowStructure()) {
                return false;
            }
        }
        return validateContinuations();
    }

    private boolean validateContinuations() {
        for (FlowDefinition flow: flows) {
            if(flow.getContinuations() != null) {
                for (Continuation continuation: flow.getContinuations().getContinuations()) {
                    if(!isFlowExist(continuation.getTargetFlow())){
                        return false;
                    }
                    for (ContinuationMapping mapping: continuation.getContinuationMappings()) {
                        if(!isOutputExistInFlow(mapping.getSourceData(), flow) ||
                                !isInputExistInFlow(mapping.getTargetData(),
                                        findFlowDefinitionByName(continuation.getTargetFlow()))){
                            return false;
                        }
                        if(!isSameType(mapping.getSourceData(), mapping.getTargetData(), findFlowDefinitionByName(continuation.getTargetFlow()))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isSameType(String data1, String data2, FlowDefinition flowDefinition){
        DataInFlow dataInFlow1 = null;
        DataInFlow dataInFlow2 = null;
        for (DataInFlow data: flowDefinition.getAllDataInFlow()) {
            if(data.getDataInstanceName().equals(data1)) {
                dataInFlow1 = data;
            } else if(data.getDataInstanceName().equals(data2)){
                dataInFlow2 = data;
            }
        }
        if(dataInFlow1 != null && dataInFlow2 != null){
            return  dataInFlow1.getDataDefinition().getType().equals(dataInFlow2.getDataDefinition().getType());
        } else {
            return false;
        }
    }

    private boolean isInputExistInFlow(String inputName, FlowDefinition flow){
        for (DataInFlow input: flow.getFlowInputs()) {
            if(input.getDataInstanceName().equals(inputName)){
                return true;
            }
        }
        return false;
    }
    private boolean isOutputExistInFlow(String outputName, FlowDefinition flow){
        for (DataInFlow output: flow.getFlowOutputs()) {
            if(output.getDataInstanceName().equals(outputName)){
                return true;
            }
        }
        return false;
    }

    private boolean isFlowExist(String flowName){
        for (FlowDefinition flow: flows) {
            if(flow.getName().equals(flowName)){
                return true;
            }
        }
        return false;
    }

    public FlowDefinition findFlowDefinitionByName(String flowName) {
        for (FlowDefinition flow: flows) {
            if(flow.getName().equals(flowName)) {
                return flow;
            }
        }
        return null;
    }

}
