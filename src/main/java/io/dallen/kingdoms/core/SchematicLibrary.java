/*
 * Copyright 2017 donoa_000.
 * 
 * This file is part of Kingdoms for the Morphics Network.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.kingdoms.core;

import io.dallen.utils.Storage.Blueprint;
import io.dallen.utils.Storage.Blueprint.BlueBlock;
import io.dallen.utils.Storage.DatabaseInterfaces.JsonInterface;
import io.dallen.utils.jnbt.ByteArrayTag;
import io.dallen.utils.jnbt.CompoundTag;
import io.dallen.utils.jnbt.NBTInputStream;
import io.dallen.utils.jnbt.NBTOutputStream;
import static io.dallen.utils.jnbt.NBTUtils.getChildTag;
import io.dallen.utils.jnbt.NamedTag;
import io.dallen.utils.jnbt.ShortTag;
import io.dallen.utils.jnbt.StringTag;
import java.io.File;
import java.util.HashMap;
import io.dallen.utils.jnbt.Tag;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author donoa_000
 */
public class SchematicLibrary {
    
    private static File database = new File(Kingdoms.getPlugin().getDataFolder() + JsonInterface.getFileSep() + "SchematicLibrary");
    
    public static boolean exists(String name){
        return new File(database + JsonInterface.getFileSep() + name).exists();
    }

    public static void save(Blueprint schem, String name, String type) throws IOException{
        HashMap<String, Tag> schematic = new HashMap<String, Tag>();
        schematic.put("Width", new ShortTag((short) schem.getWid()));
        schematic.put("Length", new ShortTag((short) schem.getLen()));
        schematic.put("Height", new ShortTag((short) schem.getHigh()));
        schematic.put("Type", new StringTag(type));
        schematic.put("Materials", new StringTag("Alpha"));
        
        byte[] blocks = new byte[schem.getWid()*schem.getLen()*schem.getHigh()];
        byte[] addBlocks = null;
        byte[] blockData = new byte[schem.getWid()*schem.getLen()*schem.getHigh()];
        ArrayList<Tag> tileEntities = new ArrayList<Tag>();
        for (int x = 0; x < schem.getWid(); ++x) {
            for (int y = 0; y < schem.getHigh(); ++y) {
                for (int z = 0; z < schem.getLen(); ++z) {
                    int index = y * schem.getWid() * schem.getLen() + z * schem.getWid() + x;
                    BlueBlock block = schem.getBlocks()[x][y][z];

                    blocks[index] = (byte) block.getBlock().getId();
                    blockData[index] = (byte) block.getData();
                }
            }
        }

        schematic.put("Blocks", new ByteArrayTag(blocks));
        schematic.put("Data", new ByteArrayTag(blockData));
        
        // Build and output
        CompoundTag schematicTag = new CompoundTag(schematic);
        NBTOutputStream stream = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(new File(database + JsonInterface.getFileSep() + name))));
        stream.writeNamedTag("Schematic", schematicTag);
        stream.close();
    }
    
    public static Schematic load(String name) throws IOException, DataFormatException{
        FileInputStream stream = new FileInputStream(new File(database + JsonInterface.getFileSep() + name));
        NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));

        // Schematic tag
        NamedTag rootTag = nbtStream.readNamedTag();
        nbtStream.close();
        if (!rootTag.getName().equals("Schematic")) {
            throw new DataFormatException("Tag \"Schematic\" does not exist or is not first");
        }

        CompoundTag schematicTag = (CompoundTag) rootTag.getTag();

        // Check
        Map<String, Tag> schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new DataFormatException("Schematic file is missing a \"Blocks\" tag");
        }

        // Get information
        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
        String type = getChildTag(schematic, "Type", StringTag.class).getValue();


        // Check type of Schematic
        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
        if (!materials.equals("Alpha")) {
            throw new DataFormatException("Schematic file is not an Alpha schematic");
        }

        // Get blocks
        byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
        short[] blocks = new short[blockId.length]; // Have to later combine IDs
        
        return new Schematic(new Blueprint(length, width, height, blocks, blockData), name, type);
        
    }
    
}
