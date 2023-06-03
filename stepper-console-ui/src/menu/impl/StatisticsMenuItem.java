package menu.impl;

import dto.DTO;
import dto.FlowExecutionDTO;
import menu.api.MenuItem;
import stepper.management.StepperEngineManager;

import java.util.*;

public class StatisticsMenuItem implements MenuItem {
    private final String title;


    public StatisticsMenuItem() {
        this.title = "Statistics of previous executions";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void execute(StepperEngineManager manager) {
        List<FlowExecutionDTO> allExecutionsDTO = manager.getAllExecutionsDTO();
        if(allExecutionsDTO.size() != 0) {
            Set<String> executedFlows = new HashSet<>();
            for (FlowExecutionDTO execution : allExecutionsDTO) {
                executedFlows.add(execution.getFlowDefinitionDTO().getName());
            }
            for (String flowName: executedFlows) {
                System.out.println("Flow name: " + flowName +
                        ", Executed " + manager.getFlowExecutedTimes().get(flowName) +
                        " times, Execution time average: " + manager.getFlowExecutedTotalMillis().get(flowName) / manager.getFlowExecutedTimes().get(flowName));
            }
                List<String> executedStepsDefinitionsNames = manager.getExecutedStepsDefinitionsNames();
            Set<String> executedSteps = new HashSet<>(executedStepsDefinitionsNames);
                for (String stepName : executedSteps) {
                    if(manager.getStepExecutedTimes().get(stepName) != null)
                    {
                        System.out.println("Step name: " + stepName +
                                ", Executed " + manager.getStepExecutedTimes().get(stepName)+
                                " times, Execution time average: " +
                                manager.getStepExecutedTotalMillis().get(stepName) / manager.getStepExecutedTimes().get(stepName));
                    }
                }
        } else {
            System.out.println("No Execution yet.");
        }
    }



    @Override
    public void setInput(String inputName, Object input) {

    }

    @Override
    public DTO getDTO() {
        return null;
    }
}
