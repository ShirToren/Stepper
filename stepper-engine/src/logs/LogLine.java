package logs;

import java.time.LocalTime;

public class LogLine {
    private final String line;
    private final LocalTime time;

    public LogLine(String line, LocalTime time) {
        this.line = line;
        this.time = time;
    }

    public String getLine() {
        return line;
    }

    public LocalTime getTime() {
        return time;
    }
}
