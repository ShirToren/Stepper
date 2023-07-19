package impl.continuations;

import api.DTO;

public class ContinuationMappingDTO implements DTO {
    private final String sourceData;
    private final String targetData;

    public ContinuationMappingDTO(String sourceData, String targetData) {
        this.sourceData = sourceData;
        this.targetData = targetData;
    }

    public String getSourceData() {
        return sourceData;
    }

    public String getTargetData() {
        return targetData;
    }
}
