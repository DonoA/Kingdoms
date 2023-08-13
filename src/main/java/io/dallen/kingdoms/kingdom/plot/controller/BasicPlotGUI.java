package io.dallen.kingdoms.kingdom.plot.controller;

import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;

import java.util.List;

public class BasicPlotGUI extends ChestGUI {

    private final Ref<Plot> plotRef;

    public BasicPlotGUI(String name, Ref<Plot> plotRef) {
        super(name, 18);
        this.plotRef = plotRef;
        setClickHandler(this::controllerClick);
    }

    private void controllerClick(ChestGUI.OptionClickEvent optionClickEvent) {
        if (optionClickEvent.getClicked().getType() != CustomItemIndex.RECYCLE.toMaterial()) {
            optionClickEvent.setClose(false);
            return;
        }

        plotRef.get().setController(null);
    }

    public void updateWithReqs(List<PlotRequirement> requirementList) {
        for (int i = 0; i < requirementList.size(); i++) {
            var req = requirementList.get(i);
            if (req.isCompleted()) {
                setOption(i, CustomItemIndex.SUBMIT.toItemStack(), req.getName(), "Completed!");
            } else {
                setOption(i, CustomItemIndex.CANCEL.toItemStack(), req.getName(), "Incomplete!");
            }
        }
        setOption(9, CustomItemIndex.RECYCLE.toItemStack(), "Change plot type");
    }
}
