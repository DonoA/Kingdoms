package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;

public class Storage extends PlotController {

    public Storage(Ref<Plot> plot) {
        super(plot, "Storage");
    }

    @Override
    public ChestGUI getPlotMenu() {
        return null;
    }
}
