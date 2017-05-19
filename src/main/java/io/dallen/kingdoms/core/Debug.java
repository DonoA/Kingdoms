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
package io.dallen.kingdoms.core;

import io.dallen.utils.BukkitUtils.ItemUtil;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Debug {
    
    private HashMap<Player, SchemSelect> openSchems = new HashMap<>(); 
    
    public class Commands implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
            if(cmd.getName().equalsIgnoreCase("schem") && sender instanceof Player){
                ItemStack wand = ItemUtil.setItemNameAndLore(Material.WOOD_AXE, "Schem Wand", "Left - p1", "Right - p2", "Drop - save");
                ((Player) sender).getInventory().addItem(wand);
                openSchems.put(((Player) sender), new SchemSelect());
            }
            return true;
        }
    }

    public class Events implements Listener {
        
        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent e){
            if(openSchems.containsKey(e.getPlayer()) && e.hasItem() && e.getItem().hasItemMeta() && 
                    e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Schem Wand")){
                if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                    openSchems.get(e.getPlayer()).p2 = e.getClickedBlock().getLocation();
                }else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                    openSchems.get(e.getPlayer()).p1 = e.getClickedBlock().getLocation();
                }
            }
        }
        
        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent e){
            if(openSchems.containsKey(e.getPlayer()) && e.getItemDrop().getItemStack().hasItemMeta() && 
                    e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("Schem Wand")){
                e.getItemDrop().remove();
                
            }
        }
        
    }
    
    public static class SchemSelect {
        public Location p1;
        public Location p2;
    }
    
}
