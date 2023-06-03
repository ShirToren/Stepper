import dd.impl.DataDefinitionRegistry;
import dd.impl.list.FileList;
import dd.impl.list.ListData;
import dto.XMLDTO;
import flow.definition.api.DataInFlow;
import flow.definition.api.FlowDefinition;
import flow.definition.api.FlowDefinitionImpl;
import flow.definition.api.StepUsageDeclarationImpl;
import flow.execution.FlowExecution;
import flow.execution.runner.FLowExecutor;
import menu.api.MainMenu;
import step.StepDefinitionRegistry;
import step.api.DataDefinitionDeclaration;
import stepper.definition.Stepper;
import stepper.definition.XMLLoader;
import stepper.management.StepperEngineManager;
import dto.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        MainMenu manu = new MainMenu();
        manu.run();
    }
}