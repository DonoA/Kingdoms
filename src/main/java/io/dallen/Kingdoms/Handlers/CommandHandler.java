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

import lombok.Getter;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Donovan Allen
 */
public class CommandHandler implements Listener{
    
    @Getter
    private boolean newPossition = false;
    
    @Getter
    private Location possition = null;
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.hasItem() && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName() && 
                e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Command Stick") && e.hasBlock()){
            possition = e.getClickedBlock().getLocation();
        }
    }
    
    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent e){
        
    }
    
}
