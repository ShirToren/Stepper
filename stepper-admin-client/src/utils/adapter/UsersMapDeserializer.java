package utils.adapter;

import com.google.gson.*;
import impl.UserDTO;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UsersMapDeserializer implements JsonDeserializer<Map<String, UserDTO>> {

    @Override
    public Map<String, UserDTO> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, UserDTO> map = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            UserDTO userDTO = context.deserialize(value, UserDTO.class);
            map.put(key, userDTO);
        }

        return map;
    }
}

