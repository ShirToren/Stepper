package flow.definition.api;

import dd.api.DataDirection;
import step.api.DataDefinitionDeclaration;

import java.util.*;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private final List<String> formalOutputs;
    private final List<StepUsageDeclaration> steps;
    private final List<FlowLevelAlias> flowLevelAliases;

    private final List<CustomMapping> customMappings;

    private final Map<String, String> dataNames;
    private final List<DataInFlow> allDataInFlow;

    private final List<DataInFlow> freeInputs;
    private final List<String> uniqueFreeInputs;
    private final Map<String, List<String>> freeInputsStepTarget;

    private boolean isReadOnly;
    private final List<DataInFlow> flowOutputs;
    private final List<DataInFlow> flowInputs;
    private final List<DataInFlow> formalOutputsDataInFlow;

    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        this.formalOutputs = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.dataNames = new HashMap<>();
        this.flowLevelAliases = new ArrayList<>();
        this.customMappings = new ArrayList<>();
        this.allDataInFlow = new ArrayList<>();
        this.freeInputs = new ArrayList<>();
        this.flowOutputs = new ArrayList<>();
        this.formalOutputsDataInFlow = new ArrayList<>();
        this.freeInputsStepTarget = new HashMap<>();
        this.uniqueFreeInputs = new ArrayList<>();
        this.flowInputs = new ArrayList<>();
    }

    public List<String> getUniqueFreeInputs() {
        return uniqueFreeInputs;
    }

    public void initDataNames() {
        for(StepUsageDeclaration step : steps) {
            for(DataDefinitionDeclaration output : step.getStepDefinition().outputs()) {
                dataNames.put(output.getName() + "." + step.getFinalStepName(), output.getName());
            }
            for (DataDefinitionDeclaration input : step.getStepDefinition().inputs()) {
                dataNames.put(input.getName() + "." + step.getFinalStepName(), input.getName());
            }
        }
    }

    @Override
    public Map<String, List<String>> getFreeInputsStepTarget() {
        return freeInputsStepTarget;
    }

    @Override
    public void initDataInFlow() {
        int index = 1;
        for(StepUsageDeclaration step : steps) {
            for(DataDefinitionDeclaration output : step.getStepDefinition().outputs()) {
                DataInFlow currentOutput = new DataInFlowImpl(
                        Integer.toString(index),
                        output.getName(),
                        output.getName(),
                        DataDirection.OUTPUT,
                        output.dataDefinition(),
                        step,
                        output);
                allDataInFlow.add(currentOutput);
                flowOutputs.add(currentOutput);
                index++;
            }
            for (DataDefinitionDeclaration input : step.getStepDefinition().inputs()) {
                DataInFlow currentInput = new DataInFlowImpl(
                        Integer.toString(index),
                        input.getName(),
                        input.getName(),
                        DataDirection.INPUT,
                        input.dataDefinition(),
                        step,
                        input);
                allDataInFlow.add(currentInput);
                flowInputs.add(currentInput);
                index++;
            }
        }
    }

    @Override
    public String findDataInstanceName(String dataOriginalName, String ownerStepName) {
        String name = dataNames.get(dataOriginalName + "." + ownerStepName);
        return name;
    }

    private void findFreeInputs() {
        for (DataInFlow data : allDataInFlow) {
            if(data.getDataDirection() == DataDirection.INPUT &&
                    data.getSourceDataInFlow().size() == 0) {
                freeInputs.add(data);
                if(!uniqueFreeInputs.contains(data.getDataInstanceName())) {
                    uniqueFreeInputs.add(data.getDataInstanceName());
                }
                dataNames.put(data.getDataInstanceName() + "." + data.getOwnerStepUsageDeclaration().getFinalStepName(), data.getDataInstanceName());
                if(freeInputsStepTarget.containsKey(data.getDataInstanceName())) {
                    freeInputsStepTarget.get(data.getDataInstanceName()).add(data.getOwnerStepUsageDeclaration().getFinalStepName());
                } else {
                    freeInputsStepTarget.put(data.getDataInstanceName(), new ArrayList<>());
                    freeInputsStepTarget.get(data.getDataInstanceName()).add(data.getOwnerStepUsageDeclaration().getFinalStepName());
                }
            }
        }
    }

    @Override
    public void applyAutomaticMapping() {
        for (int i = 0; i < steps.size(); i++) {
            List<DataDefinitionDeclaration> currentOutputsList =
                    steps.get(i).getStepDefinition().outputs();
            for (int j = i + 1; j < steps.size(); j++) {
                List<DataDefinitionDeclaration> currentInputsList =
                        steps.get(j).getStepDefinition().inputs();
                for (DataDefinitionDeclaration output : currentOutputsList) {
                    for (DataDefinitionDeclaration input : currentInputsList) {
                        String outputName = findDataInstanceName(output.getName(), steps.get(i).getFinalStepName());
                        String inputName = findDataInstanceName(input.getName(), steps.get(j).getFinalStepName());
                        if (outputName.equals(inputName) &&
                                output.dataDefinition().getType().equals(input.dataDefinition().getType())) {
                            addDataSourceByAutomatic(output.getName(),
                                    steps.get(i).getFinalStepName(),
                                    input.getName(),
                                    steps.get(j).getFinalStepName());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void defineFlow() {
        initDataNames();
        initDataInFlow();
        applyFlowLevelAliasing();
        applyCustomMapping();
        applyAutomaticMapping();
        findFreeInputs();
        checkIfFlowIsReadOnly();
        validateFlowStructure();
        initFormalOutputsDataInFlow();
    }

    private void checkIfFlowIsReadOnly(){
        for (StepUsageDeclaration step : steps) {
            if(!step.getStepDefinition().isReadonly()){
                this.isReadOnly = false;
            }
        }
        this.isReadOnly = true;
    }


    @Override
    public  void applyFlowLevelAliasing() {
        for (FlowLevelAlias alias: flowLevelAliases) {
            for (DataInFlow data: allDataInFlow) {
                if (data.getOriginalDataInstanceNameInStep().equals(alias.getSourceDataName()) &&
                        data.getOwnerStepUsageDeclaration().getFinalStepName().equals(alias.getStepName())) {
                    data.setDataInstanceName(alias.getAlias());
                    dataNames.replace(data.getOriginalDataInstanceNameInStep() + "." + data.getOwnerStepUsageDeclaration().getFinalStepName() , alias.getAlias());
                }
            }
        }
    }
    private void addDataSourceByCustom(String sourceDataName, String sourceStepName, String targetDataName, String targetStepName) {
        DataInFlow sourceDataInFlow =
                findDataInFlowByNameAndOwnerStep(sourceDataName, sourceStepName);
        DataInFlow targetDataInFlow =
                findDataInFlowByNameAndOwnerStep(targetDataName, targetStepName);
        assert targetDataInFlow != null;
        assert sourceDataInFlow != null;
        targetDataInFlow.getSourceDataInFlow().add(sourceDataInFlow);
        sourceDataInFlow.getTargetDataInFlow().add(targetDataInFlow);
        //check same type
    }
    private void addDataSourceByAutomatic(String sourceDataName, String sourceStepName, String targetDataName, String targetStepName) {
        DataInFlow sourceDataInFlow =
                findDataInFlowByOriginalNameAndOwnerStep(sourceDataName, sourceStepName);
        DataInFlow targetDataInFlow =
                findDataInFlowByOriginalNameAndOwnerStep(targetDataName, targetStepName);
        assert targetDataInFlow != null;
       // assert sourceDataInFlow != null;
        targetDataInFlow.getSourceDataInFlow().add(sourceDataInFlow);
        //sourceDataInFlow.getTargetDataInFlow().add(targetDataInFlow);
        //check same type
    }
    @Override
    public void applyCustomMapping() {
        for (CustomMapping customMapping : customMappings) {
            addDataSourceByCustom(customMapping.getSourceData(),
                    customMapping.getSourceStep(),
                    customMapping.getTargetData(),
                    customMapping.getTargetStep());
        }
    }

    @Override
    public DataInFlow findDataInFlowByNameAndOwnerStep(String dataName, String ownerStepName) {
        for (DataInFlow data: allDataInFlow) {
            if(data.getOwnerStepUsageDeclaration().getFinalStepName().equals(ownerStepName) &&
            data.getDataInstanceName().equals(dataName)) {
                return data;
            }
        }
        return null;
    }

    @Override
    public StepUsageDeclaration getStepUsageDeclarationByFinalName(String stepName) {
        for (StepUsageDeclaration step : steps) {
            if(step.getFinalStepName().equals(stepName))
                return step;
        }
        return null;
    }

    private DataInFlow findDataInFlowByOriginalNameAndOwnerStep(String dataOriginalName, String ownerStepName) {
        for (DataInFlow data: allDataInFlow) {
            if(data.getOwnerStepUsageDeclaration().getFinalStepName().equals(ownerStepName) &&
                    data.getOriginalDataInstanceNameInStep().equals(dataOriginalName)) {
                return data;
            }
        }
        return null;
    }

    public void addFormalOutput(String outputName) {
        formalOutputs.add(outputName);
    }


    @Override
    public boolean validateFlowStructure() {
        boolean result = true;
        for (DataInFlow mandatoryInput : freeInputs) {
            if (!mandatoryInput.getDataDefinition().isUserFriendly()) {
                ////mandatory input not  user-friendly
                return false;
            }
        }
        boolean hasDuplicatOutputName = false;
        Set<String> values = new HashSet<>();

        for (DataInFlow data : allDataInFlow) {
            if (data.getDataDirection() == DataDirection.OUTPUT &&
                    values.contains(data.getDataInstanceName())) {
                hasDuplicatOutputName = true;
                break;
            } else if (data.getDataDirection() == DataDirection.OUTPUT) {
                values.add(data.getDataInstanceName());
            }
        }
        if (hasDuplicatOutputName) {
            //duplicate output name
            return false;
        }

        return validateCustomMapping() && validateFlowLevelAliasing() && validateFormalOutputs() && validateMandatoryInput();
    }

    private boolean validateMandatoryInput(){
        Map<String, Class<?>> map = new HashMap<>();
        for (DataInFlow freeInput : freeInputs) {
            if(!map.containsKey(freeInput.getDataInstanceName())) {
                map.put(freeInput.getDataInstanceName(), freeInput.getDataDefinition().getType());
            } else {
                if(!map.get(freeInput.getDataInstanceName()).equals(freeInput.getDataDefinition().getType())){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateFormalOutputs(){
        boolean result = true;
        for (String outputName: formalOutputs) {
            DataInFlow flowOutput = findDataInFlowByNameAfterAlias(outputName);
            if(flowOutput == null) {
                /// flow output not exist
                result = false;
            }
        }
        return result;
    }

    private boolean validateFlowLevelAliasing() {
        boolean result = true;
        for (FlowLevelAlias flowLevelAlias : flowLevelAliases) {
            DataInFlow data = findDataInFlowByOriginalNameAndOwnerStep(flowLevelAlias.getSourceDataName(), flowLevelAlias.getStepName());
            StepUsageDeclaration ownerStep = getStepUsageDeclarationByFinalName(flowLevelAlias.getStepName());
            if (data == null) {
                ////data not exist
                result = false;
            }
            if (ownerStep == null) {
                ///step not exist
                result = false;
            }
        }
        return result;
    }


    private boolean validateCustomMapping() {
        boolean result = true;
        for (CustomMapping customMapping : customMappings) {
            DataInFlow sourceDataInFlow = findDataInFlowByNameAndOwnerStep(customMapping.getSourceData(), customMapping.getSourceStep());
            DataInFlow targetDataInFlow = findDataInFlowByNameAndOwnerStep(customMapping.getTargetData(), customMapping.getTargetStep());
            StepUsageDeclaration sourceStep = getStepUsageDeclarationByFinalName(customMapping.getSourceStep());
            StepUsageDeclaration targetStep = getStepUsageDeclarationByFinalName(customMapping.getTargetStep());

            if(sourceStep == null | targetStep == null)
            {
                ////step not exist
                result = false;
            }
            if (steps.indexOf(sourceStep) > steps.indexOf(targetStep)) {
                ///not valid order of steps
                result = false;
            }
            if(sourceDataInFlow == null | targetDataInFlow == null) {
                /////data not exist
                result = false;
            } else if (sourceDataInFlow.getDataDefinition().getType() !=
                    targetDataInFlow.getDataDefinition().getType()) {
                /////not same type
                result = false;
            }

        }
        return result;
    }

    private DataInFlow findDataInFlowByNameAfterAlias(String dataName) {
        for (DataInFlow data : allDataInFlow) {
            if(data.getDataInstanceName().equals(dataName)) {
                return data;
            }
        }
        return null;
    }



    @Override
    public List<DataInFlow> getFlowFreeInputs() {
        return freeInputs;
    }

    @Override
    public Map<String, String> getDataNames() {
        return dataNames;
    }

    @Override
    public void addStepToFlow(StepUsageDeclaration stepUsageDeclaration) {
        steps.add(stepUsageDeclaration);
    }

    @Override
    public void addFlowLevelAlias(FlowLevelAlias flowLevelAlias) {
        this.flowLevelAliases.add(flowLevelAlias);
    }

    @Override
    public void addCustomMapping(CustomMapping customMapping) {
        this.customMappings.add(customMapping);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return steps;
    }


    @Override
    public List<String> getFlowFormalOutputs() {
        return formalOutputs;
    }

    @Override
    public List<DataInFlow> getFormalOutputsDataInFlow() {
        return formalOutputsDataInFlow;
    }

    private void initFormalOutputsDataInFlow() {
        for (String output : formalOutputs) {
            formalOutputsDataInFlow.add(findDataInFlowByNameAfterAlias(output));
        }
    }

    @Override
    public List<DataInFlow> getFlowOutputs() {
        return flowOutputs;
    }

    @Override
    public List<DataInFlow> getFlowInputs() {
        return flowInputs;
    }

    @Override
    public List<DataInFlow> getAllDataInFlow() {
        return allDataInFlow;
    }

    @Override
    public List<FlowLevelAlias> getFlowLevelAliases() {
        return flowLevelAliases;
    }

    @Override
    public List<CustomMapping> getCustomMappings() {
        return customMappings;
    }
}
