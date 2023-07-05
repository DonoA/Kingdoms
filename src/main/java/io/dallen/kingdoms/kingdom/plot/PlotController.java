package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@AllArgsConstructor
public abstract class PlotController {

    protected Ref<Plot> plot;

    public Plot getPlot() {
        return plot.get();
    }

    public void onCreate() { }

    public void onPlace(BlockPlaceEvent event) { }

    public void onBreak(BlockBreakEvent event) { }

    public void onDestroy() { }

}
