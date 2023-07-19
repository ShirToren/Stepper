package impl.continuations;

import api.DTO;

import java.util.List;

public class ContinuationDTO implements DTO {
    private final String targetFlow;
    private final List<ContinuationMappingDTO> continuationMappings;

    public ContinuationDTO(String targetFlow, List<ContinuationMappingDTO> continuationMappings) {
        this.targetFlow = targetFlow;
        this.continuationMappings = continuationMappings;
    }

    public String getTargetFlow() {
        return targetFlow;
    }

    public List<ContinuationMappingDTO> getContinuationMappings() {
        return continuationMappings;
    }
}
