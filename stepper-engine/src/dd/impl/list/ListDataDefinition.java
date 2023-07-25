package dd.impl.list;

import dd.api.AbstractDataDefinition;

public class ListDataDefinition extends AbstractDataDefinition {

    public ListDataDefinition() {
        super("List", false, dd.ListData.class);
    }
}
