package io.dallen.kingdoms.savedata.adapters;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class ApachePairAdapter<L, R> implements JsonSerializer<Pair<L, R>>, JsonDeserializer<Pair<L, R>> {

    private final Class<L> left;
    private final Class<R> right;

    public static void register(GsonBuilder builder) {
        builder.registerTypeAdapter(new TypeToken<Pair<Material, Integer>>(){}.getType(),
                new ApachePairAdapter<>(Material.class, Integer.class));
    }

    @Override
    public Pair<L, R> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var json = jsonElement.getAsJsonObject();
        var leftVal = (L) ctx.deserialize(json.get("left"), left);
        var rightVal = (R) ctx.deserialize(json.get("right"), right);
        return Pair.of(leftVal, rightVal);
    }

    @Override
    public JsonElement serialize(Pair<L, R> lrPair, Type type, JsonSerializationContext ctx) {
        var json = new JsonObject();
        json.add("left", ctx.serialize(lrPair.getLeft(), left));
        json.add("right", ctx.serialize(lrPair.getRight(), right));
        return json;
    }
}
