package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.kingdom.AreaBounds;
import io.dallen.kingdoms.kingdom.ClaimedRegion;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.kingdom.plot.controller.PlotController;
import io.dallen.kingdoms.savedata.FileManager;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.savedata.SaveDataManager;
import io.dallen.kingdoms.savedata.SubClass;
import io.dallen.kingdoms.util.Bounds;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@ToString
public class Plot extends ClaimedRegion<UUID, Plot> implements Listener {

    public final static Material outlineMaterial = Material.OBSIDIAN;
    @Getter
    public final static SaveDataManager<UUID, Plot> plotIndex =
            new SaveDataManager<>(FileManager.PLOT_SAVE_DATA, UUID.class);

    @Getter
    private UUID uuid;

    private Ref<Kingdom> kingdom;

    private SubClass<PlotController> controller = null;

    public Plot(Location plotBlock, Kingdom kingdom, Bounds bounds) {
        super(new AreaBounds(bounds, true), plotBlock);
        this.uuid = UUID.randomUUID();
        this.kingdom = kingdom.asRef();
    }

    public static Plot findPlot(Location location) {
        for (var plot : plotIndex.values()) {
            if (plot.getBounds().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                return plot;
            }
        }

        return null;
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

        super.destroy();
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

        if (controller == null) {
            this.controller = null;
        } else {
            this.controller = new SubClass<>(controller);
            getController().onCreate();
        }
    }

    public static BukkitTask startTicking(Plugin plugin) {
        return Bukkit.getScheduler().runTaskTimer(plugin, Plot::tickAllPlots, 20, 20);
    }

    private static void tickAllPlots() {
        for (var plot : plotIndex.values()) {
            if (plot.getController() == null) {
                continue;
            }

            if (!plot.getKingdom().isOnline()) {
                continue;
            }

            plot.getController().tick();
        }
    }
}
