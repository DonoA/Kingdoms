package io.dallen.kingdoms.kingdom.plot.controller;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
public abstract class PlotController {

    protected Ref<Plot> plot;

    @Getter
    private String name;

    public abstract List<PlotRequirement> getAllReqs();
    public abstract List<PlotInventory> getAllPlotInventories();
    protected abstract void scanBlock(Location blocLoc, Material typ);

    public Plot getPlot() {
        return plot.get();
    }

    public void onCreate() { }

    public void onPlace(BlockPlaceEvent event) { }

    public void onBreak(BlockBreakEvent event) { }

    public void onDestroy() { }

    public abstract ChestGUI getPlotMenu();

    public void tick() { };

    public void onInteract(PlayerInteractEvent event) { }

    protected void scanPlotAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(Kingdoms.instance, this::scanPlot);
    }

    protected void scanPlot() {
        var plotWorld = getPlot().getBlock().getWorld();
        getAllReqs().forEach(req -> req.setRechecked(false));
        getAllPlotInventories().forEach(inv -> inv.getChests().clear());
        getPlot().getBounds().forEach(((x, y, z, i) -> {
            var blocLoc = new Location(plotWorld, x, y, z);
            var typ = blocLoc.getBlock().getType();
            scanBlock(blocLoc, typ);
        }));
        getAllReqs().forEach(PlotRequirement::ensureRechecked);
        checkEnabled();
    }

    protected boolean checkEnabled() {
        for(var req : getAllReqs()) {
            if (!req.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    protected boolean locateAndDamage(Set<Material> tools, PlotInventory toolInv) {
        var offset = toolInv.firstOf(tools);
        if (offset == -1) {
            return false;
        }

        var item = toolInv.getItem(offset);
        var meta = (Damageable) item.getItemMeta();
        meta.setDamage(meta.getDamage() + 1);

        if (meta.getDamage() >= item.getType().getMaxDurability()) {
            toolInv.clear(offset);
        } else {
            item.setItemMeta(meta);
        }

        return true;
    }
}
