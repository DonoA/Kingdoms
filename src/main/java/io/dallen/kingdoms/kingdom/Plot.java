package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.util.Bounds;
import org.bukkit.Location;

public class Plot {

    private final Kingdom kingdom;
    private final Bounds bounds;
    private final Location plotBlock;

    public Plot(Location plotBlock, Kingdom kingdom, Bounds bounds) {
        this.kingdom = kingdom;
        this.plotBlock = plotBlock;
        this.bounds = bounds;
    }

}
