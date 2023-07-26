package dd.impl.json;

import dd.api.AbstractDataDefinition;

public class JsonDataDefinition extends AbstractDataDefinition {
    public JsonDataDefinition() {
        super("Json", true, dd.JsonData.class);
    }
}
