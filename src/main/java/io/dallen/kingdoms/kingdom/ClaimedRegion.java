package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.savedata.SaveDataManager;
import io.dallen.kingdoms.util.Bounds;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

@RequiredArgsConstructor
public abstract class ClaimedRegion<K, V> {

    protected final AreaBounds areaBounds;

    @Getter
    protected final Location block;

    protected abstract Material getBorderMaterial();
    protected abstract SaveDataManager<K, V> getIndex();
    protected abstract K getId();


    public void placeBounds() {
        areaBounds.placeBounds(getBorderMaterial());
    }

    public void eraseBounds() {
        areaBounds.eraseBounds(Material.DIRT);
    }

    public Bounds getBounds() {
        return areaBounds.getBounds();
    }

    public Ref<V> asRef() {
        return Ref.create(getIndex(), getId(), (V) this);
    }

    public void register() {
        getIndex().put(getId(), (V) this);
    }

    public void destroy() {
        eraseBounds();
        getIndex().remove(getId());
    }
}
