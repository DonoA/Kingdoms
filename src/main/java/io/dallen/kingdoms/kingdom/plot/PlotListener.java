package io.dallen.kingdoms.kingdom.plot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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

}
