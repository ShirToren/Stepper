package impl;

import api.DTO;

public class XMLDTO implements DTO {
    private final String fileState;
    private final boolean isValid;


    public XMLDTO(String fileState, boolean isValid) {
        this.fileState = fileState;
        this.isValid = isValid;
    }

    public String getFileState() {
        return fileState;
    }

    public boolean isValid() {
        return isValid;
    }
}
