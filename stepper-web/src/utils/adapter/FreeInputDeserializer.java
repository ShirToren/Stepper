package utils.adapter;

import com.google.gson.*;
import utils.FreeInput;

import java.lang.reflect.Type;

public class FreeInputDeserializer implements JsonDeserializer<FreeInput> {

    @Override
    public FreeInput deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // Extract the properties from the JSON object
        String id = jsonObject.get("id").getAsString();
        String inputName = jsonObject.get("inputName").getAsString();
        String ObjType = jsonObject.get("type").getAsString();
        String value = jsonObject.get("value").getAsString();

        if(ObjType.equals(Integer.class.getName())) {
            return new FreeInput(id, inputName, Integer.parseInt(value), ObjType);
        } else if (ObjType.equals(Double.class.getName())) {
            return new FreeInput(id, inputName, Double.parseDouble(value), ObjType);
        } else {
            return new FreeInput(id, inputName, value, ObjType);
        }
    }
}
