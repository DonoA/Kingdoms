package io.dallen.kingdoms.kingdom.plot.controller;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public abstract class PlotController {

    protected Ref<Plot> plot;

    @Getter
    private String name;

    public abstract List<PlotRequirement> getAllReqs();
    public abstract List<PlotInventory> getAllPlotInventories();
    protected abstract void scanBlock(Location blocLoc, Material typ);

    public Plot getPlot() {
        return plot.get();
    }

    public void onCreate() { }

    public void onPlace(BlockPlaceEvent event) { }

    public void onBreak(BlockBreakEvent event) { }

    public void onDestroy() { }

    public abstract ChestGUI getPlotMenu();

    public void tick() { };

    public void onInteract(PlayerInteractEvent event) { }

    protected void scanPlotAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(Kingdoms.instance, this::scanPlot);
    }

    protected void scanPlot() {
        var plotWorld = getPlot().getBlock().getWorld();
        getAllReqs().forEach(req -> req.setRechecked(false));
        getAllPlotInventories().forEach(inv -> inv.getChests().clear());
        getPlot().getBounds().forEach(((x, y, z, i) -> {
            var blocLoc = new Location(plotWorld, x, y, z);
            var typ = blocLoc.getBlock().getType();
            scanBlock(blocLoc, typ);
        }));
        getAllReqs().forEach(PlotRequirement::ensureRechecked);
        checkEnabled();
    }

    protected boolean checkEnabled() {
        for(var req : getAllReqs()) {
            if (!req.isCompleted()) {
                return false;
            }
        }
        return true;
    }


    public static ChestGUI emptyPlotAssign(Plot plot) {
        var stoneCutterCost = StoneCutter.getCost(plot);
        var storageCost = Storage.getCost(plot);
        var quarryCost = Quarry.getCost(plot);

        var guiName = "Plot";
        if (plot.getController() != null) {
            guiName = "Plot (" + plot.getController().getName() + ")";
        }
        var gui = new ChestGUI(guiName, 9);
        gui.setOption(0, new ItemStack(Material.STONE_PICKAXE), "Stone Worker", stoneCutterCost.requirements());
        gui.setOption(1, new ItemStack(Material.WHEAT), "Farm");
        gui.setOption(2, new ItemStack(Material.BARREL), "Storage", storageCost.requirements());
        gui.setOption(3, new ItemStack(Material.COBBLESTONE), "Quarry", quarryCost.requirements());

        if (plot.getController() != null) {
            gui.setOption(8, CustomItemIndex.CANCEL.toItemStack(), "Clear Plot");
        }
        gui.setClickHandler(clickEvent -> handlePlotAssign(clickEvent, plot));
        return gui;
    }

    private static void handlePlotAssign(ChestGUI.OptionClickEvent clickEvent, Plot plot) {
        var player = clickEvent.getPlayer();
        switch (clickEvent.getClicked().getType()) {
            case STONE_PICKAXE:
                var stoneCost = StoneCutter.getCost(plot);
                if (!stoneCost.canPurchase(player.getInventory())) {
                    clickEvent.setClose(false);
                    player.sendMessage("Too expensive!");
                } else {
                    stoneCost.purchase(player.getInventory());
                    plot.setController(new StoneCutter(plot.asRef()));
                }
                break;
            case WHEAT:
                plot.setController(new Farmer(plot.asRef()));
                break;
            case BARREL:
                var storageCost = Storage.getCost(plot);
                if (!storageCost.canPurchase(player.getInventory())) {
                    clickEvent.setClose(false);
                    player.sendMessage("Too expensive!");
                } else {
                    storageCost.purchase(player.getInventory());
                    plot.setController(new Storage(plot.asRef()));
                }
                break;
            case COBBLESTONE:
                var quarryCost = Quarry.getCost(plot);
                if (!quarryCost.canPurchase(player.getInventory())) {
                    clickEvent.setClose(false);
                    player.sendMessage("Too expensive!");
                } else {
                    quarryCost.purchase(player.getInventory());
                    plot.setController(new Quarry(plot.asRef()));
                }
                break;
        }
    }
}
