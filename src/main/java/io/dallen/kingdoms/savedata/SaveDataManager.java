package io.dallen.kingdoms.savedata;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.dallen.kingdoms.Kingdoms;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveDataManager<K, V> extends HashMap<K, V> {

    protected final String saveLocation;
    protected final Class<K> keyType;

    public SaveDataManager(String saveLocation, Class<K> keyType) {
        this.saveLocation = saveLocation;
        this.keyType = keyType;
    }

    public void saveAll() {
        try {
            var elementList = new ArrayList<JsonObject>();
            for (var entry : this.entrySet()) {
                var jsonKey = Kingdoms.gson.toJsonTree(entry.getKey());
                var valueType = entry.getValue().getClass().getName();

                var jsonObject = new JsonObject();
                jsonObject.add("key", jsonKey);
                jsonObject.addProperty("type", valueType);
                jsonObject.add("value", Kingdoms.gson.toJsonTree(entry.getValue()));
                elementList.add(jsonObject);
            }
            var json = Kingdoms.gson.toJson(elementList);
            FileManager.writeDataFile(saveLocation, json);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to save complex save data");
            ex.printStackTrace();
        }
    }

    public void loadAll() {
        try {
            var rawJson = FileManager.readDataFile(saveLocation);
            var loadedData = Kingdoms.gson.fromJson(rawJson, JsonArray.class);
            for (var entry : loadedData) {
                var jsonObj = entry.getAsJsonObject();
                var keyObj = Kingdoms.gson.fromJson(jsonObj.get("key"), keyType);
                var valueType = jsonObj.get("type").getAsString();
                var valueClass = Class.forName(valueType);
                var valueObj = Kingdoms.gson.fromJson(jsonObj.get("value"), valueClass);

                this.put(keyObj, (V) valueObj);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to load complex save data");
            ex.printStackTrace();
        }
    }
}
