package utils.adapter;

import com.google.gson.*;
import impl.RoleDefinitionDTO;
import impl.UserDTO;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RolesMapDeserializer implements JsonDeserializer<Map<String, RoleDefinitionDTO>> {
    @Override
    public Map<String, RoleDefinitionDTO> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, RoleDefinitionDTO> map = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            RoleDefinitionDTO roleDefinitionDTO = context.deserialize(value, RoleDefinitionDTO.class);
            map.put(key, roleDefinitionDTO);
        }
        return map;
    }
}
