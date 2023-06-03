package stepper.definition;
import exception.DoubleFlowNameException;
import exception.StepNotExistException;
import flow.definition.api.*;
import jaxb.schema.generated.*;
import step.StepDefinitionRegistry;
import step.api.StepDefinition;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class XMLLoader {
    private final static String JAXB_XML_PACKAGE_NAME = "jaxb.schema.generated";

    public StepUsageDeclarationImpl createStepFromJAXB(STStepInFlow jaxbStep){
        String nameAsEnum = jaxbStep.getName().toUpperCase().replaceAll(" ", "_");

        StepUsageDeclarationImpl step;
        try {
            StepDefinition stepDefinition = StepDefinitionRegistry.valueOf(nameAsEnum);
            stepDefinition.setName(jaxbStep.getName());

            if(jaxbStep.isContinueIfFailing() != null && jaxbStep.getAlias() != null) {
                step = new StepUsageDeclarationImpl
                        (stepDefinition,
                                jaxbStep.isContinueIfFailing(),
                                jaxbStep.getAlias());
            } else if (jaxbStep.getAlias() != null) {
                step = new StepUsageDeclarationImpl
                        (stepDefinition,
                                jaxbStep.getAlias());
            }
            else {
                step = new StepUsageDeclarationImpl
                        (stepDefinition);
            }
        } catch (IllegalArgumentException e){
            throw new StepNotExistException(jaxbStep.getName());
        }
        return step;
    }

    public FlowLevelAlias createFlowLevelAliasFromJAXB(STFlowLevelAlias jaxbAlias) {
        return new FlowLevelAlias(
                jaxbAlias.getStep(), jaxbAlias.getSourceDataName(), jaxbAlias.getAlias());
    }

    public CustomMapping createCustomMappingFromJAXB(STCustomMapping jaxbCustomMapping) {
        return new CustomMapping(
                jaxbCustomMapping.getSourceStep(),
                jaxbCustomMapping.getSourceData(),
                jaxbCustomMapping.getTargetStep(),
                jaxbCustomMapping.getTargetData());
    }
    public FlowDefinitionImpl createFlowFromJAXB(STFlow jaxbFlow) {
        String[] outputs = jaxbFlow.getSTFlowOutput().split(",");

        FlowDefinitionImpl theFlow = new FlowDefinitionImpl(
                jaxbFlow.getName(), jaxbFlow.getSTFlowDescription());
        for (STStepInFlow step : jaxbFlow.getSTStepsInFlow().getSTStepInFlow()) {
            theFlow.addStepToFlow(createStepFromJAXB(step)); }
        for (String output: outputs) {
            theFlow.addFormalOutput(output);
        }
        if(jaxbFlow.getSTFlowLevelAliasing() != null) {
            for (STFlowLevelAlias alias : jaxbFlow.getSTFlowLevelAliasing().getSTFlowLevelAlias()) {
                theFlow.addFlowLevelAlias(createFlowLevelAliasFromJAXB(alias));
            }
        }
        if(jaxbFlow.getSTCustomMappings() != null){
            for (STCustomMapping customMapping: jaxbFlow.getSTCustomMappings().getSTCustomMapping()) {
                theFlow.addCustomMapping(createCustomMappingFromJAXB(customMapping));
            }
        }
        theFlow.defineFlow();
        return theFlow;
    }

    public Stepper createStepperFromJAXB(STStepper jaxbStepper) {
        Stepper stepper = new Stepper();
        Set<String> values = new HashSet<>();

        for (STFlow flow : jaxbStepper.getSTFlows().getSTFlow()) {
            if(values.contains(flow.getName())) {
                throw new DoubleFlowNameException(flow.getName());
            } else {
                values.add(flow.getName());
            }
            stepper.addFlowToStepper(createFlowFromJAXB(flow));
        }
        return stepper;
    }

    public Stepper loadStepperFromXMLFile(String fileName) throws FileNotFoundException, JAXBException{
        STStepper jaxbStepper = readXMLFile(fileName);
        return createStepperFromJAXB(jaxbStepper);
    }

    public STStepper readXMLFile(String fileName) throws FileNotFoundException, JAXBException {
            InputStream inputStream = new FileInputStream(new File(fileName));
            return deserializeFrom(inputStream);
    }
    private STStepper deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (STStepper)u.unmarshal(in);
    }
}
