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
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.savedata.SaveDataManager;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class RefAdapter<K, V> implements JsonSerializer<Ref<V>>, JsonDeserializer<Ref<V>>  {

    private final SaveDataManager<K, V> dataManager;
    private final Class<K> keyClass;

    public static void register(GsonBuilder builder) {
        builder.registerTypeAdapter(new TypeToken<Ref<Kingdom>>(){}.getType(),
                new RefAdapter<>(Kingdom.getKingdomIndex(), String.class));
    }

    @Override
    public Ref<V> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var id = jsonElement.getAsJsonObject().get("id");

        return Ref.create(dataManager, ctx.deserialize(id, keyClass));
    }

    @Override
    public JsonElement serialize(Ref<V> ref, Type type, JsonSerializationContext ctx) {
        var json = new JsonObject();

        var id = ref.getKey();
        var jsonId = ctx.serialize(id);
        json.add("id", jsonId);

        return json;
    }
}
