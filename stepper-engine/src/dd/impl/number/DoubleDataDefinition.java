package dd.impl.number;

import dd.api.AbstractDataDefinition;

public class DoubleDataDefinition extends AbstractDataDefinition {
    public DoubleDataDefinition() {
        super("Double", true, Double.class);
    }
}
