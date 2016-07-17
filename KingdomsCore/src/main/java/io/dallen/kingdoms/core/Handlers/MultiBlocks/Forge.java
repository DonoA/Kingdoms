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
package io.dallen.kingdoms.core.Handlers.MultiBlocks;

import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.utilities.Blueprint;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import io.dallen.kingdoms.utilities.Utils.NBTmanager;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class Forge extends MultiBlock {

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

    public void interact(PlayerInteractEvent e) {
        e.setCancelled(true);
        Inventory frg = Bukkit.createInventory(e.getPlayer(), InventoryType.FURNACE, "Forge");
        frg.setItem(0, input);
        frg.setItem(1, fuel);
        frg.setItem(2, output);
        e.getPlayer().openInventory(frg);
    }

    public static void placeForge(Location l) {
        Location startCorner = new Location(l.getWorld(), l.getX() - basicForm.getWid() / 2 + (basicForm.getWid() % 2 == 0 ? 1 : 0),
                l.getBlockY(), l.getBlockZ() - basicForm.getLen() / 2 + (basicForm.getLen() % 2 == 0 ? 1 : 0));
        for (int y = 0; y < basicForm.getHigh(); y++) {
            for (int z = 0; z < basicForm.getLen(); z++) {
                for (int x = 0; x < basicForm.getWid(); x++) {
                    Location nLoc = startCorner.clone().add(x, y, z);
                    nLoc.getBlock().setType(basicForm.getBlocks()[x][y][z].getBlock(), false);
                    nLoc.getBlock().setData(basicForm.getBlocks()[x][y][z].getData(), false);
                }
            }
        }
    }

    public static void loadForm() {
        if (basicForm == null) {
            try {
                basicForm = NBTmanager.loadData(new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "multiblock"
                        + DBmanager.getFileSep() + "forge.schematic"));
            } catch (IOException ex) {
                Logger.getLogger(Forge.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DataFormatException ex) {
                Logger.getLogger(Forge.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
