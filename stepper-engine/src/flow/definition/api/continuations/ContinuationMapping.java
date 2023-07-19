package flow.definition.api.continuations;

public class ContinuationMapping {
    private  String sourceData;
    private  String targetData;

    public ContinuationMapping(String sourceData, String targetData) {
        this.sourceData = sourceData;
        this.targetData = targetData;
    }
    public String getSourceData() {
        return sourceData;
    }

    public String getTargetData() {
        return targetData;
    }

/*    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public void setTargetData(String targetData) {
        this.targetData = targetData;
    }*/
}
