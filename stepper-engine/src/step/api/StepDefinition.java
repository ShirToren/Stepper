package step.api;

import flow.execution.context.StepExecutionContext;

import java.util.List;

public interface StepDefinition
{
    String getName();

    void setName(String name);
    boolean isReadonly();
    List<DataDefinitionDeclaration> inputs();
    List<DataDefinitionDeclaration> outputs();
    StepResult invoke(StepExecutionContext context);
}
