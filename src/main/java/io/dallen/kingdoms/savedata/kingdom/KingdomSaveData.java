package io.dallen.kingdoms.savedata.kingdom;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.savedata.FileManager;
import io.dallen.kingdoms.savedata.SaveDataManager;

public class KingdomSaveData extends SaveDataManager<String, Kingdom> {

    public void saveAll() {
        var json = Kingdoms.gson.toJson(this);
        FileManager.writeDataFile(FileManager.KINGDOMS_SAVE_DATA, json);
    }

    public void loadAll() {
        var rawJson = FileManager.readDataFile(FileManager.KINGDOMS_SAVE_DATA);
        var kingdoms = Kingdoms.gson.fromJson(rawJson, KingdomSaveData.class);
        this.putAll(kingdoms);
    }
}
