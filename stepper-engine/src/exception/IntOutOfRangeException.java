package exception;

public class IntOutOfRangeException extends RuntimeException{
    private final int start;
    private final int end;
    private final String EXCEPTION_MESSAGE = "Input out of range. Please enter a number between %d - %d";

    public IntOutOfRangeException(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, start, end);
    }
}
