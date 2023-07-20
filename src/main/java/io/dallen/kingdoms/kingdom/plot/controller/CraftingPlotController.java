package io.dallen.kingdoms.kingdom.plot.controller;

import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class CraftingPlotController extends PlotController {

    public CraftingPlotController(Ref<Plot> plot, String name) {
        super(plot, name);
    }

    public abstract ChestGUI getPlotMenu();

    public abstract ChestGUI getCraftingMenu();
}
