package dd.impl.file;

import dd.api.AbstractDataDefinition;

import java.io.File;

public class FileDataDefinition extends AbstractDataDefinition {
    public FileDataDefinition() {
        super("File", false, FileData.class);
    }

}
