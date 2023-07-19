package flow.execution.runner;

import flow.definition.api.DataInFlow;
import flow.definition.api.InitialInputValue;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import flow.execution.FlowExecutionResult;
import flow.execution.context.StepExecutionContext;
import flow.execution.context.StepExecutionContextImpl;
import step.api.StepResult;

import java.time.LocalTime;
import java.util.Map;

public class FLowExecutor {

    public void executeFlow(FlowExecution flowExecution) {
        LocalTime executionTime = LocalTime.now();
        flowExecution.setStartExecutionTime(executionTime);
        //System.out.println("Starting execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUuid() + "]");
        StepExecutionContext context = new StepExecutionContextImpl(flowExecution); // actual object goes here...
        context.storeDataTypes(flowExecution);

        // populate context with all free inputs (mandatory & optional) that were given from the user
        // (typically stored on top of the flow execution object)
        for(Map.Entry<String, Object> entry : flowExecution.getFreeInputs().entrySet()) {
            String inputName = entry.getKey();
            StepUsageDeclaration step = flowExecution.getOwnerStepByInputName(entry.getKey());
            context.setCurrentStep(step);
            context.storeDataValue(inputName, entry.getValue());
        }
        for (InitialInputValue initInputValue: flowExecution.getFlowDefinition().getInitialInputValues()) {
            context.setCurrentStep(flowExecution.getFlowDefinition().findOwnerStep(initInputValue.getInputName()));
            context.storeDataValue(initInputValue.getInputName(), initInputValue.getInitialValue());
        }

        ////check if all inputs are ok
        // start actual execution
        for (int i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size(); i++) {
            StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
            context.setCurrentStep(stepUsageDeclaration);
            //System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
            StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);


            for (DataInFlow formalOutput : flowExecution.getFlowDefinition().getFormalOutputsDataInFlow()) {
                if(formalOutput.getOwnerStepUsageDeclaration().getFinalStepName().equals(stepUsageDeclaration.getFinalStepName())) {
                    if(context.getDataValueByFinalName(formalOutput.getDataInstanceName(),
                            formalOutput.getDataDefinition().getType()) != null){
                        flowExecution.getExecutionFormalOutputs().put(formalOutput.getDataInstanceName(),
                                context.getDataValueByFinalName(formalOutput.getDataInstanceName(),
                                        formalOutput.getDataDefinition().getType()));
                    }
                }
            }

            for(DataInFlow output : flowExecution.getFlowDefinition().getFlowOutputs()) {
                if (output.getOwnerStepUsageDeclaration().getFinalStepName().equals(stepUsageDeclaration.getFinalStepName())) {
                    Object dataValue = context.getDataValueByFinalName(output.getDataInstanceName(), output.getDataDefinition().getType());
                    if (dataValue != null) {
                        flowExecution.getAllExecutionOutputs().put(output.getDataInstanceName(), dataValue);
                    }
                }
            }
            for (DataInFlow input: flowExecution.getFlowDefinition().getFlowInputs()) {
                if (input.getOwnerStepUsageDeclaration().getFinalStepName().equals(stepUsageDeclaration.getFinalStepName())) {
                    Object dataValue = context.getDataValueByFinalName(input.getDataInstanceName(), input.getDataDefinition().getType());
                    if (dataValue != null) {
                        flowExecution.getAllExecutionInputs().put(input.getDataInstanceName(), dataValue);
                    }
                }
            }


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(stepResult.equals(StepResult.FAILURE)) {
                flowExecution.setFlowExecutionResult(FlowExecutionResult.FAILURE);
                if(!stepUsageDeclaration.skipIfFail()) { break; }
            } else if (stepResult.equals(StepResult.WARNING)){
                    flowExecution.setFlowExecutionResult(FlowExecutionResult.WARNING);
                } else {
                    flowExecution.setFlowExecutionResult(FlowExecutionResult.SUCCESS);
                }
            context.copyOutputsValuesForCustomMapping(flowExecution, stepUsageDeclaration.getStepDefinition());
            //System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
            // check if should continue etc..
            }

        //System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUuid() + "]. Status: " + flowExecution.getFlowExecutionResult());
        LocalTime endExecutionTime = LocalTime.now();
        flowExecution.setEndExecutionTime(endExecutionTime);
    }
}
