package io.dallen.kingdoms.util;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.dallen.kingdoms.savedata.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class WorldEditUtil {

    public static String saveBounds(Bounds bounds, String name) throws WorldEditException, IOException {
        var min = BukkitAdapter.asBlockVector(bounds.minPoint());
        var max = BukkitAdapter.asBlockVector(bounds.maxPoint());
        var world = new BukkitWorld(bounds.getWorld());

        var region = new CuboidRegion(min, max);
        var clipboard = new BlockArrayClipboard(region);

        var forwardExtentCopy = new ForwardExtentCopy(
                world, region, clipboard, region.getMinimumPoint()
        );
        //Configure
        Operations.complete(forwardExtentCopy);

        var randomName = UUID.randomUUID() + "_" + name;
        var file = new File(FileManager.getSchematicFolder() + "/" + randomName + ".schem");
        try (var writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        }

        return randomName;
    }

    public static void loadToBounds(Bounds bounds, String exactName) throws WorldEditException, IOException {
        Clipboard clipboard;

        var file = new File(FileManager.getSchematicFolder() + "/" + exactName + ".schem");
        var format = ClipboardFormats.findByFile(file);
        try (var reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        }

        var world = new BukkitWorld(bounds.getWorld());
        try (var editSession = WorldEdit.getInstance().newEditSession(world)) {
            var operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BukkitAdapter.asBlockVector(bounds.minPoint()))
                    // configure here
                    .build();
            Operations.complete(operation);
        }
    }

}
