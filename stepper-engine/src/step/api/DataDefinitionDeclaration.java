package step.api;

import dd.api.DataDefinition;

public interface DataDefinitionDeclaration
{
    String getName();

    DataNecessity necessity();
    String userString();
    DataDefinition dataDefinition();
}
