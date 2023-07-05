package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.menus.OptionCost;
import io.dallen.kingdoms.savedata.Ref;
import org.bukkit.Material;

public class StoneCutter extends PlotController {

    private final static Material floorMaterial = Material.COBBLESTONE;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    public StoneCutter(Ref<Plot> plot) {
        super(plot);
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
    }

}
