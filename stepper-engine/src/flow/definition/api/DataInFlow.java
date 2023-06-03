package flow.definition.api;

import dd.api.DataDefinition;
import dd.api.DataDirection;
import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;

import java.util.List;

public interface DataInFlow {
    String getID();

    String getDataInstanceName();

    String getOriginalDataInstanceNameInStep();

    DataDirection getDataDirection();

    DataDefinition getDataDefinition();

    StepDefinition getOwnerStepDefinition();

    StepUsageDeclaration getOwnerStepUsageDeclaration();

    List<DataInFlow> getSourceDataInFlow();
    List<DataInFlow> getTargetDataInFlow();

    DataDefinitionDeclaration getDataDefinitionDeclaration();

    void setDataInstanceName(String dataInstanceName);
}
