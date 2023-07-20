package io.dallen.kingdoms.kingdom.plot.controller;

import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@AllArgsConstructor
@NoArgsConstructor
public abstract class PlotController {

    protected Ref<Plot> plot;

    @Getter
    private String name;

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
}
