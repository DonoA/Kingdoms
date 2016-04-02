/*
 * Copyright 2016 Donovan Allen
 *
 * This file is part of Kingdoms for the Morphics Network.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.Kingdoms.Kingdom.Vaults;

import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Wrappers.MaterialWrapper;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class BuildingVault implements Vault{
    @Getter
    private Structure owner;
    
    @Getter
    private double size;
    
    @Getter
    private int fullSlots;
    
    @Getter
    private MaterialWrapper[] contents;
    
    public BuildingVault(int size) {
        this.size = size;
        contents = new MaterialWrapper[size];
    }
    
    @Override
    public boolean SendToPlayer(Player p){
        if(InvenHandler.openVaults.containsKey(p.getName())){
            return false;
        }else{
            Inventory inv = Bukkit.createInventory(p, (int) Math.ceil(size/9)*9, "Building Inventory");
            for(MaterialWrapper m : contents){
                inv.addItem(m.asBukkitItem());
            }
            InvenHandler.openVaults.put(p.getName(), this);
            p.openInventory(inv);
        }
        return true;
    }
    
    @Override
    public boolean CanOpen(Player p){
        return true;
    }
    
    public void removeItem(int slot){
        contents[slot] = null;
       for(int i = slot; i < fullSlots; i++){
           if(i + 1 >= size){
               contents[i] = null;
           }else{
               contents[i] = contents[i+1];
           }
       }
       fullSlots--;
    }
    
    public void addItem(ItemStack is){
        if(fullSlots < size){
            contents[fullSlots] = new MaterialWrapper(is);
        }
    }
    
    public static class InvenHandler implements Listener{
        
        private static HashMap<String, BuildingVault> openVaults = new HashMap<String, BuildingVault>();
        
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(openVaults.containsKey(e.getWhoClicked().getName())){
                BuildingVault v = openVaults.get(e.getWhoClicked().getName());
                if(e.getCursor() != null){
                    v.addItem(e.getCursor());
                    e.getWhoClicked().closeInventory();
                    v.SendToPlayer((Player) e.getWhoClicked());
                }else{
                    if(v.getContents()[e.getSlot()].getStackSize() > 64){
                        e.getInventory().setItem(e.getSlot(), v.getContents()[e.getSlot()].asBukkitItem());
                    }else{
                        v.removeItem(e.getSlot());
                        e.getWhoClicked().closeInventory();
                        v.SendToPlayer((Player) e.getWhoClicked());
                    }
                }
            }
        }
        
        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e){
            if(openVaults.containsKey(e.getPlayer().getName())){
                openVaults.remove(e.getPlayer().getName());
            }
        }
    }
}
