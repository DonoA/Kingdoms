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

import io.dallen.Kingdoms.Util.PermissionManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Donovan Allen
 */
public class WaterHandler implements Listener{
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getItem()!=null && 
                !e.getPlayer().hasPermission(PermissionManager.getBuildPermission())){
            if(e.getItem().getType().equals(Material.WATER_BUCKET)){
                e.setCancelled(true);
                e.getItem().setType(Material.BUCKET);
                if(e.getClickedBlock().getType().equals(Material.GRASS) || e.getClickedBlock().getType().equals(Material.SOIL)){
                    e.getClickedBlock().setType(Material.DIRT);
                }
//                e.getClickedBlock().getRelative(e.getBlockFace()).setType(Material.WATER); this would be if we wanted it to "flow"
            }else if(e.getItem().getType().equals(Material.BUCKET)){
            }
        }
    }
    
}
