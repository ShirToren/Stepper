package menu.impl;

import dto.DTO;
import dto.DataInFlowDTO;
import dto.FlowExecutionDTO;
import exception.IntOutOfRangeException;
import menu.api.MenuItem;
import stepper.management.StepperEngineManager;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExecuteFlowMenuItem implements MenuItem {
    private final String title;
    private FlowExecutionDTO dto;

    public ExecuteFlowMenuItem() {
        this.title = "Execute flow";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void execute(StepperEngineManager manager) {
        if(manager.isStepperLoaded()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please choose the flow: ");
            showAllFlowsInStepper(manager);
            try{
                int numOfFlow = scanner.nextInt();
                if(numOfFlow < 0 | numOfFlow > manager.getAllFlowsNames().size()) {
                    throw new IntOutOfRangeException(0,manager.getAllFlowsNames().size());
                }
                if (numOfFlow != 0) {
                    manager.createFlowExecution(manager.getAllFlowsNames().get(numOfFlow - 1));
                    int countFreeInputs = manager.countCurrentFreeInputs();
                    int numOfInput = 1;
                    while (numOfInput != 0) {
                        showFreeInputs(manager);
                        System.out.printf("Choose the number of the input to add it, %d to start execution, Or 0 to go back to the main menu%n", countFreeInputs + 1);
                        try {
                            numOfInput = scanner.nextInt();
                            if (numOfInput < 0 | numOfInput > countFreeInputs + 1) {
                                throw new IntOutOfRangeException(0, countFreeInputs + 1);
                            }
                            if (numOfInput == countFreeInputs + 1) {
                                List<String> missingInputs = manager.findMissingFreeInputs();
                                if (missingInputs.size() == 0) {
                                    dto = manager.executeFlow();
                                    System.out.println("End execution.");
                                    showFlowExecutionDetails();
                                    break;
                                } else {
                                    System.out.println("You didn't entered all mandatory inputs.");
                                    System.out.println("Missing inputs: ");
                                    for (String missingInput : manager.findMissingFreeInputs()) {
                                        System.out.println(missingInput);
                                    }
                                }
                            } else if (numOfInput != 0) {
                                addFreeInput(manager, numOfInput - 1);
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.nextLine();
                        } catch (IntOutOfRangeException e) {
                            System.out.println(e.getMessage());
                            scanner.nextLine();
                        }
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (IntOutOfRangeException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        } else {
            System.out.println("You have to load a valid xml file before.");
        }
    }

    private void showFlowExecutionDetails() {
        System.out.println("Execution unique ID: " + dto.getUuid().toString());
        System.out.println("Flow's name: " + dto.getFlowDefinitionDTO().getName());
        System.out.println("Flow execution result: " + dto.getExecutionResult());
        System.out.println("Formal outputs: ");
        for (Map.Entry<DataInFlowDTO, Object> output : dto.getExecutionFormalOutputs().entrySet()) {
            System.out.println(output.getKey().getUserString() + ": " + output.getValue());
        }
    }

    private void showFreeInputs(StepperEngineManager manager) {
        int index = 1;
        System.out.println("Free inputs:");
        for (DataInFlowDTO freeInput : manager.getCurrentFreeInputs()) {
            System.out.printf("%d. %s (%s), %s%n",
                    index, freeInput.getUserString(),
                    freeInput.getFinalName(),
                    freeInput.getDataNecessity().name());
            index++;
        }
    }

    private void addFreeInput(StepperEngineManager manager, int numOfInput) {
        Scanner scanner = new Scanner(System.in);
        List<DataInFlowDTO> currentFreeInputs = manager.getCurrentFreeInputs();
        DataInFlowDTO inputDataInFlow = currentFreeInputs.get(numOfInput);
        String inputName = inputDataInFlow.getFinalName();
        String stepName = inputDataInFlow.getOwnerStep().getName();
        System.out.println("Please enter " + currentFreeInputs.get(numOfInput).getUserString() + " (" + inputName + ")");
        Object userInput = null;
        if(inputDataInFlow.getDataDefinitionDTO().getType() == String.class) {
            userInput = scanner.nextLine();
        } else if(inputDataInFlow.getDataDefinitionDTO().getType() == Integer.class) {
            userInput = scanner.nextInt();
        } else {
            userInput = scanner.nextDouble();
        }
        manager.addFreeInputToFlowExecution(inputName + "." + stepName , userInput);
    }

    @Override
    public void setInput(String inputName, Object input) {

    }

    @Override
    public DTO getDTO() {
        return dto;
    }

    private void showAllFlowsInStepper(StepperEngineManager manager) {
        int index = 1;
        for (String flowName: manager.getAllFlowsNames()) {
            System.out.println(index + ". " + flowName);
            index++;
        }
        System.out.println("0. Back");
    }
}
