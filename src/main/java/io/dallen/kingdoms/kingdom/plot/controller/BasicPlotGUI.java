package io.dallen.kingdoms.kingdom.plot.controller;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.FileManager;
import io.dallen.kingdoms.savedata.Ref;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class BasicPlotGUI extends ChestGUI {

    private final Ref<Plot> plotRef;

    public BasicPlotGUI(String name, Ref<Plot> plotRef) {
        super(name, 18);
        this.plotRef = plotRef;
        setClickHandler(this::controllerClick);
    }

    @SneakyThrows
    private void controllerClick(ChestGUI.OptionClickEvent optionClickEvent) {
        if (optionClickEvent.getClicked().getType() == CustomItemIndex.RECYCLE.toMaterial()) {
            plotRef.get().setController(null);
            return;
        }

        if (optionClickEvent.getClicked().getType() == Material.BOOK) {
            var plotBounds = plotRef.get().getBounds();

            var min = BukkitAdapter.asBlockVector(plotBounds.minPoint());
            var max = BukkitAdapter.asBlockVector(plotBounds.maxPoint());
            var world = new BukkitWorld(plotBounds.getWorld());

            var region = new CuboidRegion(min, max);
            var clipboard = new BlockArrayClipboard(region);

            var forwardExtentCopy = new ForwardExtentCopy(
                    world, region, clipboard, region.getMinimumPoint()
            );
            //Configure
            Operations.complete(forwardExtentCopy);

            var file = new File(FileManager.getDataFolder() + "/testSchem.schem");

            try (var writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard);
            }

            return;
        }

        if (optionClickEvent.getClicked().getType() == Material.ENCHANTED_BOOK) {
            var plotBounds = plotRef.get().getBounds();
            var file = new File(FileManager.getDataFolder() + "/testSchem.schem");
            Clipboard clipboard;

            var format = ClipboardFormats.findByFile(file);
            try (var reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            }

            var world = new BukkitWorld(plotBounds.getWorld());
            try (var editSession = WorldEdit.getInstance().newEditSession(world)) {
                var operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BukkitAdapter.asBlockVector(plotBounds.minPoint()))
                        // configure here
                        .build();
                Operations.complete(operation);
            }

            return;
        }

        optionClickEvent.setClose(false);
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
        setOption(10, new ItemStack(Material.BOOK), "Save Plot Schematic");
        setOption(11, new ItemStack(Material.ENCHANTED_BOOK), "Load Plot Schematic");
    }
}
