package stepper.definition;

import flow.definition.api.FlowDefinition;

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
        boolean isValid = true;
        for (FlowDefinition flow: flows) {
            if(!flow.validateFlowStructure()) {
                isValid = false;
                break;
            }
        }
        return  isValid;
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
