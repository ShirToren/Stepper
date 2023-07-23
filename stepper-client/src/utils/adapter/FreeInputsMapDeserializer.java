package utils.adapter;

import com.google.gson.*;
import impl.DataInFlowDTO;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FreeInputsMapDeserializer implements JsonDeserializer<Map<String, Object>> {
    @Override
    public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return readMap(json.getAsJsonObject(), context);
    }

    private Map<String, Object> readMap(JsonObject jsonObject, JsonDeserializationContext context) {
        Map<String, Object> map = new HashMap<>();
        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();

        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            Object deserializedValue = deserializeValue(value, context);

            map.put(key, deserializedValue);
        }

        return map;
    }

    private Object deserializeValue(JsonElement jsonElement, JsonDeserializationContext context) {
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                if (primitive.getAsDouble() == primitive.getAsInt()) {
                    return primitive.getAsInt();
                } else {
                    return primitive.getAsDouble();
                }
            } else if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        } else if (jsonElement.isJsonObject()) {
            return readMap(jsonElement.getAsJsonObject(), context);
        } else if (jsonElement.isJsonArray()) {
            return context.deserialize(jsonElement, Object[].class);
        } else if (jsonElement.isJsonNull()) {
            return null;
        }

        // Throw an exception if an unsupported JSON type is encountered
        throw new JsonParseException("Unsupported JSON type: " + jsonElement.getClass().getSimpleName());
    }
}
