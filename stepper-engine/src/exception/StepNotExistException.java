package exception;

public class StepNotExistException extends RuntimeException{
    private final String stepName;
    private final String EXCEPTION_MESSAGE = "Step: %s doesn't exist.";

    public StepNotExistException(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, stepName);
    }
}
