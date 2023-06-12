package flow.definition.api;

import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;

import java.util.List;
import java.util.Map;


public interface FlowDefinition {
    String getName();

    String getDescription();

    boolean isReadOnly();

    List<StepUsageDeclaration> getFlowSteps();

    List<String> getFlowFormalOutputs();
    List<DataInFlow> getFormalOutputsDataInFlow();
    Map<String, List<String>> getFreeInputsStepTarget();
    List<DataInFlow> getFlowOutputs();
    List<DataInFlow> getFlowInputs();

    List<DataInFlow> getAllDataInFlow();
    StepUsageDeclaration findOwnerStep(String inputName);

    List<FlowLevelAlias> getFlowLevelAliases();

    List<CustomMapping> getCustomMappings();
    List<InitialInputValue> getInitialInputValues();

    boolean validateFlowStructure();

    List<DataInFlow> getFlowFreeInputs();

    Map<String, String> getDataNames();

    void addStepToFlow(StepUsageDeclaration stepUsageDeclaration);

    void addFlowLevelAlias(FlowLevelAlias flowLevelAlias);

    void addCustomMapping(CustomMapping customMapping);
    void addInitialInputValue(InitialInputValue initialInputValue);

    void applyCustomMapping();
    void applyFlowLevelAliasing();
    void initDataInFlow();

    void applyAutomaticMapping();

    void defineFlow();

    String findDataInstanceName(String dataOriginalName, String ownerStepName);
    DataInFlow findDataInFlowByNameAndOwnerStep(String dataName, String ownerStepName);

    StepUsageDeclaration getStepUsageDeclarationByFinalName(String stepName);



}
