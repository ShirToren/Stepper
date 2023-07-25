package utils.adapter;

import com.google.gson.*;
import impl.DataInFlowDTO;
import utils.Constants;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataInFlowMapDeserializer implements JsonDeserializer<Map<DataInFlowDTO, Object>> {

    @Override
    public Map<DataInFlowDTO, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<DataInFlowDTO, Object> map = new HashMap<>();

        if(json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            for (int i = 0; i < jsonArray.size() ; i++) {
                JsonArray asJsonArray = jsonArray.get(i).getAsJsonArray();
                DataInFlowDTO dataInFlowDTO = context.deserialize(asJsonArray.get(0), DataInFlowDTO.class);
                JsonElement valueJson = asJsonArray.get(1);
                Object value = new Object();
                Class<?> type = null;
                try {
                    type = Class.forName(dataInFlowDTO.getDataDefinition().getType());
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                if (valueJson.isJsonObject()) {
                    //value = Constants.GSON_INSTANCE.fromJson(valueJson, type);
                    if(!type.isInterface()) {
                        // value = context.deserialize(valueJson, type);
                        value = Constants.GSON_INSTANCE.fromJson(valueJson, type);
                    } else {
                        JsonElement jsonElement = valueJson.getAsJsonObject().get("theList");
                        value = deserializeArray(jsonElement.getAsJsonArray(), type, context);
                        //value = context.deserialize(jsonElement, type);
                    }
                } else if (valueJson.isJsonArray()) {
                    value = deserializeArray(valueJson.getAsJsonArray(), type, context);
                } else {
                    JsonPrimitive primitive = valueJson.getAsJsonPrimitive();
                    if (primitive.isNumber()) {
                        if (primitive.getAsDouble() == primitive.getAsInt()) {
                            value = primitive.getAsInt();
                        } else {
                            value = primitive.getAsDouble();
                        }
                    } else if (primitive.isString()) {
                        value = primitive.getAsString();
                    } else if (primitive.isBoolean()) {
                        value = primitive.getAsBoolean();
                    }
                }
                map.put(dataInFlowDTO, value);
            }
        }
        return map;
    }

    private List<Object> deserializeArray(JsonArray jsonArray, Type typeOfT, JsonDeserializationContext context) {
        List<Object> list = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                list.add(Constants.GSON_INSTANCE.fromJson(element, File.class));
                //list.add(deserialize(element, typeOfT, context));
            } else if (element.isJsonArray()) {
                list.add(deserializeArray(element.getAsJsonArray(), typeOfT, context));
            } else {
                list.add(context.deserialize(element, Object.class));
            }
        }
        return list;
    }
}
