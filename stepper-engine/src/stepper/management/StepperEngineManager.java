package stepper.management;

import dto.*;
import exception.DoubleFlowNameException;
import exception.StepNotExistException;
import flow.definition.api.DataInFlow;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import flow.execution.runner.FLowExecutor;
import step.api.DataNecessity;
import stepper.definition.Stepper;
import stepper.definition.XMLLoader;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StepperEngineManager {
    private Stepper stepper;
    private final XMLLoader loader;
    private final FLowExecutor fLowExecutor;
    private FlowExecution currentFlowExecution;
    private final List<FlowExecution> allFlowExecutionsList;
    private final Map<UUID, FlowExecution> allFlowExecutionsMap;
    private final List<FlowExecutionDTO> allExecutionsDTO;
    private final Map<String, Integer> flowExecutedTimes;
    private final Map<String, Long> flowExecutedTotalMillis;
    private final Map<String, Integer> stepExecutedTimes;
    private final  Map<String, Long> stepExecutedTotalMillis;
    private ExecutorService executor;


    public StepperEngineManager() {
        this.loader = new XMLLoader();
        this.fLowExecutor = new FLowExecutor();
        this.allExecutionsDTO = new ArrayList<>();
        this.flowExecutedTimes = new HashMap<>();
        this.stepExecutedTimes = new HashMap<>();
        this.flowExecutedTotalMillis = new HashMap<>();
        this.stepExecutedTotalMillis = new HashMap<>();
        this.allFlowExecutionsMap = new ConcurrentHashMap<>();
        this.allFlowExecutionsList = Collections.synchronizedList(new ArrayList<>());
        this.executor = Executors.newFixedThreadPool(5);
    }

    public FlowDefinitionDTO showFlowDefinition(String flowName) {
        FlowDefinition flow = stepper.findFlowDefinitionByName(flowName);
        return new FlowDefinitionDTO(flow);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public int countCurrentFreeInputs(){
        Set<String> set = new HashSet<>();
        for (DataInFlow input : currentFlowExecution.getFlowDefinition().getFlowFreeInputs()) {
            set.add(input.getDataInstanceName());
        }
        return set.size();
    }

    public List<FlowDefinition> getAllFlowsDefinitionInStepper(){
        return stepper.getFlows();
    }

    private void cleanAllSystem(){
        synchronized (this) {
            currentFlowExecution = null;
            allFlowExecutionsList.clear();
            allExecutionsDTO.clear();
            flowExecutedTimes.clear();
            flowExecutedTotalMillis.clear();
            stepExecutedTimes.clear();
            stepExecutedTotalMillis.clear();
            allFlowExecutionsMap.clear();
        }
    }

    public void copyFreeInputsValues(UUID sourceID, UUID targetID) {
        FlowExecution sourceExecution = allFlowExecutionsMap.get(sourceID);
        FlowExecution targetExecution = allFlowExecutionsMap.get(targetID);
        for (Map.Entry<String, Object> input: sourceExecution.getFreeInputs().entrySet()) {
            targetExecution.addFreeInput(input.getKey(), input.getValue());
        }
    }

    public List<FlowExecutionDTO> getAllExecutionsDTO(){
        return allExecutionsDTO;
    }

    public List<String> getExecutedStepsDefinitionsNames(){
        Set<String> values = new HashSet<>();
        List<String> executedSteps = new ArrayList<>();
        for (FlowExecution execution : allFlowExecutionsList) {
            for (StepUsageDeclaration step : execution.getFlowDefinition().getFlowSteps()) {
                if(!values.contains(step.getStepDefinition().getName())) {
                    values.add(step.getStepDefinition().getName());
                    executedSteps.add(step.getStepDefinition().getName());
                }
            }
        }
        return executedSteps;
    }

    public List<FlowDefinitionDTO> getAllFlowDefinitionsInStepper(){
        List<FlowDefinitionDTO> allFlowDefinitionsInStepper = new ArrayList<>();
        if(stepper != null) {
            for (FlowDefinition flow: stepper.getFlows()) {
                allFlowDefinitionsInStepper.add(new FlowDefinitionDTO(flow));
            }
        }
        return allFlowDefinitionsInStepper;
    }

    private void addExecutionDTO(FlowExecutionDTO dto){
        allExecutionsDTO.add(0, dto);
    }

    public FlowExecution getCurrentFlowExecution() {
        return allFlowExecutionsList.get(0);
    }
    public boolean isStepperLoaded(){
        return stepper != null;
    }

    public List<String> findMissingFreeInputs(){
        List<String> missingInputs = new ArrayList<>();
        for (DataInFlow input : currentFlowExecution.getFlowDefinition().getFlowFreeInputs()) {
            if(input.getDataDefinitionDeclaration().necessity() == DataNecessity.MANDATORY &&
            !currentFlowExecution.getFreeInputs().containsKey(input.getDataInstanceName() + "." + input.getOwnerStepUsageDeclaration().getFinalStepName())) {
                missingInputs.add(input.getDataInstanceName());
            }
        }
        return missingInputs;
    }

    public Map<String, Object> getActualFreeInputsList() {
        return new HashMap<>(currentFlowExecution.getFreeInputs());
    }
    public Map<String, Object> getActualFreeInputsList(UUID id) {
        return new HashMap<>(allFlowExecutionsMap.get(id).getFreeInputs());
    }

    public List<String> getAllFlowsNames() {
        List<String> names = new ArrayList<>();
        for (FlowDefinition flow : stepper.getFlows()) {
            names.add(flow.getName());
        }
        return  names;
    }

    public UUID createFlowExecution(String flowName) {
        UUID id = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(id,
                stepper.findFlowDefinitionByName(flowName));
            this.allFlowExecutionsList.add(0, flowExecution);
            this.allFlowExecutionsMap.put(id, flowExecution);
        //currentFlowExecution = flowExecution;
        return id;
    }

    public void setCurrentFlowExecution(FlowExecutionDTO currentFlowExecution){
        synchronized (this) {
            for (FlowExecution execution: allFlowExecutionsList) {
                if(currentFlowExecution.getUuid().equals(execution.getUuid())) {
                    this.currentFlowExecution = execution;
                }
            }
        }
    }

    public List<FlowExecutionDTO> getAllFlowExecutionsDTO() {
        List<FlowExecutionDTO> result = new ArrayList<>();
        for (FlowExecution execution: allFlowExecutionsList) {
            result.add(new FlowExecutionDTO(execution, new FlowDefinitionDTO(execution.getFlowDefinition())));
        }
        return result;
    }

    public void addFreeInputToFlowExecution(UUID id, String inputName, Object value) {
        allFlowExecutionsMap.get(id).addFreeInput(inputName, value);
        //currentFlowExecution.addFreeInput(inputName, value);
    }
    public void addFreeInputToFlowExecution(String inputName, Object value) {
        currentFlowExecution.addFreeInput(inputName, value);
    }

    public List<FlowExecution> getAllFlowExecutionsList() {
        return allFlowExecutionsList;
    }

    public FlowExecutionDTO executeFlow() {
        Instant start = Instant.now();
        fLowExecutor.executeFlow(allFlowExecutionsList.get(0));
        currentFlowExecution.setFinished(true);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        currentFlowExecution.setTotalTime(duration);
        FlowExecutionDTO dto = new FlowExecutionDTO(currentFlowExecution, new FlowDefinitionDTO(currentFlowExecution.getFlowDefinition()));
        addExecutionDTO(dto);
        addExecutionToStatistics(dto);
        return dto;
    }

    public void executeFlow(UUID id) {
        Instant start = Instant.now();
        FlowExecution flowExecution = allFlowExecutionsMap.get(id);
        fLowExecutor.executeFlow(flowExecution);
        flowExecution.setFinished(true);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        flowExecution.setTotalTime(duration);
        addExecutionToStatistics(getExecutionDTOByUUID(id));
    }

    public XMLDTO readSystemInformationFile(String fileName) {
        XMLDTO dto;
        Stepper stepper;
        if (fileName.endsWith(".xml")) {
            try {
                stepper = loader.loadStepperFromXMLFile(fileName);
                if (stepper.validateStepperStructure()) {
                    dto = new XMLDTO("The file is valid and fully loaded.");
                    this.stepper = stepper;
                    if(stepper.getThreadPool() != 0){
                        this.executor = Executors.newFixedThreadPool(stepper.getThreadPool());
                    }
                    cleanAllSystem();
                } else {
                    dto = new XMLDTO("The file is not valid. One or more flows are not valid.");
                }
            } catch (FileNotFoundException | JAXBException e) {
                dto = new XMLDTO(String.format("The file is not valid. Error: %s", e.getMessage()));
            } catch (StepNotExistException e) {
                dto = new XMLDTO(e.getMessage());
            } catch (DoubleFlowNameException e) {
                dto = new XMLDTO(e.getMessage());
            }

        } else {
            dto = new XMLDTO("The file is not an XML file.");
        }
        return dto;
    }

    public List<DataInFlowDTO> getCurrentFreeInputs(){
        List<DataInFlowDTO> freeInputs = new ArrayList<>();
        for (DataInFlow input : currentFlowExecution.getFlowDefinition().getFlowFreeInputs()) {
            freeInputs.add(new DataInFlowDTO(input));
        }
        return freeInputs;
    }

    public void addExecutionToStatistics(FlowExecutionDTO execution) {
        if(!flowExecutedTimes.containsKey(execution.getFlowDefinitionDTO().getName())) {
            flowExecutedTimes.put(execution.getFlowDefinitionDTO().getName(), 1);
        } else {
            int numOfExecutions = flowExecutedTimes.get(execution.getFlowDefinitionDTO().getName());
            flowExecutedTimes.replace(execution.getFlowDefinitionDTO().getName(), numOfExecutions + 1);
        }
        if(!flowExecutedTotalMillis.containsKey(execution.getFlowDefinitionDTO().getName())) {
            flowExecutedTotalMillis.put(execution.getFlowDefinitionDTO().getName(), execution.getTotalTime().toMillis());
        }
        else {
            long totalTime = flowExecutedTotalMillis.get(execution.getFlowDefinitionDTO().getName());
            flowExecutedTotalMillis.replace(execution.getFlowDefinitionDTO().getName(), totalTime + execution.getTotalTime().toMillis());
        }

        List<StepUsageDeclarationDTO> stepsInExecution = execution.getFlowDefinitionDTO().getSteps();
        for (StepUsageDeclarationDTO step : stepsInExecution) {
            if (execution.getStepsTotalTimes().get(step.getName()) != null) {
                if (!stepExecutedTimes.containsKey(step.getOriginalName())) {
                    stepExecutedTimes.put(step.getOriginalName(), 1);
                } else {
                    int numOfExecutions = stepExecutedTimes.get(step.getOriginalName());
                    stepExecutedTimes.replace(step.getOriginalName(), numOfExecutions + 1);
                }
                if (!stepExecutedTotalMillis.containsKey(step.getOriginalName())) {
                    stepExecutedTotalMillis.put(step.getOriginalName(), execution.getStepsTotalTimes().get(step.getName()).toMillis());
                } else {
                    long totalTime = stepExecutedTotalMillis.get(step.getOriginalName());
                    stepExecutedTotalMillis.replace(step.getOriginalName(), totalTime +
                            execution.getStepsTotalTimes().get(step.getName()).toMillis());
                }
            }
        }
    }

    public FlowExecutionDTO getExecutionDTOByUUID(UUID id) {
        return new FlowExecutionDTO(allFlowExecutionsMap.get(id),
                new FlowDefinitionDTO(allFlowExecutionsMap.get(id).getFlowDefinition()));
    }

    public FlowExecution getExecutionByUUID(UUID id) {
        for (FlowExecution flowExecution: allFlowExecutionsList) {
            if(flowExecution.getUuid().equals(id)) {
                return flowExecution;
            }
        }
        return null;
    }

    public List<StepUsageDeclarationDTO> getOnlyExecutedSteps(FlowExecutionDTO execution) {
        List<StepUsageDeclarationDTO> stepsInExecution = execution.getFlowDefinitionDTO().getSteps();
        List<StepUsageDeclarationDTO> executedSteps = new ArrayList<>();
        for (StepUsageDeclarationDTO step : stepsInExecution) {
            if (execution.getStepsTotalTimes().get(step.getName()) != null) {
                executedSteps.add(step);
            }
        }
        return  executedSteps;
    }

    public Map<String, Integer> getFlowExecutedTimes() {
        return flowExecutedTimes;
    }

    public Map<String, Long> getFlowExecutedTotalMillis() {
        return flowExecutedTotalMillis;
    }

    public Map<String, Integer> getStepExecutedTimes() {
        return stepExecutedTimes;
    }

    public Map<String, Long> getStepExecutedTotalMillis() {
        return stepExecutedTotalMillis;
    }
}
