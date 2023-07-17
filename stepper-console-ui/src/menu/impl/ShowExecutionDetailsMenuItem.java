package menu.impl;

import dto.*;
import exception.IntOutOfRangeException;
import logs.LogLine;
import menu.api.MenuItem;
import step.api.DataNecessity;
import stepper.management.StepperEngineManager;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShowExecutionDetailsMenuItem implements MenuItem {
    private final String title;
    private FlowExecutionDTO dto;

    public ShowExecutionDetailsMenuItem() {
        this.title = "Show execution details";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void execute(StepperEngineManager manager) {
        List<FlowExecutionDTO> allExecutionsDTO = manager.getAllExecutionsDTO();
        if(allExecutionsDTO.size() != 0) {
            Scanner scanner = new Scanner(System.in);
            showAllFlowExecutions(allExecutionsDTO);
            System.out.printf("Choose the number of the execution to see all execution details, Or 0 to go back to the main menu%n");
            try {
                int numOfInput = scanner.nextInt();
                if(numOfInput < 0 | numOfInput > allExecutionsDTO.size()) {
                    throw new IntOutOfRangeException(0, allExecutionsDTO.size());
                }
                if (numOfInput != 0) {
                    dto = allExecutionsDTO.get(numOfInput - 1);
                    manager.setCurrentFlowExecution(dto);
                    System.out.println("ID: " + dto.getUuid().toString());
                    System.out.println("Flow name: " + dto.getFlowDefinitionDTO().getName());
                    System.out.println("Execution result: " + dto.getExecutionResult());
                    System.out.println("Total execution time: " + dto.getTotalTime());

                    System.out.println("Free inputs from user: ");
                    showFreeInputsDetails(manager);
                    System.out.println("Flow's outputs: ");
                    showAllFlowOutputsDetails();
                    System.out.println("Flow's Steps: ");
                    showAllStepsExecutionDetails();
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (IntOutOfRangeException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        } else {
            System.out.println("No Execution yet.");
        }
    }

    private void showAllStepsExecutionDetails(){
        for (StepUsageDeclarationDTO step : dto.getFlowDefinitionDTO().getSteps()) {
            if(dto.getStepsTotalTimes().containsKey(step.getName()) && dto.getStepsTotalTimes().get(step.getName()) != null) {
                System.out.println(step.getName() + ": Total Execution time: " + dto.getStepsTotalTimes().get(step.getName()) + ", Step result: " + dto.getStepsResults().get(step.getName()));
            }else {
                System.out.println(step.getName() + ": Total Execution time: 0, Step result: " + dto.getStepsResults().get(step.getName()));
            }
            System.out.printf("Summery line: %s%n",
                    dto.getSummeryLines().get(step.getName()));
            System.out.println("Log lines:");
            List<LogLine> logLines = dto.getLogLines().get(step.getName());
            for (LogLine log : logLines) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS"); // create a formatter
                String formattedTime = log.getTime().format(formatter); // format the time using the formatter
                System.out.printf("TimeStamp: %s, Line: %s%n",
                        formattedTime, log.getLine()); // output the formatted time
            }
        }
    }

    private void showAllFlowOutputsDetails() {
        int index = 1;
        for (Map.Entry<DataInFlowDTO, Object> entry : dto.getAllExecutionOutputs().entrySet()) {
            if(entry.getValue() == null) {
                System.out.printf("%d. %s: Type: %s, Value: %s%n", index,
                        entry.getKey().getFinalName(), entry.getKey().getDataDefinitionDTO().getType().getName()
                                .substring(entry.getKey().getDataDefinitionDTO().getType().getName().lastIndexOf(".") + 1),
                        entry.getValue().toString());
                index++;
            }
            System.out.printf("%d. %s: Type: %s, Value: %s%n", index,
                    entry.getKey().getFinalName(), entry.getKey().getDataDefinitionDTO().getType().getName()
                            .substring(entry.getKey().getDataDefinitionDTO().getType().getName().lastIndexOf(".") + 1),
                    entry.getValue().toString());
            index++;
        }
    }


    private void showFreeInputsDetails(StepperEngineManager manager){
        Map<String, Object> actualFreeInputs = manager.getActualFreeInputsList();
        List<DataInFlowDTO> optionalInput = new ArrayList<>();
        int index = 1;
        for (DataInFlowDTO freeInput : dto.getFlowDefinitionDTO().getFreeInputs()) {
            if(freeInput.getDataNecessity().equals(DataNecessity.MANDATORY) &&
                    actualFreeInputs.containsKey(freeInput.getFinalName() + "." + freeInput.getOwnerStep().getName())) {
                System.out.printf("%d. %s: Type: %s, Value: %s, %s%n", index,
                        freeInput.getFinalName(), freeInput.getDataDefinitionDTO().getType().getName().
                                substring(freeInput.getDataDefinitionDTO().getType().getName().lastIndexOf(".") + 1),
                        actualFreeInputs.get(freeInput.getFinalName() + "." + freeInput.getOwnerStep().getName()),
                        freeInput.getDataNecessity().name());
                index++;
            }
            else if (actualFreeInputs.containsKey(freeInput.getFinalName() + "." + freeInput.getOwnerStep().getName())) {
                optionalInput.add(freeInput);
            }
        }
        for (DataInFlowDTO optional : optionalInput) {
            System.out.printf("%d. %s: Type: %s, Value: %s, %s%n", index,
                    optional.getFinalName(), optional.getDataDefinitionDTO().getType().getName().
                            substring(optional.getDataDefinitionDTO().getType().getName().lastIndexOf(".")),
                    actualFreeInputs.get(optional.getFinalName() + "." + optional.getOwnerStep().getName()),
                    optional.getDataNecessity().name());
            index++;
        }
    }

    private void showAllFlowExecutions(List<FlowExecutionDTO> allFlowExecutionsDTO){
        System.out.println("All flows executions: ");
        int index = 1;
        for (FlowExecutionDTO execution : allFlowExecutionsDTO) {
            LocalTime executionTime = execution.getStartExecutionTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = executionTime.format(formatter);
            System.out.printf("%d. %s%n", index, execution.getFlowDefinitionDTO().getName());
            System.out.printf("ID: %s, start execution time: %s%n", execution.getUuid().toString(), formattedTime);
            index++;
        }
    }

    @Override
    public void setInput(String inputName, Object input) {

    }

    @Override
    public DTO getDTO() {
        return dto;
    }
}
