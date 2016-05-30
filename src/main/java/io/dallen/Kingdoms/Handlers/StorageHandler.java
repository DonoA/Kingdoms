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
package io.dallen.Kingdoms.Handlers;

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Donovan Allen
 */
public class StorageHandler implements Listener{
    
    private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
    
    private final static ArrayList<InventoryAction> remove = new ArrayList<InventoryAction>(){{
        add(InventoryAction.COLLECT_TO_CURSOR);
        add(InventoryAction.DROP_ALL_SLOT);
        add(InventoryAction.DROP_ONE_SLOT);
        add(InventoryAction.PICKUP_ALL);
        add(InventoryAction.PICKUP_HALF);
        add(InventoryAction.PICKUP_ONE);
        add(InventoryAction.PICKUP_SOME);
    }};
    
    private final static ArrayList<InventoryAction> add = new ArrayList<InventoryAction>(){{
        add(InventoryAction.PLACE_ALL);
        add(InventoryAction.PLACE_ONE);
        add(InventoryAction.PLACE_SOME);
    }};
    
    @Getter
    private static HashMap<Player, Storage> openStorages = new HashMap<Player, Storage>();
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event){//NEED TO HANDLE MOVE_TO_OTHER_INVENTORY //ALSO THE DROP AND PICKUP THING
        if((!cooldown.containsKey((Player) event.getWhoClicked())) || 
            (cooldown.containsKey((Player) event.getWhoClicked()) && 
             cooldown.get((Player) event.getWhoClicked()) < System.currentTimeMillis() - 500)){
            cooldown.put((Player) event.getWhoClicked(), System.currentTimeMillis());
            //NEED TO CHECK SLOTS HERE
            if(event.getInventory().getName().equalsIgnoreCase("Building Inventory")){
                if(remove.contains(event.getAction())){
                    Storage s = openStorages.get((Player) event.getWhoClicked());
                    //REMOVE THEM
                }else if(add.contains(event.getAction())){
                    Storage s = openStorages.get((Player) event.getWhoClicked());
                    //ADD THEM
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e){
        if((!cooldown.containsKey(e.getPlayer())) || 
              (cooldown.containsKey(e.getPlayer()) && cooldown.get(e.getPlayer()) < System.currentTimeMillis() - 500)){
               cooldown.put(e.getPlayer(), System.currentTimeMillis());
            if(e.hasBlock()){
                Plot p = Plot.inPlot(e.getClickedBlock().getLocation());
                if(p != null){
                    if(p instanceof Storage){
                        Storage s = (Storage) p;
                        boolean opened = s.interact(e);
                        e.setCancelled(opened);
                        if(opened)
                            openStorages.put(e.getPlayer(), s);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e){
        if(openStorages.containsKey((Player) e.getPlayer())){
            openStorages.remove((Player) e.getPlayer());
        }
    }
    
}
