package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.savedata.Ref;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlotChest extends CustomBlock {

    @Data
    public static class ChestMetadata extends CustomBlockData {
        private PlotChestType typ = null;
        private Location leftSide = null;
        private Location rightSide = null;
    }

    public enum PlotChestType {
        INPUT, OUTPUT
    }

    public PlotChest(Material m) {
        super("Chest", m);
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        var loc = event.getBlock().getLocation();
        var metaData = CustomBlockData.removeBlockData(loc, ChestMetadata.class);

        if (metaData == null) {
            return;
        }

        // If we destroyed the left (primary) side, then swap the right to the primary
        if (!loc.equals(metaData.getRightSide())) {
            metaData.setLeftSide(metaData.getRightSide());
        }
        metaData.setRightSide(null);
    }
}
