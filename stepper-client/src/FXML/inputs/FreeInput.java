package FXML.inputs;

public class FreeInput {
    private final String id;
    private final String inputName;
    private final Object value;
    private final String type;

    public FreeInput(String id, String inputName, Object value, String type) {
        this.id = id;
        this.inputName = inputName;
        this.value = value;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getInputName() {
        return inputName;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
