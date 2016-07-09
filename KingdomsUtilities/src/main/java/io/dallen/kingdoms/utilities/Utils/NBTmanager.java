/**
 *
 * This file is unlicensed so as not to conflict with the existing WorldEdit copyright held by sk89q <http://www.sk89q.com> and the
 * WorldEdit team and contributors who wrote most of the code found within this file
 *
 */
package io.dallen.kingdoms.utilities.Utils;

import io.dallen.kingdoms.utilities.Blueprint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import net.citizensnpcs.api.jnbt.ByteArrayTag;
import net.citizensnpcs.api.jnbt.CompoundTag;
import net.citizensnpcs.api.jnbt.IntTag;
import net.citizensnpcs.api.jnbt.ListTag;
import net.citizensnpcs.api.jnbt.NBTInputStream;
import net.citizensnpcs.api.jnbt.ShortTag;
import net.citizensnpcs.api.jnbt.StringTag;
import net.citizensnpcs.api.jnbt.Tag;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * Based on code from sk98q's WorldEdit
 *
 * @author donoa_000
 */
public class NBTmanager {

    //Width = X direction
    //Length = Z direction
    //Height = Y direction
    //Starts at smallest x y and z
    //Starts in X direction, then Z, then Y
    public static Blueprint loadData(File f) throws IOException, DataFormatException {
        FileInputStream stream = new FileInputStream(f);
        NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));

        // Schematic tag
        Tag rootTag = nbtStream.readTag();
        nbtStream.close();
        if (!rootTag.getName().equals("Schematic")) {
            throw new DataFormatException("Tag \"Schematic\" does not exist or is not first");
        }

        CompoundTag schematicTag = (CompoundTag) rootTag;

        // Check
        Map<String, Tag> schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new DataFormatException("Schematic file is missing a \"Blocks\" tag");
        }

        // Get information
        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

        // Check type of Schematic
        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
        if (!materials.equals("Alpha")) {
            throw new DataFormatException("Schematic file is not an Alpha schematic");
        }

        // Get blocks
        byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
        byte[] addId = new byte[0];
        short[] blocks = new short[blockId.length]; // Have to later combine IDs

        // We support 4096 block IDs using the same method as vanilla Minecraft, where
        // the highest 4 bits are stored in a separate byte array.
        if (schematic.containsKey("AddBlocks")) {
            addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
        }

        // Combine the AddBlocks data with the first 8-bit block ID
        for (int index = 0; index < blockId.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blocks[index] = (short) (blockId[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                }
            }
        }

        // Need to pull out tile entities
        List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();
        Map<BlockVector, Map<String, Tag>> tileEntitiesMap = new HashMap<BlockVector, Map<String, Tag>>();

        for (Tag tag : tileEntities) {
            if (!(tag instanceof CompoundTag)) {
                continue;
            }
            CompoundTag t = (CompoundTag) tag;

            int x = 0;
            int y = 0;
            int z = 0;

            Map<String, Tag> values = new HashMap<String, Tag>();

            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                if (entry.getKey().equals("x")) {
                    if (entry.getValue() instanceof IntTag) {
                        x = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("y")) {
                    if (entry.getValue() instanceof IntTag) {
                        y = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("z")) {
                    if (entry.getValue() instanceof IntTag) {
                        z = ((IntTag) entry.getValue()).getValue();
                    }
                }

                values.put(entry.getKey(), entry.getValue());
            }

            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, values);
        }
        return new Blueprint(length, width, height, blocks, blockData);
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws DataException if the tag does not exist or the tag is not of the expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws DataFormatException {
        if (!items.containsKey(key)) {
            throw new DataFormatException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new DataFormatException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}
