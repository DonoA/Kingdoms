package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.controller.CraftingPlotController;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlotCrafting extends CustomBlock {
    public PlotCrafting(String name, Material baseBlock) {
        super(name, baseBlock);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);

        var blockLoc = event.getClickedBlock().getLocation();
        var plot = Plot.findPlot(blockLoc);
        if (plot == null || plot.getController() == null) {
            return;
        }

        var controller = plot.getController();
        if (controller instanceof CraftingPlotController) {
            ((CraftingPlotController) controller).getCraftingMenu().sendMenu(event.getPlayer());
        }
    }
}
