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

    public static void updateWithReqs(ChestGUI gui, List<PlotRequirement> requirementList) {
        for (int i = 0; i < requirementList.size(); i++) {
            var req = requirementList.get(i);
            if (req.isCompleted()) {
                gui.setOption(i, CustomItemIndex.SUBMIT.toItemStack(), req.getName(), "Completed!");
            } else {
                gui.setOption(i, CustomItemIndex.CANCEL.toItemStack(), req.getName(), "Incomplete!");
            }
        }
        gui.setOption(9, CustomItemIndex.RECYCLE.toItemStack(), "Change plot type");
    }
}
