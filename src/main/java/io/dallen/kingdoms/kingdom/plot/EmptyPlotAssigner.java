package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.controller.BuildersHut;
import io.dallen.kingdoms.kingdom.plot.controller.Farmer;
import io.dallen.kingdoms.kingdom.plot.controller.PlotController;
import io.dallen.kingdoms.kingdom.plot.controller.Quarry;
import io.dallen.kingdoms.kingdom.plot.controller.StoneCutter;
import io.dallen.kingdoms.kingdom.plot.controller.Storage;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.OptionCost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class EmptyPlotAssigner {

    @AllArgsConstructor
    @Getter
    private static class PlotControllerType {
        private final ItemStack icon;
        private final String name;
        private final OptionCost cost;

        private final Supplier<PlotController> createController;
    }

    public static ChestGUI emptyPlotAssign(Plot plot) {
        var allPlotTypes = List.of(
                new PlotControllerType(new ItemStack(Material.STONE_PICKAXE), "Stone Cutter",
                        StoneCutter.getCost(plot), () -> new StoneCutter(plot.asRef())),

                new PlotControllerType(new ItemStack(Material.WHEAT), "Farm",
                        null, () -> new Farmer(plot.asRef())),

                new PlotControllerType(new ItemStack(Material.BARREL), "Storage",
                        Storage.getCost(plot), () -> new Storage(plot.asRef())),

                new PlotControllerType(new ItemStack(Material.COBBLESTONE), "Quarry",
                        Quarry.getCost(plot), () -> new Quarry(plot.asRef())),

                new PlotControllerType(new ItemStack(Material.OAK_STAIRS), "Builder's Hut",
                        BuildersHut.getCost(plot), () -> new BuildersHut(plot.asRef()))
        );

        var guiName = "Plot";
        if (plot.getController() != null) {
            guiName = "Plot (" + plot.getController().getName() + ")";
        }
        var gui = new ChestGUI(guiName, 9);
        for (int i = 0; i < allPlotTypes.size(); i++) {
            var plotType = allPlotTypes.get(i);
            if (plotType.getCost() != null) {
                gui.setOption(i, plotType.getIcon(), plotType.getName(), plotType.getCost().requirements());
            } else {
                gui.setOption(i, plotType.getIcon(), plotType.getName());
            }
        }

        if (plot.getController() != null) {
            gui.setOption(8, CustomItemIndex.CANCEL.toItemStack(), "Clear Plot");
        }
        gui.setClickHandler(clickEvent -> handlePlotAssign(clickEvent, plot, allPlotTypes));
        return gui;
    }

    private static void handlePlotAssign(ChestGUI.OptionClickEvent clickEvent, Plot plot, List<PlotControllerType> allPlotTypes) {
        var player = clickEvent.getPlayer();
        if (clickEvent.getSlot() >= allPlotTypes.size()) {
            clickEvent.setClose(false);
            return;
        }

        var plotType = allPlotTypes.get(clickEvent.getSlot());

        if (plotType.getCost() != null) {
            if (!plotType.getCost().canPurchase(player.getInventory())) {
                clickEvent.setClose(false);
                player.sendMessage("Too expensive!");
                return;
            } else {
                plotType.getCost().purchase(player.getInventory());
            }
        }

        plot.setController(plotType.getCreateController().get());
    }
}
