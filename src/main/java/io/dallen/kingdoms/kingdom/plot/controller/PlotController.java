package io.dallen.kingdoms.kingdom.plot.controller;

import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@AllArgsConstructor
public abstract class PlotController {

    protected Ref<Plot> plot;

    @Getter
    private final String name;

    public Plot getPlot() {
        return plot.get();
    }

    public void onCreate() { }

    public void onPlace(BlockPlaceEvent event) { }

    public void onBreak(BlockBreakEvent event) { }

    public void onDestroy() { }

    public abstract ChestGUI getPlotMenu();

    public ChestGUI getCraftingMenu() {
        return null;
    }

    public void tick() { };
}
