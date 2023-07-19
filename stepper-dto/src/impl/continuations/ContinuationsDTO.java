package impl.continuations;

import api.DTO;

import java.util.List;

public class ContinuationsDTO implements DTO {
    private final List<ContinuationDTO> continuations;

    public ContinuationsDTO(List<ContinuationDTO> continuations) {
        this.continuations = continuations;
    }

    public List<ContinuationDTO> getContinuations() {
        return continuations;
    }
}
