package io.dallen.kingdoms.kingdom.plot.controller;

import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.menus.ChestGUI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PlotRequirement {
    private final String name;

    private boolean completed = false;
    private Location poi = null;

    @Setter
    private boolean rechecked = false;

    public void setPoi(Location newPoi) {
        if (newPoi == null) {
            completed = false;
        } else {
            rechecked = true;
            completed = true;
        }
        poi = newPoi;
    }

    public void ensureRechecked() {
        if (rechecked) {
            return;
        }

        completed = false;
        poi = null;
    }


}
