package dto;

import logs.LogLine;

public class LogLineDTO implements DTO{
    private final String line;
    private final String time;

    public LogLineDTO(LogLine logLine) {
        this.line = logLine.getLine();
        this.time = logLine.getTime().toString();
    }

    public String getLine() {
        return line;
    }

    public String getTime() {
        return time;
    }
}
