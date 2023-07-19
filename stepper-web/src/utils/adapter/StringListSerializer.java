package utils.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dd.impl.list.ListData;

import java.lang.reflect.Type;
import java.util.List;

import static constants.Constants.GSON_INSTANCE;

public class StringListSerializer implements JsonSerializer<ListData<String>> {

    @Override
    public JsonElement serialize(ListData<String> listData, Type type, JsonSerializationContext jsonSerializationContext) {
        List<String> theList = listData.getList();
        return JsonParser.parseString(GSON_INSTANCE.toJson(theList));
    }
}
