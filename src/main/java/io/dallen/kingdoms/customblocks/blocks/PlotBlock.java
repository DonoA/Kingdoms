package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.controller.PlotController;
import io.dallen.kingdoms.kingdom.plot.controller.StoneCutter;
import io.dallen.kingdoms.kingdom.plot.controller.Storage;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.ChestSizingGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlotBlock extends CustomBlock {

    @AllArgsConstructor
    @Getter
    @ToString
    private static class PlotData extends CustomBlockData {
        private Ref<Plot> plot;
    }

    public PlotBlock(Material baseBlock) {
        super("Plot", baseBlock);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        var data = new PlotData(null);
        CustomBlockData.setBlockData(event.getBlock().getLocation(), data);

        var blockLoc = event.getBlock().getLocation();
        var kingdom = Kingdom.findKingdom(blockLoc);
        if (kingdom == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Plot must be placed within kingdom");
            return;
        }

        ChestSizingGUI.builder()
                .title("New Plot")
                .blockLoc(blockLoc)
                .complete((bounds) -> {
                    bounds.setHeight(10);
                    var plot = new Plot(blockLoc, kingdom, bounds);
                    plot.register();
                    plot.placeBounds();
                    data.plot = plot.asRef();
                    event.getPlayer().sendMessage("Create plot " + bounds.getSizeX() + " x " + bounds.getSizeZ());
                })
                .exit(() -> {
                    blockLoc.getBlock().breakNaturally();
                })
                .build()
                .sendToPlayer(event.getPlayer());
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        var data = CustomBlockData.removeBlockData(event.getBlock().getLocation(), PlotData.class);
        if (data == null || data.plot == null) {
            return;
        }

        data.plot.get().destroy();
    }

    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);

        var data = CustomBlockData.getBlockData(event.getClickedBlock().getLocation(), PlotData.class);
        if (data == null || data.plot == null) {
            return;
        }

        if (!data.plot.get().getKingdom().isMember(event.getPlayer())) {
            return;
        }

        var plot = data.plot.get();

        if (plot.getController() == null) {
            PlotController.emptyPlotAssign(plot).sendMenu(event.getPlayer());
        } else {
            plot.getController().getPlotMenu().sendMenu(event.getPlayer());
        }
    }




}
