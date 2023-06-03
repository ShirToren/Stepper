package dd.impl.list;

import java.util.List;

public interface ListData<T> {
    List<T> getList();
    void addToList(T value);

    @Override
    public String toString();
}
