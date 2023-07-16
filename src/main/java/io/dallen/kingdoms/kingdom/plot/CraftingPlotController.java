package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public abstract class CraftingPlotController extends PlotController {

    public CraftingPlotController(Ref<Plot> plot, String name) {
        super(plot, name);
    }

    public abstract ChestGUI getPlotMenu();

    public abstract ChestGUI getCraftingMenu();
}
