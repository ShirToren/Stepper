package dd;

import java.util.ArrayList;
import java.util.List;

public class StringList implements ListData<String> {
    private final List<String> theList;

    public StringList() {
        this.theList = new ArrayList<>();
    }

    @Override
    public List<String> getList() {
        return theList;
    }

    @Override
    public void addToList(String value) {
        theList.add(value);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("");
        int index = 1;
        for(String str : theList) {
            result.append(Integer.toString(index));
            result.append(". ");
            result.append(str);
            result.append("\n");
            index++;
        }
        return result.toString();
    }
}