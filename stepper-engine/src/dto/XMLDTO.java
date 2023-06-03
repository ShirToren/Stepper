package dto;

public class XMLDTO implements DTO {
    private final String fileState;

    public XMLDTO(String fileState) {
        this.fileState = fileState;
    }

    public String getFileState() {
        return fileState;
    }
}
