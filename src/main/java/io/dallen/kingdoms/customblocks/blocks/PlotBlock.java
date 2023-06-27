package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.kingdom.Plot;
import io.dallen.kingdoms.menus.ChestSizingGUI;
import io.dallen.kingdoms.util.Bounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlotBlock extends CustomBlock {

    private static class PlotData extends CustomBlockData {

    }

    public PlotBlock(Material baseBlock) {
        super("Plot", baseBlock);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        var blockLoc = event.getBlock().getLocation();
        var kingdom = Kingdom.getOwner(blockLoc);
        if (kingdom == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Plot must be placed within kingdom");
            return;
        }

        ChestSizingGUI.builder()
                .title("New Plot")
                .blockLoc(blockLoc)
                .complete((bounds) -> {
                    event.getPlayer().sendMessage("Create plot " + bounds.getSizeX() + " x " + bounds.getSizeZ());
                })
                .exit(() -> {
                    blockLoc.getBlock().breakNaturally();
                })
                .build()
                .sendToPlayer(event.getPlayer());
    }

    private Plot createNewPlot(Location loc, Kingdom kingdom, Bounds bounds) {
        var newPlot = new Plot(loc, kingdom, bounds);

        return newPlot;
    }
}
