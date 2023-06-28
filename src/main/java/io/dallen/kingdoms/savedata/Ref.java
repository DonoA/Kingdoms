package io.dallen.kingdoms.savedata;

import lombok.Getter;

public class Ref<V>{

    private final SaveDataManager<Object, V> index;

    @Getter
    private final Object key;

    private Ref(SaveDataManager<Object, V> index, Object key, V value) {
        this.index = index;
        this.key = key;
        this.value = value;
    }

    public static <K, V> Ref<V> create(SaveDataManager<K, V> index, K key) {
        return create(index, key, null);
    }

    public static <K, V> Ref<V> create(SaveDataManager<K, V> index, K key, V value) {
        return new Ref<>((SaveDataManager<Object, V>) index, key, value);
    }

    private V value;

    public V get() {
        if (value == null) {
            value = index.get(key);
        }

        return value;
    }
}
