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
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    public static void register(GsonBuilder builder) {
        builder.registerTypeHierarchyAdapter(Location.class, new LocationAdapter());
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        var world = Bukkit.getWorld(obj.get("world").getAsString());
        var x = obj.get("x").getAsDouble();
        var y = obj.get("y").getAsDouble();
        var z = obj.get("z").getAsDouble();
        return new Location(world, x, y, z);
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        var json = new JsonObject();
        json.addProperty("world", location.getWorld().getName());
        json.addProperty("x", location.getX());
        json.addProperty("y", location.getY());
        json.addProperty("z", location.getZ());
        return json;
    }
}
