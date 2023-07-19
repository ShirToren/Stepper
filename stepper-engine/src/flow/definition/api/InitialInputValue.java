package flow.definition.api;

public class InitialInputValue {
    private final String inputName;
    private final String initialValue;

    public InitialInputValue(String inputName, String initialValue) {
        this.inputName = inputName;
        this.initialValue = initialValue;
    }

    public String getInputName() {
        return inputName;
    }

    public String getInitialValue() {
        return initialValue;
    }
}
