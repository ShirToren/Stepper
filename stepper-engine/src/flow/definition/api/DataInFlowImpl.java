package flow.definition.api;

import dd.api.DataDefinition;
import dd.api.DataDirection;
import step.api.DataDefinitionDeclaration;
import step.api.DataDefinitionDeclarationImpl;
import step.api.StepDefinition;

import java.util.ArrayList;
import java.util.List;

public class DataInFlowImpl implements DataInFlow{
    private final String id;
    private String dataInstanceName;
    private final String originalDataInstanceNameInStep;
    private final DataDirection dataDirection;
    private final DataDefinition dataDefinition;
    private final StepDefinition ownerStepDefinition;
    private final List<DataInFlow> sourceDataInFlow;
    private final List<DataInFlow> targetDataInFlow;
    private final StepUsageDeclaration ownerStepUsageDeclaration;

    private final DataDefinitionDeclaration dataDefinitionDeclaration;

    public DataInFlowImpl(String id, String dataInstanceName, String originalDataInstanceNameInStep, DataDirection dataDirection, DataDefinition dataDefinition, StepUsageDeclaration ownerStepUsageDeclaration, DataDefinitionDeclaration dataDefinitionDeclaration) {
        this.id = id;
        this.dataInstanceName = dataInstanceName;
        this.originalDataInstanceNameInStep = originalDataInstanceNameInStep;
        this.dataDirection = dataDirection;
        this.dataDefinition = dataDefinition;
        this.sourceDataInFlow = new ArrayList<>();
        this.targetDataInFlow = new ArrayList<>();
        this.ownerStepUsageDeclaration = ownerStepUsageDeclaration;
        this.ownerStepDefinition = ownerStepUsageDeclaration.getStepDefinition();
        this.dataDefinitionDeclaration = dataDefinitionDeclaration;

    }

    @Override
    public void setDataInstanceName(String dataInstanceName) {
        this.dataInstanceName = dataInstanceName;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getDataInstanceName() {
        return dataInstanceName;
    }

    @Override
    public String getOriginalDataInstanceNameInStep() {
        return originalDataInstanceNameInStep;
    }

    @Override
    public DataDirection getDataDirection() {
        return dataDirection;
    }


    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    }

    @Override
    public StepDefinition getOwnerStepDefinition() {
        return ownerStepDefinition;
    }

    @Override
    public StepUsageDeclaration getOwnerStepUsageDeclaration() {
        return ownerStepUsageDeclaration;
    }

    @Override
    public List<DataInFlow> getSourceDataInFlow() {
        return sourceDataInFlow;
    }

    @Override
    public List<DataInFlow> getTargetDataInFlow() {
        return targetDataInFlow;
    }

    @Override
    public DataDefinitionDeclaration getDataDefinitionDeclaration() {
        return dataDefinitionDeclaration;
    }
}
