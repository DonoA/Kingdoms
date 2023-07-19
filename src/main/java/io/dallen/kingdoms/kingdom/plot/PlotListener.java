package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class PlotListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        var placeLoc = event.getBlockPlaced().getLocation();
        for (var plot :  Plot.getPlotIndex().values()) {
            if (!plot.getBounds().contains(placeLoc.getBlockX(), placeLoc.getBlockY(), placeLoc.getBlockZ())) {
                return;
            }

            if (plot.getController() == null) {
                return;
            }

            plot.getController().onPlace(event);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        var placeLoc = event.getBlock().getLocation();
        for (var plot :  Plot.getPlotIndex().values()) {
            if (!plot.getBounds().contains(placeLoc.getBlockX(), placeLoc.getBlockY(), placeLoc.getBlockZ())) {
                return;
            }

            if (plot.getController() == null) {
                return;
            }

            plot.getController().onBreak(event);
        }
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent e){
        var holder = e.getInventory().getHolder();
        Location chestLoc;
        if (holder instanceof Chest){
            chestLoc = ((Chest) holder).getLocation();
        } else if (holder instanceof DoubleChest) {
            var leftChest = (Chest) ((DoubleChest) holder).getLeftSide();
            chestLoc = leftChest.getBlock().getLocation();
        } else {
            return;
        }

        var chestData = CustomBlockData.getBlockData(chestLoc, PlotChest.ChestMetadata.class);
        if (chestData == null) {
            return;
        }

        var invTitle = e.getView().getTitle();
        if ("Input Chest".equals(invTitle) || "Output Chest".equals(invTitle)) {
            return;
        }

        if (chestData.getTyp() == PlotChest.PlotChestType.INPUT) {
            e.setCancelled(true);
            e.getView().setTitle("Input Chest");
            e.getPlayer().openInventory(e.getView());
        } else if (chestData.getTyp() == PlotChest.PlotChestType.OUTPUT) {
            e.setCancelled(true);
            e.getView().setTitle("Output Chest");
            e.getPlayer().openInventory(e.getView());
        }
    }
}
