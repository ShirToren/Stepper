package stepper.definition;
import exception.DoubleFlowNameException;
import exception.StepNotExistException;
import flow.definition.api.*;
import flow.definition.api.continuations.Continuation;
import flow.definition.api.continuations.ContinuationMapping;
import flow.definition.api.continuations.Continuations;
import jaxb.schema.generated.ex02.*;
import step.StepDefinitionRegistry;
import step.api.StepDefinition;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XMLLoader {
    private final static String JAXB_XML_PACKAGE_NAME = "jaxb.schema.generated.ex02";

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
    public InitialInputValue createInitialInputValueFromJAXB(STInitialInputValue jaxbInitialInputValue) {
        return new InitialInputValue(jaxbInitialInputValue.getInputName(),
                jaxbInitialInputValue.getInitialValue());
    }

    private Continuations createContinuationsFromJaxb(STContinuations jaxbContinuations){
        List<Continuation> continuations = new ArrayList<>();
        for (STContinuation jaxbContinuation: jaxbContinuations.getSTContinuation()) {
            List<ContinuationMapping> list = new ArrayList<>();
            for (STContinuationMapping jaxbMapping: jaxbContinuation.getSTContinuationMapping()) {
                list.add(new ContinuationMapping(jaxbMapping.getSourceData(),
                        jaxbMapping.getTargetData()));
            }
            continuations.add(new Continuation(jaxbContinuation.getTargetFlow(),
                    list));
        }
        return new Continuations(continuations);
    }
    public FlowDefinitionImpl createFlowFromJAXB(STFlow jaxbFlow) {
        FlowDefinitionImpl theFlow;
        String[] outputs = jaxbFlow.getSTFlowOutput().split(",");

        if(jaxbFlow.getSTContinuations() != null){
            Continuations continuationsFromJaxb = createContinuationsFromJaxb(jaxbFlow.getSTContinuations());
            theFlow = new FlowDefinitionImpl(
                    jaxbFlow.getName(), jaxbFlow.getSTFlowDescription(), continuationsFromJaxb);
        }else {
            theFlow = new FlowDefinitionImpl(
                    jaxbFlow.getName(), jaxbFlow.getSTFlowDescription(), null);
        }

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
        if(jaxbFlow.getSTInitialInputValues() != null){
            List<STInitialInputValue> stInitialInputValue = jaxbFlow.getSTInitialInputValues().getSTInitialInputValue();
            for (STInitialInputValue initValue: stInitialInputValue) {
                theFlow.addInitialInputValue(createInitialInputValueFromJAXB(initValue));
            }
        }

        theFlow.defineFlow();
        return theFlow;
    }

    public Stepper createStepperFromJAXB(STStepper jaxbStepper) {
        Stepper stepper = new Stepper(jaxbStepper.getSTThreadPool());
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
    public Stepper loadStepperFromXMLFile(InputStream inputStream) throws JAXBException{
        STStepper jaxbStepper = readXMLFile(inputStream);
        return createStepperFromJAXB(jaxbStepper);
    }

    public STStepper readXMLFile(String fileName) throws FileNotFoundException, JAXBException {
            InputStream inputStream = new FileInputStream(new File(fileName));
            return deserializeFrom(inputStream);
    }
    public STStepper readXMLFile(InputStream inputStream) throws JAXBException {
        return deserializeFrom(inputStream);
    }
    private STStepper deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (STStepper)u.unmarshal(in);
    }
}
