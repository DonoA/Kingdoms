package io.dallen.kingdoms.kingdom.plot.controller;

import com.sk89q.worldedit.WorldEditException;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.util.WorldEditUtil;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class BasicPlotGUI extends ChestGUI {

    private final Ref<Plot> plotRef;

    public BasicPlotGUI(String name, Ref<Plot> plotRef) {
        super(name, 18);
        this.plotRef = plotRef;
        setClickHandler(this::controllerClick);
    }

    public Plot getPlot() {
        return plotRef.get();
    }

    @SneakyThrows
    private void controllerClick(ChestGUI.OptionClickEvent optionClickEvent) {
        if (optionClickEvent.getClicked().getType() == CustomItemIndex.RECYCLE.toMaterial()) {
            getPlot().setController(null);
            return;
        }

        if (optionClickEvent.getClicked().getType() == Material.BOOK) {
            optionClickEvent.setNext(createSchematicMenu());
            return;
        }

        if (optionClickEvent.getClicked().getType() == Material.ENCHANTED_BOOK) {
            optionClickEvent.setNext(getSchematicMenu());
            return;
        }

        optionClickEvent.setClose(false);
    }

    public ChestGUI createSchematicMenu() {
        var gui = new ChestGUI("Create Blueprint", InventoryType.ANVIL);
        gui.setClickHandler((menuEvent) -> {
            var anvilMenu = (ChestGUI.AnvilMenuInstance) menuEvent.getMenu();
            var blueprintName = anvilMenu.getCurrentItemName();
            var plotBounds = getPlot().getBounds();

            try {
                var exactName = WorldEditUtil.saveBounds(plotBounds, blueprintName);
                getPlot().getKingdom().getBlueprints().put(blueprintName, exactName);
            } catch (WorldEditException | IOException e) {
                menuEvent.getPlayer().sendMessage("Failed to save blueprint");
                e.printStackTrace();
            }
        });

        gui.setOption(0, new ItemStack(Material.BOOK), "Name Blueprint");
        return gui;
    }

    public ChestGUI getSchematicMenu() {
        var blueprints = getPlot().getKingdom().blueprintNames();
        var gui = new ChestGUI("Plan Blueprint", blueprints.size());
        gui.setClickHandler((menuEvent) -> {
            if (menuEvent.getClicked().getType() == Material.AIR) {
                menuEvent.setClose(false);
                return;
            }

            var blueprintName = menuEvent.getClicked().getItemMeta().getDisplayName();
            var exactName = getPlot().getKingdom().getBlueprintForName(blueprintName);

            try {
                WorldEditUtil.loadToBounds(getPlot().getBounds(), exactName);
            } catch (WorldEditException | IOException e) {
                menuEvent.getPlayer().sendMessage("Failed to load blueprint");
                e.printStackTrace();
            }
        });

        for (var blueprint : blueprints) {
            gui.addOption(new ItemStack(Material.ENCHANTED_BOOK), blueprint);
        }

        return gui;
    }

    public void updateWithReqs(List<PlotRequirement> requirementList) {
        for (int i = 0; i < requirementList.size(); i++) {
            var req = requirementList.get(i);
            if (req.isCompleted()) {
                setOption(i, CustomItemIndex.SUBMIT.toItemStack(), req.getName(), "Completed!");
            } else {
                setOption(i, CustomItemIndex.CANCEL.toItemStack(), req.getName(), "Incomplete!");
            }
        }
        setOption(9, CustomItemIndex.RECYCLE.toItemStack(), "Change plot type");
        setOption(10, new ItemStack(Material.BOOK), "Save Plot Schematic");
        setOption(11, new ItemStack(Material.ENCHANTED_BOOK), "Load Plot Schematic");
    }
}
