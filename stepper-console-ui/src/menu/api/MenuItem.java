package menu.api;

import dto.DTO;
import stepper.management.StepperEngineManager;

import java.util.Scanner;

public interface MenuItem {
    String getTitle();
    void execute(StepperEngineManager manager);
    void setInput(String inputName, Object input);

    DTO getDTO();
}
