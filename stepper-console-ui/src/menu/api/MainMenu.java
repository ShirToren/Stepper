package menu.api;

import dto.FlowDefinitionDTO;
import dto.XMLDTO;
import exception.IntOutOfRangeException;
import menu.impl.*;
import stepper.management.StepperEngineManager;

import java.util.*;

public class MainMenu {
    private final StepperEngineManager manager;
    private final Map<MenuOption, MenuItem> menuItems;
    private final List<MenuItem> menuItemsList;
    private MenuOption currentOperation;
    private int chosenNumber;


    public MainMenu() {
        this.menuItems = new HashMap<>();
        this.menuItemsList = new ArrayList<>();
        ReadXMLMenuItem readXMLMenuItem = new ReadXMLMenuItem();
        ShowFlowDefinitionMenuItem showFlowDefinitionMenuItem = new ShowFlowDefinitionMenuItem();
        ExecuteFlowMenuItem executeFlowMenuItem = new ExecuteFlowMenuItem();
        ShowExecutionDetailsMenuItem showExecutionDetailsMenuItem = new ShowExecutionDetailsMenuItem();
        StatisticsMenuItem statisticsMenuItem = new StatisticsMenuItem();
        menuItems.put(MenuOption.READ_XML, readXMLMenuItem);
        menuItems.put(MenuOption.SHOW_FLOW_DEFINITION, showFlowDefinitionMenuItem);
        menuItems.put(MenuOption.EXECUTE_FLOW, executeFlowMenuItem);
        menuItems.put(MenuOption.SHOW_EXECUTION_DETAILS, showExecutionDetailsMenuItem);
        menuItems.put(MenuOption.STATISTICS, statisticsMenuItem);
        menuItemsList.add(readXMLMenuItem);
        menuItemsList.add(showFlowDefinitionMenuItem);
        menuItemsList.add(executeFlowMenuItem);
        menuItemsList.add(showExecutionDetailsMenuItem);
        menuItemsList.add(statisticsMenuItem);
        this.manager = new StepperEngineManager();
    }

    private void showMainMenu() {
        int index = 1;
        System.out.println("Please choose an operation: ");
        for (MenuItem menuItem : menuItemsList) {
            System.out.printf("%d. %s%n", index, menuItem.getTitle());
            index++;
        }
        System.out.printf("%d. Exit%n", index);
    }

    private void updateCurrentOperation() {
        switch (chosenNumber) {
            case 1:
                currentOperation = MenuOption.READ_XML;
                break;
            case 2:
                currentOperation = MenuOption.SHOW_FLOW_DEFINITION;
                break;
            case 3:
                currentOperation = MenuOption.EXECUTE_FLOW;
                break;
            case 4:
                currentOperation = MenuOption.SHOW_EXECUTION_DETAILS;
                break;
            case 5:
                currentOperation = MenuOption.STATISTICS;
                break;
            case 6:
                currentOperation = MenuOption.EXIT;
                break;
        }
    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (currentOperation != MenuOption.EXIT) {
            showMainMenu();
            try {
                chosenNumber = scanner.nextInt();
                if(chosenNumber < 1 | chosenNumber > 6 ) {
                    throw new IntOutOfRangeException(1,6);
                }
                updateCurrentOperation();
                runCurrentOperation(scanner);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (IntOutOfRangeException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        }
        scanner.close();
    }


    private void runCurrentOperation(Scanner scanner) {
        switch (currentOperation) {
            case READ_XML:
                menuItems.get(MenuOption.READ_XML).execute(manager);
                System.out.println();
                break;
            case SHOW_FLOW_DEFINITION:
                menuItems.get(MenuOption.SHOW_FLOW_DEFINITION).execute(manager);
                System.out.println();
                break;
            case EXECUTE_FLOW:
                menuItems.get(MenuOption.EXECUTE_FLOW).execute(manager);
                System.out.println();
                break;
            case SHOW_EXECUTION_DETAILS:
                menuItems.get(MenuOption.SHOW_EXECUTION_DETAILS).execute(manager);
                System.out.println();
                break;
            case STATISTICS:
                menuItems.get(MenuOption.STATISTICS).execute(manager);
                System.out.println();
                break;
            case EXIT:
                System.out.println("GoodBye :)");
                break;
        }
    }
}
