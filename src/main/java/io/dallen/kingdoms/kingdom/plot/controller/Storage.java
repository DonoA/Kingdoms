package io.dallen.kingdoms.kingdom.plot.controller;

import com.google.gson.annotations.Expose;
import io.dallen.kingdoms.Kingdoms;
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
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

@NoArgsConstructor
public class Storage extends PlotController {
    private final static Material floorMaterial = Material.OAK_PLANKS;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    private final PlotInventory storageInventory = new PlotInventory();

    @Expose(serialize = false, deserialize = false)
    private final Lazy<ChestGUI> controllerMenu = new Lazy<>(() ->
            new ChestGUI("Storage", 18));

    private final PlotRequirement inputChest = new PlotRequirement("Input Chest");
    private final PlotRequirement outputChest = new PlotRequirement("Output Chest");
    private final PlotRequirement storageSpace = new PlotRequirement("Storage Space");

    private List<PlotRequirement> getAllReqs() {
        return List.of(
                inputChest, outputChest, storageSpace
        );
    }

    private long workAdded = 0;

    public Storage(Ref<Plot> plot) {
        super(plot, "Storage");
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        scanPlotAsync();
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        scanPlotAsync();
    }

    private void scanPlotAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(Kingdoms.instance, this::scanPlot);
    }

    private void scanPlot() {
        var plotWorld = getPlot().getBlock().getWorld();
        getAllReqs().forEach(req -> req.setRechecked(false));
        storageInventory.getChests().clear();
        getPlot().getBounds().forEach(((x, y, z, i) -> {
            var blocLoc = new Location(plotWorld, x, y, z);
            var typ = blocLoc.getBlock().getType();
            checkPoi(blocLoc, typ);
        }));
        getAllReqs().forEach(PlotRequirement::ensureRechecked);


    }

    private void checkPoi(Location blocLoc, Material typ) {
        if (!Material.CHEST.equals(typ)) {
            return;
        }

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

    @Override
    public ChestGUI getPlotMenu() {
        scanPlot();
        PlotRequirement.updateWithReqs(controllerMenu.get(), getAllReqs());

        controllerMenu.get().refreshAllViewers();
        return controllerMenu.get();
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        var blocLoc = event.getClickedBlock().getLocation();
        var chestData = CustomBlockData.getBlockData(blocLoc, PlotChest.ChestMetadata.class);

        if (chestData == null || chestData.getTyp() != PlotChest.PlotChestType.OUTPUT) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage("Clicked output");
    }
}
