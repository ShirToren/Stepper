package dd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileList implements ListData<File> {
    private final List<File> theList;
    public FileList() {
        this.theList = new ArrayList<>();
    }
    public FileList(List<File> filesList) {
        this.theList = filesList;
    }
    @Override
    public List<File> getList() {
        return theList;
    }
    @Override
    public void addToList(File value) {
        theList.add(value);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        int index = 1;
        for(File file: theList) {
            str.append(Integer.toString(index));
            str.append(". ");
            str.append(file.getAbsolutePath());
            str.append("\n");
            index++;
        }
        return str.toString();
    }
}
