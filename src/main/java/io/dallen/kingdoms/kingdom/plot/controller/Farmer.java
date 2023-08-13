package io.dallen.kingdoms.kingdom.plot.controller;

import com.google.gson.annotations.Expose;
import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.util.Lazy;
import io.dallen.kingdoms.util.MaterialUtil;
import io.dallen.kingdoms.util.ToolTypes;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class Farmer extends PlotController {

    private final PlotInventory inputInventory = new PlotInventory();
    private final PlotInventory outputInventory = new PlotInventory();

    private final PlotRequirement bed = new PlotRequirement("Bed");
    private final PlotRequirement inputChest = new PlotRequirement("Input Chest");
    private final PlotRequirement outputChest = new PlotRequirement("Output Chest");

    public List<PlotRequirement> getAllReqs() {
        return List.of(
                bed, inputChest, outputChest
        );
    }

    @Override
    public List<PlotInventory> getAllPlotInventories() {
        return List.of(
                inputInventory, outputInventory
        );
    }

    @Expose(serialize = false, deserialize = false)
    private final Lazy<BasicPlotGUI> controllerMenu = new Lazy<>(() ->
            new BasicPlotGUI("Farmer", plot));

    public Farmer(Ref<Plot> plot) {
        super(plot, "Farmer");
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(Material.DIRT);
    }

    @Override
    public void onDestroy() {
        getPlot().setFloor(Material.DIRT);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        super.scanPlotAsync();
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
            if (chestData == null) {
                return;
            }

            if (chestData.getTyp() == PlotChest.PlotChestType.INPUT) {
                inputInventory.getChests().add(blocLoc);
                inputChest.setPoi(blocLoc);
            } else if (chestData.getTyp() == PlotChest.PlotChestType.OUTPUT) {
                outputInventory.getChests().add(blocLoc);
                outputChest.setPoi(blocLoc);
            }
        }
    }

    @Override
    public ChestGUI getPlotMenu() {
        scanPlot();
        refreshControllerMenu();
        return controllerMenu.get();
    }

    private void refreshControllerMenu() {
        controllerMenu.get().updateWithReqs(getAllReqs());
        controllerMenu.get().refreshAllViewers();
    }

    @Override
    public void tick() {
        if (!checkEnabled()) {
            return;
        }

        tickFarming();
    }

    private void tickFarming() {
        var plotWorld = getPlot().getBlock().getWorld();
        getPlot().getBounds().forEach(((x, y, z, i) -> {
            var block = new Location(plotWorld, x, y, z).getBlock();
            var blockData = block.getBlockData();

            var drop = getDrop(block.getType());
            if (drop != null) {
                var ageable = (Ageable) blockData;

                if (ageable.getAge() >= ageable.getMaximumAge()) {
                    var didHarvest = locateAndDamage(ToolTypes.getHoes(), inputInventory);
                    if (didHarvest) {
                        ageable.setAge(0);
                        block.setBlockData(ageable);
                        outputInventory.addItem(new ItemStack(drop));
                    }
                }
            }
        }));
    }

    private Material getDrop(Material mat) {
        switch (mat) {
            case WHEAT:
                return Material.WHEAT;
            case BEETROOTS:
                return Material.BEETROOT;
            case CARROTS:
                return Material.CARROT;
            case POTATOES:
                return Material.POTATO;
            case SWEET_BERRY_BUSH:
                return Material.SWEET_BERRIES;
            default:
                return null;
        }
    }
}
