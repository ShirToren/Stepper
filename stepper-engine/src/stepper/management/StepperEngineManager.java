package stepper.management;


import dd.api.DataDefinition;
import exception.DoubleFlowNameException;
import exception.StepNotExistException;
import flow.definition.api.DataInFlow;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.definition.api.continuations.Continuation;
import flow.definition.api.continuations.ContinuationMapping;
import flow.definition.api.continuations.Continuations;
import flow.execution.FlowExecution;
import flow.execution.runner.FLowExecutor;
import impl.continuations.ContinuationDTO;
import impl.continuations.ContinuationMappingDTO;
import impl.continuations.ContinuationsDTO;
import logs.LogLine;
import role.RoleDefinition;
import role.RoleDefinitionImpl;
import step.api.DataNecessity;
import stepper.definition.Stepper;
import stepper.definition.XMLLoader;
import impl.*;
import users.User;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StepperEngineManager {
    private Stepper stepper;
    private final XMLLoader loader;
    private final FLowExecutor fLowExecutor;
    private final Map<String, RoleDefinition> roles;
    private FlowExecution currentFlowExecution;
    private final List<FlowExecution> allFlowExecutionsList;
    private final Map<UUID, FlowExecution> allFlowExecutionsMap;
    private final Map<String, UUID> stringToIDMap;
    private final List<dto.FlowExecutionDTO> allExecutionsDTO;
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
        this.stringToIDMap = new ConcurrentHashMap<>();
        this.allFlowExecutionsList = Collections.synchronizedList(new ArrayList<>());
        this.executor = Executors.newFixedThreadPool(5);
        this.roles = new HashMap<>();
        initRoles();
    }

    public Map<String, RoleDefinition> getRoles() {
        return roles;
    }

    public Map<String, RoleDefinitionDTO> getRolesDTO() {
        Map<String, RoleDefinitionDTO> result = new HashMap<>();
        for (Map.Entry<String, RoleDefinition> entry: roles.entrySet()) {
            result.put(entry.getKey(),
                    createRoleDefinitionDTO(entry.getValue()));
        }
        return result;
    }

    public FlowDefinitionDTO showFlowDefinition(String flowName) {
        FlowDefinition flow = stepper.findFlowDefinitionByName(flowName);
        return createFlowDefinitionDTO(flow);
    }

    private void initRoles(){
        this.roles.put("Read Only Flows", new RoleDefinitionImpl("Read Only Flows", "This role describes all flows that are read-only",
                new ArrayList<>()));
        this.roles.put("All Flows", new RoleDefinitionImpl("All Flows", "This role describes all flows in the system",
                new ArrayList<>()));
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

    private StepUsageDeclarationDTO createStepUsageDeclarationDTO(StepUsageDeclaration stepUsageDeclaration) {
        return new StepUsageDeclarationDTO(stepUsageDeclaration.getStepDefinition().getName(),
                stepUsageDeclaration.getFinalStepName(),
                stepUsageDeclaration.getStepDefinition().isReadonly());
    }

    private DataInFlowDTO createDataInFlowDTO(DataInFlow dataInFlow){
        DataDefinitionDTO dataDefinitionDTO = createDataDefinitionDTO(dataInFlow.getDataDefinition());
        List<StepUsageDeclarationDTO> sourceSteps = new ArrayList<>();
        List<StepUsageDeclarationDTO> targetSteps = new ArrayList<>();
        for (DataInFlow sourceData : dataInFlow.getSourceDataInFlow()) {
            sourceSteps.add(createStepUsageDeclarationDTO(sourceData.getOwnerStepUsageDeclaration()));
        }
        for (DataInFlow targetData : dataInFlow.getTargetDataInFlow()) {
            targetSteps.add(createStepUsageDeclarationDTO(targetData.getOwnerStepUsageDeclaration()));
        }
        StepUsageDeclarationDTO ownerStep = createStepUsageDeclarationDTO(dataInFlow.getOwnerStepUsageDeclaration());
        return new DataInFlowDTO(dataInFlow.getDataInstanceName(),
                dataInFlow.getOriginalDataInstanceNameInStep(),
                dataDefinitionDTO, sourceSteps, targetSteps,
                dataInFlow.getDataDefinitionDeclaration().necessity().name(),
                dataInFlow.getDataDefinitionDeclaration().userString(),
                ownerStep);
    }
    private DataDefinitionDTO createDataDefinitionDTO(DataDefinition dataDefinition){
        return new DataDefinitionDTO(dataDefinition.getName(),
                dataDefinition.isUserFriendly(),
                dataDefinition.getType().getName());
    }

    private LogLineDTO createLogLineDTO(LogLine logLine){
        return new LogLineDTO(logLine.getLine(),
                logLine.getTime().toString());
    }

    private FlowDefinitionDTO createFlowDefinitionDTO(FlowDefinition flowDefinition){
        List<DataInFlowDTO> flowsFormalOutputs = new ArrayList<>();
        List<StepUsageDeclarationDTO> steps = new ArrayList<>();
        List<DataInFlowDTO> freeInputs = new ArrayList<>();
        List<DataInFlowDTO> flowsOutputs = new ArrayList<>();
        List<DataInFlowDTO> flowsInputs = new ArrayList<>();
        ContinuationsDTO continuations = createContinuationsDTO(flowDefinition.getContinuations());

        for (StepUsageDeclaration step : flowDefinition.getFlowSteps()) {
            steps.add(createStepUsageDeclarationDTO(step));
        }
        for (DataInFlow freeInput : flowDefinition.getFlowFreeInputs()) {
            freeInputs.add(createDataInFlowDTO(freeInput));
        }
        for (DataInFlow output : flowDefinition.getFlowOutputs()) {
            flowsOutputs.add(createDataInFlowDTO(output));
        }
        for (DataInFlow input : flowDefinition.getFlowInputs()) {
            flowsInputs.add(createDataInFlowDTO(input));
        }
        for (DataInFlow formalOutput : flowDefinition.getFormalOutputsDataInFlow()) {
            flowsFormalOutputs.add(createDataInFlowDTO(formalOutput));
        }
        return new FlowDefinitionDTO(flowDefinition.getName(),
                flowDefinition.getDescription(),
                flowsFormalOutputs, flowDefinition.isReadOnly(),
                steps, freeInputs, flowsOutputs, flowsInputs,
                flowDefinition.getFreeInputsStepTarget(),
                continuations);
    }

    private FlowExecutionDTO createFlowExecutionDTO(FlowExecution flowExecution){
        FlowDefinitionDTO flowDefinitionDTO = createFlowDefinitionDTO(flowExecution.getFlowDefinition());
        Map<DataInFlowDTO, Object> executionFormalOutputs = new HashMap<>();
        Map<DataInFlowDTO, Object> allExecutionOutputs = new HashMap<>();
        Map<DataInFlowDTO, Object> allExecutionInputs = new HashMap<>();
        Map<String, Long> stepsTotalTimes = new HashMap<>();
        List<StepUsageDeclarationDTO> executedSteps = new ArrayList<>();
        Map<String, String> stepsResults = new HashMap<>();
        Map<String, List<LogLineDTO>> logLines = new HashMap<>();
        Map<String, String> summeryLines = new HashMap<>();
        Map<String, String> stepsStartTimes = new HashMap<>();
        Map<String, String> stepsEndTimes = new HashMap<>();
        List<LogLineDTO> logLineDTOS = new ArrayList<>();
        String executionResult = flowExecution.getFlowExecutionResult() != null? flowExecution.getFlowExecutionResult().name() : null;
        long totalTime = flowExecution.getTotalTime() != null? flowExecution.getTotalTime().toMillis() : 0;
        String startExecutionTime = flowExecution.getStartExecutionTime() != null? flowExecution.getStartExecutionTime().toString() : null;
        String endExecutionTime = flowExecution.getEndExecutionTime() != null? flowExecution.getEndExecutionTime().toString() : null;



        for (DataInFlowDTO dataInFlowDTO : flowDefinitionDTO.getFlowsOutputs()) {
            if (flowExecution.getExecutionFormalOutputs().containsKey(dataInFlowDTO.getFinalName())) {
                executionFormalOutputs.put(dataInFlowDTO, flowExecution.getExecutionFormalOutputs().get(dataInFlowDTO.getFinalName()));
            }
            if (flowExecution.getAllExecutionOutputs().containsKey(dataInFlowDTO.getFinalName())) {
                allExecutionOutputs.put(dataInFlowDTO, flowExecution.getAllExecutionOutputs().get(dataInFlowDTO.getFinalName()));
            }
        }

        for (DataInFlowDTO input: flowDefinitionDTO.getFlowsInputs()) {
            if (flowExecution.getAllExecutionInputs().containsKey(input.getFinalName())) {
                allExecutionInputs.put(input, flowExecution.getAllExecutionInputs().get(input.getFinalName()));
            }
        }

        for (StepUsageDeclaration step : flowExecution.getExecutedSteps()) {
            if(flowExecution.getStepsTotalTimes().get(step.getFinalStepName()) != null &&
                    flowExecution.getStepsResults().get(step.getFinalStepName()) != null) {
                stepsTotalTimes.put(step.getFinalStepName(), flowExecution.getStepsTotalTimes().get(step.getFinalStepName()).toMillis());
                stepsResults.put(step.getFinalStepName(), flowExecution.getStepsResults().get(step.getFinalStepName()).name());
            }
            executedSteps.add(createStepUsageDeclarationDTO(step));
        }

        for (Map.Entry<StepUsageDeclaration, List<LogLine>> entry : flowExecution.getLogLines().entrySet()) {
            for (LogLine logLine: entry.getValue()) {
                logLineDTOS.add(createLogLineDTO(logLine));
            }
            logLines.put(entry.getKey().getFinalStepName(), logLineDTOS);
            logLineDTOS.clear();
        }
        for (Map.Entry<StepUsageDeclaration,String> entry : flowExecution.getSummeryLines().entrySet()) {
            summeryLines.put(entry.getKey().getFinalStepName(), entry.getValue());
        }

        for (Map.Entry<String,LocalTime> entry : flowExecution.getStepsStartTimes().entrySet()) {
            stepsStartTimes.put(entry.getKey(), entry.getValue().toString());
        }
        for (Map.Entry<String,LocalTime> entry : flowExecution.getStepsEndTimes().entrySet()) {
            stepsEndTimes.put(entry.getKey(), entry.getValue().toString());
        }
        return new FlowExecutionDTO(flowExecution.getUuid(),
                flowDefinitionDTO, executionResult,
                executionFormalOutputs, allExecutionOutputs, allExecutionInputs,
                totalTime, startExecutionTime, endExecutionTime,
                stepsTotalTimes, executedSteps, stepsResults, logLines, summeryLines,
                flowExecution.isFinished(), flowExecution.getFreeInputs(),
                stepsStartTimes, stepsEndTimes, flowExecution.getUserName());
    }
    private ContinuationsDTO createContinuationsDTO(Continuations continuations){
        List<ContinuationDTO> continuationDTOS = new ArrayList<>();
        if(continuations != null){
            for (Continuation continuation: continuations.getContinuations()) {
                continuationDTOS.add(createContinuationDTO(continuation));
            }
        }
        return new ContinuationsDTO(continuationDTOS);
    }
    private ContinuationMappingDTO createContinuationMappingDTO(ContinuationMapping mapping){
        return new ContinuationMappingDTO(mapping.getSourceData(), mapping.getTargetData());
    }
    private ContinuationDTO createContinuationDTO(Continuation continuation){
        List<ContinuationMappingDTO> mappingDTOS = new ArrayList<>();
        for (ContinuationMapping mapping: continuation.getContinuationMappings()) {
            mappingDTOS.add(createContinuationMappingDTO(mapping));
        }
        return new ContinuationDTO(continuation.getTargetFlow(),
                mappingDTOS);
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

    public void copyFreeInputsValues(String sourceID, String targetID) {
        UUID sourceUUID = stringToIDMap.get(sourceID);
        UUID targetUUID = stringToIDMap.get(targetID);
        FlowExecution sourceExecution = allFlowExecutionsMap.get(sourceUUID);
        FlowExecution targetExecution = allFlowExecutionsMap.get(targetUUID);
        for (Map.Entry<String, Object> input: sourceExecution.getFreeInputs().entrySet()) {
            targetExecution.addFreeInput(input.getKey(), input.getValue());
        }
    }

    public void copyContinuationValues(String sourceID, String targetID) {
        UUID sourceUUID = this.stringToIDMap.get(sourceID);
        UUID targetUUID = this.stringToIDMap.get(targetID);
        FlowExecution sourceExecution = allFlowExecutionsMap.get(sourceUUID);
        FlowExecution targetExecution = allFlowExecutionsMap.get(targetUUID);
        Continuation continuation =
                sourceExecution.getFlowDefinition().getContinuationByTargetFlowName(targetExecution.getFlowDefinition().getName());
        List<DataInFlow> flowFreeInputs = targetExecution.getFlowDefinition().getFlowFreeInputs();
        for (DataInFlow input: flowFreeInputs) {
            if(sourceExecution.getAllExecutionOutputs().containsKey(input.getDataInstanceName()) &&
                    !sourceExecution.getAllExecutionOutputs().get(input.getDataInstanceName()).equals("Not created due to failure in flow") &&
            input.getDataDefinition().getType().equals(sourceExecution.getAllExecutionOutputs().get(input.getDataInstanceName()).getClass())){
                targetExecution.addFreeInput(input.getDataInstanceName(),
                        sourceExecution.getAllExecutionOutputs().get(input.getDataInstanceName()));
            } else if(sourceExecution.getAllExecutionInputs().containsKey(input.getDataInstanceName()) &&
            input.getDataDefinition().getType().equals(sourceExecution.getAllExecutionInputs().get(input.getDataInstanceName()).getClass())) {
                targetExecution.addFreeInput(input.getDataInstanceName(),
                        sourceExecution.getAllExecutionInputs().get(input.getDataInstanceName()));
            }
        }
        for (ContinuationMapping mapping: continuation.getContinuationMappings()) {
            if(!sourceExecution.getAllExecutionOutputs().get(mapping.getSourceData()).equals("Not created due to failure in flow")){
                targetExecution.addFreeInput(mapping.getTargetData(),
                        sourceExecution.getAllExecutionOutputs().get(mapping.getSourceData()));
            }
        }
    }

    private RoleDefinitionDTO createRoleDefinitionDTO(RoleDefinition roleDefinition) {
        return new RoleDefinitionDTO(roleDefinition.getName(),
                roleDefinition.getDescription(),
                roleDefinition.getFlows());
    }

    public UserDTO createUserDTO(User user){
        List<RoleDefinitionDTO> roleDefinitionDTOList = new ArrayList<>();
        for (RoleDefinition role: user.getRolesDefinitions()) {
            roleDefinitionDTOList.add(createRoleDefinitionDTO(role));
        }
        return new UserDTO(user.getName(), roleDefinitionDTOList, user.getNumOfExecutions(), user.isManager());
    }

    public List<dto.FlowExecutionDTO> getAllExecutionsDTO(){
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
                allFlowDefinitionsInStepper.add(createFlowDefinitionDTO(flow));
            }
        }
        return allFlowDefinitionsInStepper;
    }

    public List<FlowDefinitionDTO> getFlowDefinitionsByRole(List<String> rolesList, int fromIndex){
        List<FlowDefinitionDTO> flowDefinitionDTOS = new ArrayList<>();
        if(stepper != null) {
            for (FlowDefinition flow: stepper.getFlows()) {
                for (String roleName: rolesList) {
                    if(roles.get(roleName).getFlows().contains(flow.getName())){
                        flowDefinitionDTOS.add(createFlowDefinitionDTO(flow));
                        break;
                    }
                }
            }
        }
        if (fromIndex < 0 || fromIndex > flowDefinitionDTOS.size()) {
            fromIndex = 0;
        }
        return flowDefinitionDTOS.subList(fromIndex, flowDefinitionDTOS.size());
       // return flowDefinitionDTOS;
    }

    public int getFlowDefinitionsVersion(List<String> rolesList) {
        int count = 0;
        if(stepper != null) {
            for (FlowDefinition flow: stepper.getFlows()) {
                for (String roleName: rolesList) {
                    if(roles.get(roleName).getFlows().contains(flow.getName())){
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }

    private void addExecutionDTO(dto.FlowExecutionDTO dto){
        allExecutionsDTO.add(0, dto);
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
        if(allFlowExecutionsMap.get(id) != null) {
            return new HashMap<>(allFlowExecutionsMap.get(id).getFreeInputs());
        }
        return new HashMap<>();
    }

    public List<String> getAllFlowsNames() {
        List<String> names = new ArrayList<>();
        for (FlowDefinition flow : stepper.getFlows()) {
            names.add(flow.getName());
        }
        return  names;
    }

    public String getFlowNameByExecutionID(String id) {
        UUID uuid = stringToIDMap.get(id);
        FlowExecution flowExecution = allFlowExecutionsMap.get(uuid);
        return flowExecution.getFlowDefinition().getName();
    }

    public UUID createFlowExecution(String flowName, String userName) {
        UUID id = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(id,
                stepper.findFlowDefinitionByName(flowName), userName);
            this.allFlowExecutionsList.add(0, flowExecution);
            this.allFlowExecutionsMap.put(id, flowExecution);
            this.stringToIDMap.put(id.toString(), id);
        return id;
    }

    public UUID createFlowExecution(String flowName) {
        UUID id = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(id,
                stepper.findFlowDefinitionByName(flowName), "");
        this.allFlowExecutionsList.add(0, flowExecution);
        this.allFlowExecutionsMap.put(id, flowExecution);
        this.stringToIDMap.put(id.toString(), id);
        //currentFlowExecution = flowExecution;
        return id;
    }

    public void setCurrentFlowExecution(dto.FlowExecutionDTO currentFlowExecution){
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
            result.add(createFlowExecutionDTO(execution));
        }
        return result;
    }

    public List<FlowExecutionDTO> getFlowExecutionsDTOByUserName(String userName) {
        List<FlowExecutionDTO> result = new ArrayList<>();
        for (FlowExecution execution: allFlowExecutionsList) {
            if(execution.getUserName().equals(userName)) {
                result.add(createFlowExecutionDTO(execution));
            }
        }
        return result;
    }

    public void addFreeInputToFlowExecution(String  id, String inputName, Object value) {
        UUID uuid = stringToIDMap.get(id);
        allFlowExecutionsMap.get(uuid).addFreeInput(inputName, value);
    }
    public void addFreeInputToFlowExecution(String inputName, Object value) {
        currentFlowExecution.addFreeInput(inputName, value);
    }

    public List<FlowExecution> getAllFlowExecutionsList() {
        return allFlowExecutionsList;
    }

    public dto.FlowExecutionDTO executeFlow() {
        Instant start = Instant.now();
        fLowExecutor.executeFlow(allFlowExecutionsList.get(0));
        currentFlowExecution.setFinished(true);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        currentFlowExecution.setTotalTime(duration);
        dto.FlowExecutionDTO dto = new dto.FlowExecutionDTO(currentFlowExecution, new dto.FlowDefinitionDTO(currentFlowExecution.getFlowDefinition()));
        addExecutionDTO(dto);
        //addExecutionToStatistics(dto);
        return dto;
    }

    public void executeFlow(String id) {
        Instant start = Instant.now();
        UUID uuid = stringToIDMap.get(id);
        FlowExecution flowExecution = allFlowExecutionsMap.get(uuid);
        fLowExecutor.executeFlow(flowExecution);
        flowExecution.setFinished(true);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        flowExecution.setTotalTime(duration);
        addExecutionToStatistics(getExecutionDTOByUUID(id));
    }

    void addFlowsToStepper(Stepper stepper) {
        for (FlowDefinition flow: stepper.getFlows()) {
            if(!isFlowExist(flow)) {
                this.stepper.addFlowToStepper(flow);
            }
        }
    }

    boolean isFlowExist(FlowDefinition flowDefinition) {
        for (FlowDefinition flow: this.stepper.getFlows()) {
            if(flow.getName().equals(flowDefinition.getName())) {
                return true;
            }
        }
        return false;
    }

    public StatisticsDTO createStatisticsDTO() {
        return new StatisticsDTO(flowExecutedTimes, flowExecutedTotalMillis, stepExecutedTimes, stepExecutedTotalMillis);
    }

    public XMLDTO readSystemInformationFile(InputStream inputStream) {
        XMLDTO dto;
        Stepper stepper;
        try {
            stepper = loader.loadStepperFromXMLFile(inputStream);
            if(stepper.validateStepperStructure()){
                dto = new XMLDTO("The file is valid and fully loaded.");
                if(this.stepper == null) {
                    this.stepper = stepper;
                    if(stepper.getThreadPool() != 0){
                        this.executor = Executors.newFixedThreadPool(stepper.getThreadPool());
                    }
                } else {
                    addFlowsToStepper(stepper);
                }
                updateRoles(stepper);
            } else {
                dto = new XMLDTO("The file is not valid. One or more flows are not valid.");
            }
        } catch (JAXBException e) {
            dto = new XMLDTO(String.format("The file is not valid. Error: %s", e.getMessage()));
        } catch (StepNotExistException e) {
            dto = new XMLDTO(e.getMessage());
        } catch (DoubleFlowNameException e) {
            dto = new XMLDTO(e.getMessage());
        }
        return dto;
    }

    private void updateRoles(Stepper stepper){
        for (FlowDefinition flow: stepper.getFlows()) {
            roles.get("All Flows").getFlows().add(flow.getName());
            if(flow.isReadOnly()) {
                roles.get("Read Only Flows").getFlows().add(flow.getName());
            }
        }
    }

    public dto.XMLDTO readSystemInformationFile(String fileName) {
        dto.XMLDTO dto;
        Stepper stepper;
        if (fileName.endsWith(".xml")) {
            try {
                stepper = loader.loadStepperFromXMLFile(fileName);
                if (stepper.validateStepperStructure()) {
                    dto = new dto.XMLDTO("The file is valid and fully loaded.");
                    this.stepper = stepper;
                    if(stepper.getThreadPool() != 0){
                        this.executor = Executors.newFixedThreadPool(stepper.getThreadPool());
                    }
                    cleanAllSystem();
                } else {
                    dto = new dto.XMLDTO("The file is not valid. One or more flows are not valid.");
                }
            } catch (FileNotFoundException | JAXBException e) {
                dto = new dto.XMLDTO(String.format("The file is not valid. Error: %s", e.getMessage()));
            } catch (StepNotExistException e) {
                dto = new dto.XMLDTO(e.getMessage());
            } catch (DoubleFlowNameException e) {
                dto = new dto.XMLDTO(e.getMessage());
            }

        } else {
            dto = new dto.XMLDTO("The file is not an XML file.");
        }
        return dto;
    }

    public List<dto.DataInFlowDTO> getCurrentFreeInputs(){
        List<dto.DataInFlowDTO> freeInputs = new ArrayList<>();
        for (DataInFlow input : currentFlowExecution.getFlowDefinition().getFlowFreeInputs()) {
            freeInputs.add(new dto.DataInFlowDTO(input));
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
            flowExecutedTotalMillis.put(execution.getFlowDefinitionDTO().getName(), execution.getTotalTime());
        }
        else {
            long totalTime = flowExecutedTotalMillis.get(execution.getFlowDefinitionDTO().getName());
            flowExecutedTotalMillis.replace(execution.getFlowDefinitionDTO().getName(), totalTime + execution.getTotalTime());
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
                    stepExecutedTotalMillis.put(step.getOriginalName(), execution.getStepsTotalTimes().get(step.getName()));
                } else {
                    long totalTime = stepExecutedTotalMillis.get(step.getOriginalName());
                    stepExecutedTotalMillis.replace(step.getOriginalName(), totalTime +
                            execution.getStepsTotalTimes().get(step.getName()));
                }
            }
        }
    }

    public FlowExecutionDTO getExecutionDTOByUUID(String id) {
        UUID uuid = stringToIDMap.get(id);
        return createFlowExecutionDTO(allFlowExecutionsMap.get(uuid));
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
