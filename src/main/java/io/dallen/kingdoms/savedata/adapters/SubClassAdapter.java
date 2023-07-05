package io.dallen.kingdoms.savedata.adapters;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import io.dallen.kingdoms.kingdom.plot.PlotController;
import io.dallen.kingdoms.savedata.SubClass;
import lombok.SneakyThrows;

import java.lang.reflect.Type;

public class SubClassAdapter<T> implements JsonSerializer<SubClass<T>>, JsonDeserializer<SubClass<T>> {

    public static void register(GsonBuilder builder) {
        builder.registerTypeAdapter(new TypeToken<SubClass<PlotController>>(){}.getType(),
                new SubClassAdapter<>());
    }

    @SneakyThrows
    @Override
    public SubClass<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var typ = jsonElement.getAsJsonObject().get("_type").getAsString();

        var inner = ctx.deserialize(jsonElement, Class.forName(typ));
        return new SubClass<T>((T) inner);
    }

    @Override
    public JsonElement serialize(SubClass<T> data, Type type, JsonSerializationContext ctx) {
        var json = ctx.serialize(data.getValue()).getAsJsonObject();

        json.addProperty("_type", data.getValue().getClass().getName());

        return json;
    }
}
