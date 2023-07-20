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
import org.bukkit.event.player.PlayerInteractEvent;

public class PlotListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        var placeLoc = event.getBlockPlaced().getLocation();
        var plot = Plot.findPlot(placeLoc);

        if (plot == null || plot.getController() == null) {
            return;
        }

        plot.getController().onPlace(event);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        var placeLoc = event.getBlock().getLocation();
        var plot = Plot.findPlot(placeLoc);

        if (plot == null || plot.getController() == null) {
            return;
        }

        plot.getController().onBreak(event);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        var clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        var plot = Plot.findPlot(clicked.getLocation());
        if (plot == null || plot.getController() == null) {
            return;
        }

        plot.getController().onInteract(event);
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
