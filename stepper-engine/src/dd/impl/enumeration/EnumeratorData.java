package dd.impl.enumeration;

import java.util.ArrayList;
import java.util.List;

public class EnumeratorData {
    private List<String> possibleValues = new ArrayList<>();
    private final String value;

    public EnumeratorData(String value) {
        this.value = value;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public String getValue() {
        return value;
    }

}
