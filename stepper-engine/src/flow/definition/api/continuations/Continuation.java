package flow.definition.api.continuations;

import java.util.List;

public class Continuation {
    private  String targetFlow;
    private  List<ContinuationMapping> continuationMappings;

    public Continuation(String targetFlow, List<ContinuationMapping> continuationMappings) {
        this.targetFlow = targetFlow;
        this.continuationMappings = continuationMappings;
    }

    public String getTargetFlow() {
        return targetFlow;
    }

    public List<ContinuationMapping> getContinuationMappings() {
        return continuationMappings;
    }

/*    public void setTargetFlow(String targetFlow) {
        this.targetFlow = targetFlow;
    }

    public void setContinuationMappings(List<ContinuationMapping> continuationMappings) {
        this.continuationMappings = continuationMappings;
    }*/
}
