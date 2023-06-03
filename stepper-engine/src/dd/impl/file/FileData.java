package dd.impl.file;

import java.io.File;

public class FileData {
    private final File file;

    public FileData(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }

}
