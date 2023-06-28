package io.dallen.kingdoms.savedata.adapters;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import java.lang.reflect.Type;
import java.util.UUID;

public class NPCAdapter implements JsonSerializer<NPC>, JsonDeserializer<NPC> {

    public static void register(GsonBuilder builder) {
        builder.registerTypeHierarchyAdapter(NPC.class, new NPCAdapter());
    }

    @Override
    public NPC deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var uuid = jsonElement.getAsJsonObject().get("uuid").getAsString();
        return CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(UUID.fromString(uuid));
    }

    @Override
    public JsonElement serialize(NPC npc, Type type, JsonSerializationContext ctx) {
        var json = new JsonObject();
        json.addProperty("uuid", npc.getUniqueId().toString());
        return json;
    }
}
