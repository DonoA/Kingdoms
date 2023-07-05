package io.dallen.kingdoms.savedata.adapters;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

import java.lang.reflect.Type;
import java.util.UUID;

public class WorldAdapter implements JsonSerializer<World>, JsonDeserializer<World>  {

    public static void register(GsonBuilder builder) {
        builder.registerTypeHierarchyAdapter(World.class, new WorldAdapter());
    }

    @Override
    public World deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var uuid = jsonElement.getAsJsonObject().get("uuid").getAsString();
        return Bukkit.getWorld(UUID.fromString(uuid));
    }

    @Override
    public JsonElement serialize(World world, Type type, JsonSerializationContext ctx) {
        var json = new JsonObject();

        json.addProperty("uuid", world.getUID().toString());

        return json;
    }
}
