package flow.definition.api.continuations;

import java.util.List;

public class Continuations {
    private  List<Continuation> continuations;

    public Continuations(List<Continuation> continuations) {
        this.continuations = continuations;
    }

    public List<Continuation> getContinuations() {
        return continuations;
    }

/*    public void setContinuations(List<Continuation> continuations) {
        this.continuations = continuations;
    }*/
}
