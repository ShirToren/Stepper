package menu.impl;

import dto.DTO;
import dto.XMLDTO;
import menu.api.MenuItem;
import menu.api.MenuOption;
import stepper.management.StepperEngineManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ReadXMLMenuItem implements MenuItem {
    private final String title;
    private XMLDTO dto;
    private String xmlFileName;

    public ReadXMLMenuItem() {
        this.title = "Read system information file";
    }

    @Override
    public String getTitle() {
        return title;
    }


    @Override
    public void execute(StepperEngineManager manager) {
        System.out.println("Please enter full xml file path");
        Scanner scanner = new Scanner(System.in);
        try {
            String filePath = scanner.nextLine();
            dto = manager.readSystemInformationFile(filePath);
            showXMLDTODetails();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a string");
            scanner.nextLine();
        }
    }

    private void showXMLDTODetails() {
        System.out.println(dto.getFileState());
    }

    @Override
    public void setInput(String inputName, Object input) {
        this.xmlFileName = (String)input;
    }

    @Override
    public DTO getDTO() {
        return dto;
    }
}
