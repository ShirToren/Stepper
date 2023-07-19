package dd.impl.enumeration;

import dd.api.AbstractDataDefinition;

public class EnumerationDataDefinition extends AbstractDataDefinition {
    public EnumerationDataDefinition()
    {
        super("Enumeration", true, String.class);
    }
}
