package dd.impl;

import dd.api.DataDefinition;
import dd.impl.file.FileDataDefinition;
import dd.impl.list.ListDataDefinition;
import dd.impl.mapping.MappingDataDefinition;
import dd.impl.number.DoubleDataDefinition;
import dd.impl.number.NumberDataDefinition;
import dd.impl.relation.RelationDataDefinition;
import dd.impl.string.StringDataDefinition;

public enum DataDefinitionRegistry implements DataDefinition {
    STRING(new StringDataDefinition()),
    NUMBER(new NumberDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    RELATION(new RelationDataDefinition()),
    FILE(new FileDataDefinition()),
    LIST(new ListDataDefinition()),
    MAPPING(new MappingDataDefinition())
    ;

    DataDefinitionRegistry(DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    private final DataDefinition dataDefinition;

    @Override
    public String getName() {
        return dataDefinition.getName();
    }

    @Override
    public boolean isUserFriendly() {
        return dataDefinition.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return dataDefinition.getType();
    }
}