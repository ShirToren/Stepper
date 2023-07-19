package impl;

import api.DTO;

public class LogLineDTO implements DTO {
    private final String line;
    private final String time;

    public LogLineDTO(String line, String time) {
        this.line = line;
        this.time = time;
    }
    public String getLine() {
        return line;
    }
    public String getTime() {
        return time;
    }
}
