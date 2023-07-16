package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.kingdom.AreaBounds;
import io.dallen.kingdoms.kingdom.ClaimedRegion;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.savedata.FileManager;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.savedata.SaveDataManager;
import io.dallen.kingdoms.savedata.SubClass;
import io.dallen.kingdoms.util.Bounds;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class Plot extends ClaimedRegion<UUID, Plot> implements Listener {

    public final static Material outlineMaterial = Material.OBSIDIAN;
    @Getter
    public final static SaveDataManager<UUID, Plot> plotIndex =
            new SaveDataManager<>(FileManager.PLOT_SAVE_DATA, UUID.class);

    private UUID uuid;

    private Ref<Kingdom> kingdom;

    private SubClass<PlotController> controller = null;

    public Plot(Location plotBlock, Kingdom kingdom, Bounds bounds) {
        super(new AreaBounds(bounds, true), plotBlock);
        this.uuid = UUID.randomUUID();
        this.kingdom = kingdom.asRef();
    }

    @Override
    protected Material getBorderMaterial() {
        return outlineMaterial;
    }

    @Override
    protected SaveDataManager<UUID, Plot> getIndex() {
        return plotIndex;
    }

    @Override
    protected UUID getId() {
        return uuid;
    }

    public Kingdom getKingdom() {
        return kingdom.get();
    }

    @Override
    public void placeBounds() {
        setFloor(Material.COARSE_DIRT);
        super.placeBounds();
    }

    @Override
    public void eraseBounds() {
        setFloor(Material.DIRT);
    }

    @Override
    public void destroy() {
        var controller = getController();

        if (controller != null) {
            controller.onDestroy();
        }
    }

    public void setFloor(Material mat) {
        getBounds().forEachBase((x, z, index) -> {
            var loc = new Location(getBlock().getWorld(), x, getBlock().getBlockY() - 1, z);
            loc.getBlock().setType(mat);
        });
    }

    public PlotController getController() {
        if (controller == null) {
            return null;
        }

        return controller.getValue();
    }

    public void setController(PlotController controller) {
        if (getController() != null) {
            getController().onDestroy();
        }

        this.controller = new SubClass<>(controller);
        getController().onCreate();
    }
}
