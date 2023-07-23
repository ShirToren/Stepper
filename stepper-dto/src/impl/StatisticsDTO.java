package impl;

import api.DTO;

import java.util.Map;

public class StatisticsDTO implements DTO {
    private final Map<String, Integer> flowExecutedTimes;
    private final Map<String, Long> flowExecutedTotalMillis;
    private final Map<String, Integer> stepExecutedTimes;
    private final Map<String, Long> stepExecutedTotalMillis;

    public StatisticsDTO(Map<String, Integer> flowExecutedTimes, Map<String, Long> flowExecutedTotalMillis, Map<String, Integer> stepExecutedTimes, Map<String, Long> stepExecutedTotalMillis) {
        this.flowExecutedTimes = flowExecutedTimes;
        this.flowExecutedTotalMillis = flowExecutedTotalMillis;
        this.stepExecutedTimes = stepExecutedTimes;
        this.stepExecutedTotalMillis = stepExecutedTotalMillis;
    }

    public Map<String, Integer> getFlowExecutedTimes() {
        return flowExecutedTimes;
    }

    public Map<String, Long> getFlowExecutedTotalMillis() {
        return flowExecutedTotalMillis;
    }

    public Map<String, Integer> getStepExecutedTimes() {
        return stepExecutedTimes;
    }

    public Map<String, Long> getStepExecutedTotalMillis() {
        return stepExecutedTotalMillis;
    }
}
