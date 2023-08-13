package io.dallen.kingdoms.kingdom.plot.controller;

import com.google.gson.annotations.Expose;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.OptionCost;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.util.ItemStackMaterial;
import io.dallen.kingdoms.util.ItemUtil;
import io.dallen.kingdoms.util.Lazy;
import io.dallen.kingdoms.util.MaterialUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor
public class BuildersHut extends PlotController {
    private final static Material floorMaterial = Material.OAK_PLANKS;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    private final PlotInventory storageInventory = new PlotInventory();

    @Expose(serialize = false, deserialize = false)
    private final Lazy<BasicPlotGUI> controllerMenu = new Lazy<>(() ->
            new BasicPlotGUI("Builder's Hut", plot));

    private final PlotRequirement bed = new PlotRequirement("Bed");

    private final PlotRequirement storageSpace = new PlotRequirement("Storage Space");

    public List<PlotRequirement> getAllReqs() {
        return List.of(
                storageSpace
        );
    }

    @Override
    public List<PlotInventory> getAllPlotInventories() {
        return List.of(
                storageInventory
        );
    }

    public BuildersHut(Ref<Plot> plot) {
        super(plot, "Builder's Hut");
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
    }

    @Override
    public void onDestroy() {
        getPlot().setFloor(Material.DIRT);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        scanPlotAsync();
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        scanPlotAsync();
    }

    @Override
    protected void scanBlock(Location blocLoc, Material typ) {
        if (MaterialUtil.isBed(typ)) {
            bed.setPoi(blocLoc);
            return;
        }

        if (Material.CHEST.equals(typ)) {
            storageInventory.getChests().add(blocLoc);
        }
    }

    @Override
    public ChestGUI getPlotMenu() {
        scanPlot();
        controllerMenu.get().updateWithReqs(getAllReqs());

        controllerMenu.get().refreshAllViewers();
        return controllerMenu.get();
    }

    @Override
    public void tick() {

    }
}
