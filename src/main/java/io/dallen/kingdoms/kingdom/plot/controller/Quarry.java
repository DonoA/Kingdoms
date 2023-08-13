package io.dallen.kingdoms.kingdom.plot.controller;

import com.google.gson.annotations.Expose;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.OptionCost;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.util.Bounds;
import io.dallen.kingdoms.util.ItemStackMaterial;
import io.dallen.kingdoms.util.ItemUtil;
import io.dallen.kingdoms.util.Lazy;
import io.dallen.kingdoms.util.MaterialUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
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
public class Quarry extends PlotController {
    private final static Material floorMaterial = Material.COBBLESTONE;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    private final PlotInventory storageInventory = new PlotInventory();

    @Expose(serialize = false, deserialize = false)
    private final Lazy<BasicPlotGUI> controllerMenu = new Lazy<>(() ->
            new BasicPlotGUI("Quarry", plot));

    private final PlotRequirement inputChest = new PlotRequirement("Input Chest");
    private final PlotRequirement outputChest = new PlotRequirement("Output Chest");
    private final PlotRequirement storageSpace = new PlotRequirement("Storage Space");
    private final PlotRequirement bed = new PlotRequirement("Bed");

    public List<PlotRequirement> getAllReqs() {
        return List.of(
                inputChest, outputChest, storageSpace, bed
        );
    }

    @Override
    public List<PlotInventory> getAllPlotInventories() {
        return List.of(
                storageInventory
        );
    }

    private int maxDepth;
    private Location lastBroken;
    private boolean done;

    public Quarry(Ref<Plot> plot) {
        super(plot, "Quarry");
        var bounds = getPlot().getBounds();
        this.maxDepth = Math.max(bounds.getSizeX(), bounds.getSizeZ()) - 1;
        this.lastBroken = new Location(bounds.getWorld(),
                bounds.getBlockX() - bounds.getMinusX(),
                bounds.getBlockY() - 1,
                bounds.getBlockZ() - bounds.getMinusZ());
        this.done = false;
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
        Bukkit.broadcastMessage("Depth " + maxDepth);
    }

    @Override
    public void onDestroy() {
//        getPlot().setFloor(Material.DIRT);
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
            var chestData = CustomBlockData.getBlockData(blocLoc, PlotChest.ChestMetadata.class);
            PlotChest.PlotChestType chestType = null;
            if (chestData != null) {
                chestType = chestData.getTyp();
            }

            if (chestType == PlotChest.PlotChestType.INPUT) {
                inputChest.setPoi(blocLoc);
            } else if (chestType == PlotChest.PlotChestType.OUTPUT) {
                outputChest.setPoi(blocLoc);
            } else {
                storageInventory.getChests().add(blocLoc);
                storageSpace.setPoi(blocLoc);
            }
        }
    }

    @Override
    public ChestGUI getPlotMenu() {
        scanPlot();
        controllerMenu.get().updateWithReqs(getAllReqs());

        controllerMenu.get().setOption(10, CustomItemIndex.INCREASE.toItemStack(), "Done?", String.valueOf(done));
        controllerMenu.get().setOption(11, CustomItemIndex.INCREASE.toItemStack(), "Target Block", lastBroken.toString());

        controllerMenu.get().refreshAllViewers();
        return controllerMenu.get();
    }

    @Override
    public void tick() {
        if (done) {
            return;
        }

        var nextBlock = getNextBlock(lastBroken);
        if (nextBlock == null) {
            done = true;
            return;
        }

        nextBlock.getBlock().setType(Material.AIR);
        lastBroken = nextBlock;
    }

    private Location getNextBlock(Location lastBroken) {
        var bounds = getPlot().getBounds();
        var startY = bounds.getBlockY() - lastBroken.getBlockY();
        var startX = lastBroken.getBlockX() - bounds.getBlockX();
        var startZ = lastBroken.getBlockZ() - bounds.getBlockZ();

        for (int y = startY; y <= maxDepth; y++) {
            final int currentRadius = (maxDepth - y) / 2;
            for (int x = startX; x <= bounds.getPlusX(); x++) {
                for (int z = startZ; z <= bounds.getPlusZ(); z++) {
                    var worldX = bounds.getBlockX() + x;
                    var worldZ = bounds.getBlockZ() + z;
                    var worldY = bounds.getBlockY() - y;

                    var loc = new Location(bounds.getWorld(), worldX, worldY, worldZ);
                    var loc2d = new Location(bounds.getWorld(), worldX, bounds.getBlockY(), worldZ);
                    if (loc2d.distance(bounds.center()) < currentRadius && !loc.getBlock().isEmpty()) {
                        return loc;
                    }
                }

                // At end of row, fix z start
                startZ = -bounds.getMinusZ();
            }

            // At end of layer, fix x and z start
            startX = -bounds.getMinusX();
            startZ = -bounds.getMinusZ();
        }

        return null;
    }
}
