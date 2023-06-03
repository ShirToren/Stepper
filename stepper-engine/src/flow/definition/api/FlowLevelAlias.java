package flow.definition.api;

public class FlowLevelAlias {
        private final String stepName;
        private final String sourceDataName;
        private final String alias;

    public FlowLevelAlias(String stepName, String sourceDataName, String alias) {
        this.stepName = stepName;
        this.sourceDataName = sourceDataName;
        this.alias = alias;
    }

    public String getStepName() {
        return stepName;
    }

    public String getSourceDataName() {
        return sourceDataName;
    }

    public String getAlias() {
        return alias;
    }
}
