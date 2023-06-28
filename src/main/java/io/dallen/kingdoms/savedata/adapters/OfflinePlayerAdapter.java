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
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayerAdapter implements JsonSerializer<OfflinePlayer>, JsonDeserializer<OfflinePlayer>  {

    public static void register(GsonBuilder builder) {
        builder.registerTypeHierarchyAdapter(OfflinePlayer.class, new OfflinePlayerAdapter());
    }

    @Override
    public OfflinePlayer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var uuid = jsonElement.getAsJsonObject().get("uuid").getAsString();
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    @Override
    public JsonElement serialize(OfflinePlayer player, Type type, JsonSerializationContext ctx) {
        var json = new JsonObject();

        json.addProperty("uuid", player.getUniqueId().toString());

        return json;
    }
}
