package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.kingdom.plot.Plot;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlotCrafting extends CustomBlock {
    public PlotCrafting(String name, Material baseBlock) {
        super(name, baseBlock);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        var blockLoc = event.getClickedBlock().getLocation();
        var plot = Plot.findPlot(blockLoc);
        if (plot == null || plot.getController() == null) {
            return;
        }

        var craftingMenu = plot.getController().getCraftingMenu();
        if (craftingMenu != null) {

        }
    }
}
