package exception;

public class DoubleFlowNameException extends RuntimeException{
    private final String flowName;
    private final String EXCEPTION_MESSAGE = "There are two or more flow's with the same name: %s.";

    public DoubleFlowNameException(String flowName) {
        this.flowName = flowName;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, flowName);
    }
}
