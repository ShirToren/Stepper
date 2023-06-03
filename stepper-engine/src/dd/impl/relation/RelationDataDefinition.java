package dd.impl.relation;

import dd.api.AbstractDataDefinition;

public class RelationDataDefinition extends AbstractDataDefinition {
    public RelationDataDefinition() {
        super("Relation", false, RelationData.class);
    }
}
