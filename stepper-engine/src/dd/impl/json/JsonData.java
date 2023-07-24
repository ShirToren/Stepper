package dd.impl.json;

import com.google.gson.JsonElement;

public class JsonData {
    private JsonElement jsonElement;

    public JsonData(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    public JsonElement getJsonElement() {
        return jsonElement;
    }

}
