package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.StoneCutter;
import io.dallen.kingdoms.kingdom.plot.Storage;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.ChestSizingGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlotBlock extends CustomBlock {

    @AllArgsConstructor
    @Getter
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

    public void onBreak(BlockBreakEvent event) {
        var data = CustomBlockData.getBlockData(event.getBlock().getLocation(), PlotData.class);
        if (data == null || data.plot == null) {
            return;
        }

        data.plot.get().destroy();
    }

    public void onInteract(PlayerInteractEvent event) {
        var data = CustomBlockData.getBlockData(event.getClickedBlock().getLocation(), PlotData.class);
        if (data == null || data.plot == null) {
            return;
        }

        if (!data.plot.get().getKingdom().isMember(event.getPlayer())) {
            return;
        }

        var plot = data.plot.get();

        var stoneCost = StoneCutter.getCost(plot);

        var gui = new ChestGUI("Plot Controller", 9);
        gui.setOption(1, new ItemStack(Material.STONE_PICKAXE), "Stone Worker", stoneCost.requirements());
        gui.setOption(2, new ItemStack(Material.BARREL), "Storage");
        gui.setClickHandler((clickEvent) -> {
            switch (clickEvent.getClicked().getType()) {
                case STONE_PICKAXE:
                    if (!stoneCost.canPurchase(event.getPlayer().getInventory())) {
                        clickEvent.setNext(gui);
                        event.getPlayer().sendMessage("Too expensive!");
                    } else {
                        stoneCost.purchase(event.getPlayer().getInventory());
                        plot.setController(new StoneCutter(plot.asRef()));
                    }
                    break;
                case BARREL:
                    plot.setController(new Storage(plot.asRef()));
                    break;

            }
        });

        gui.sendMenu(event.getPlayer());
    }

}
