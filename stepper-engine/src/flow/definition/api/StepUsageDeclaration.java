package flow.definition.api;

import step.api.StepDefinition;

public interface StepUsageDeclaration {
    String getFinalStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();
}
