package io.dallen.kingdoms.savedata;

public interface SaveData<T extends SaveData<T>> {

    Ref<T> asRef();

}
