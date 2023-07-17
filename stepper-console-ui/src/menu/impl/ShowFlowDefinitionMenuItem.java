package menu.impl;

import dto.DTO;
import dto.DataInFlowDTO;
import dto.FlowDefinitionDTO;
import dto.StepUsageDeclarationDTO;
import exception.IntOutOfRangeException;
import menu.api.MenuItem;
import menu.api.MenuOption;
import stepper.management.StepperEngineManager;

import java.util.*;

public class ShowFlowDefinitionMenuItem implements MenuItem {
    private final String title;
    private FlowDefinitionDTO dto;
    private String flowName;

    public ShowFlowDefinitionMenuItem() {
        this.title = "Show flow definition";
    }

    private void showFlowsStepsDTODetails() {
        System.out.println("Flow's steps:");
        for (StepUsageDeclarationDTO step : dto.getSteps()) {
            if(!step.getOriginalName().equals(step.getName())) {
                System.out.printf("%s (%s), %s%n",
                        step.getName(), step.getOriginalName(),
                        step.isReadOnly() ? "Read only" : "Not Read only");
            } else {
                System.out.printf("%s, %s%n",
                        step.getName(),
                        step.isReadOnly() ? "Read only" : "Not Read only");
            }
        }
    }

    private void showFreeInputsDTODetails() {
        System.out.println("Flow's free inputs:");
        for (DataInFlowDTO freeInput: dto.getFreeInputs()) {
            StringBuilder relatedSteps = new StringBuilder();
            for (String step: dto.getFreeInputsStepTarget().get(freeInput.getFinalName())) {
                relatedSteps.append(step).append(", ");
            }
            relatedSteps.delete(relatedSteps.length() - 2, relatedSteps.length());
            System.out.printf("Name: %s, Type: %s, Necessity: %s,%n",
                    freeInput.getFinalName(),
                    freeInput.getDataDefinitionDTO().getType().getName()
                            .substring(freeInput.getDataDefinitionDTO().getType().getName().lastIndexOf(".") + 1),
                    freeInput.getDataNecessity().name());
            System.out.printf("related steps: %s%n", relatedSteps);
        }
    }

    private void showFlowOutputsDTODetails(){
        System.out.println("Flow's outputs:");
        for (DataInFlowDTO output : dto.getFlowsOutputs()) {
            System.out.println(String.format("Name: %s, Type: %s, From step: %s",
                    output.getFinalName(),
                    output.getDataDefinitionDTO().getType().getName()
                            .substring(output.getDataDefinitionDTO().getType().getName().lastIndexOf("." ) +1),
                    output.getOwnerStep().getName()));
        }
    }

    private void showFlowDefinitionDTODetails(){
        System.out.println("Name: " + dto.getName());
        System.out.println("Description: " + dto.getDescription());
        System.out.println("Formal outputs:");
        for (DataInFlowDTO output :dto.getFlowsFormalOutputs()) {
            System.out.println(output.getFinalName());
        }
        System.out.printf("This flow is %s%n",
                dto.isReadOnly() ? "read only" : "not read only");
        showFlowsStepsDTODetails();
        showFreeInputsDTODetails();
        showFlowOutputsDTODetails();
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
            try {
                int numOfFlow = scanner.nextInt();
                if(numOfFlow < 0 | numOfFlow > manager.getAllFlowsNames().size()) {
                    throw new IntOutOfRangeException(0 ,manager.getAllFlowsNames().size());
                }
                if (numOfFlow != 0) {
                   // dto = manager.showFlowDefinition(manager.getAllFlowsNames().get(numOfFlow - 1));
                    showFlowDefinitionDTODetails();
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (IntOutOfRangeException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        }
        else {
            System.out.println("You have to load a valid xml file before.");
        }
    }

    private void showAllFlowsInStepper(StepperEngineManager manager) {
        int index = 1;
        for (String flowName: manager.getAllFlowsNames()) {
            System.out.println(index + ". " + flowName);
            index++;
        }
        System.out.println("0. Back");
    }

    @Override
    public void setInput(String inputName, Object input) {

    }

    @Override
    public DTO getDTO() {
        return dto;
    }
}
