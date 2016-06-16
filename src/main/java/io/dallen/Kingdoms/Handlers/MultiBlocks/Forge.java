/*
 * Copyright 2016 Donovan Allen.
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
package io.dallen.Kingdoms.Handlers.MultiBlocks;

import io.dallen.Kingdoms.Kingdom.Structures.Blueprint;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.NBTmanager;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Forge extends MultiBlock{
    
    @Getter
    private static Blueprint basicForm = null;
    
    @Getter
    private Blueprint form;
    
    @Getter
    private ItemStack fuel;

    @Getter
    private ItemStack input;
    
    @Getter
    private ItemStack output;
    
    public Forge(Location cent, Blueprint form) {
        super(cent, form.getWid(), form.getHigh(), form.getLen());
    }
    
    public void interact(PlayerInteractEvent e){
        
    }
    
    public static void loadForm(){
        if(basicForm == null){
            try {
                basicForm = NBTmanager.loadData(new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "multiblocks" + 
                        DBmanager.getFileSep() + "forge" + ".schematic"));
            } catch (IOException ex) {
                Logger.getLogger(Forge.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DataFormatException ex) {
                Logger.getLogger(Forge.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
