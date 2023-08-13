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
import io.dallen.kingdoms.util.Lazy;
import io.dallen.kingdoms.util.MaterialUtil;
import io.dallen.kingdoms.util.ToolTypes;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
public class Quarry extends PlotController {
    private final static Material floorMaterial = Material.COBBLESTONE;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    private final PlotInventory inputInventory = new PlotInventory();
    private final PlotInventory outputInventory = new PlotInventory();

    @Expose(serialize = false, deserialize = false)
    private final Lazy<BasicPlotGUI> controllerMenu = new Lazy<>(() ->
            new BasicPlotGUI("Quarry", plot));

    private final PlotRequirement inputChest = new PlotRequirement("Input Chest");
    private final PlotRequirement outputChest = new PlotRequirement("Output Chest");
    private final PlotRequirement bed = new PlotRequirement("Bed");

    public List<PlotRequirement> getAllReqs() {
        return List.of(
                inputChest, outputChest, bed
        );
    }

    @Override
    public List<PlotInventory> getAllPlotInventories() {
        return List.of(
                inputInventory, outputInventory
        );
    }

    private int maxDepth;
    private Location currentBlock;
    private boolean done;
    private int blockHits = 0;

    public Quarry(Ref<Plot> plot) {
        super(plot, "Quarry");
        var bounds = getPlot().getBounds();
        this.maxDepth = Math.max(bounds.getSizeX(), bounds.getSizeZ()) - 1;
        var minPoint = bounds.minPoint();
        this.currentBlock = new Location(bounds.getWorld(),
                minPoint.getBlockX(),
                bounds.getBlockY() - 1,
                minPoint.getBlockZ());
        this.done = false;
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
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
                inputInventory.getChests().add(blocLoc);
            } else if (chestType == PlotChest.PlotChestType.OUTPUT) {
                outputChest.setPoi(blocLoc);
                outputInventory.getChests().add(blocLoc);
            }
        }
    }

    @Override
    public ChestGUI getPlotMenu() {
        refreshControllerMenu();
        return controllerMenu.get();
    }

    private void refreshControllerMenu() {
        scanPlot();
        controllerMenu.get().updateWithReqs(getAllReqs());

//        controllerMenu.get().setOption(10, CustomItemIndex.INCREASE.toItemStack(), "Done?", String.valueOf(done));
//        controllerMenu.get().setOption(11, CustomItemIndex.INCREASE.toItemStack(), "Target Block", currentBlock.toString());

        controllerMenu.get().refreshAllViewers();
    }

    @Override
    public void tick() {
        refreshControllerMenu();

        if (done) {
            return;
        }

        var didMine = locateAndDamage(ToolTypes.getPickaxes(), inputInventory);
        if (!didMine) {
            return;
        }

        blockHits++;
        if (blockHits < 10) {
            return;
        }
        blockHits = 0;

        var bloc = currentBlock.getBlock();
        outputInventory.addItem(new ItemStack(bloc.getType()));
        currentBlock.getBlock().setType(Material.AIR);

        var nextBlock = getNextBlock(currentBlock);
        if (nextBlock == null) {
            done = true;
        } else {
            currentBlock = nextBlock;
        }
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
