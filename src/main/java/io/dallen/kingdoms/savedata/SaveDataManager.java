package io.dallen.kingdoms.savedata;

import java.util.HashMap;

public class SaveDataManager<K, V extends SaveData<V>> extends HashMap<K, V> {
    public Ref<V> getRef(K key) {
        return Ref.create(this, key);
    }
}
