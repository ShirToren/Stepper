package flow.definition.api;

public class CustomMapping {
    private final String sourceStep;
    private final String sourceData;
    private final String targetStep;
    private final String targetData;

    public CustomMapping(String sourceStep, String sourceData, String targetStep, String targetData) {
        this.sourceStep = sourceStep;
        this.sourceData = sourceData;
        this.targetStep = targetStep;
        this.targetData = targetData;
    }

    public String getSourceStep() {
        return sourceStep;
    }

    public String getSourceData() {
        return sourceData;
    }

    public String getTargetStep() {
        return targetStep;
    }

    public String getTargetData() {
        return targetData;
    }
}
