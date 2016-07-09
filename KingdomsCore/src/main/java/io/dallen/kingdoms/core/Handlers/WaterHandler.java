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
package io.dallen.kingdoms.core.Handlers;

import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.utilities.Utils.PermissionManager;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class WaterHandler implements Listener {

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onBlockPhysics(BlockPhysicsEvent e){
//        if(e.getBlock().getType().equals(Material.WATER)){
//            e.setCancelled(true);
//        }
//    }
//    
//    @EventHandler
//    public void onBlockFromTo(BlockFromToEvent e){
//        e.getToBlock().setType(Material.WATER);
//        e.getToBlock().setData((byte) 1);
//        e.setCancelled(true);
//    }
    @EventHandler
    public void onRain(WeatherChangeEvent e) {
        if (KingdomsCore.getPlugin().getConfig().getBoolean("weatherDisabled", true)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() != null && !e.getPlayer().hasPermission(PermissionManager.getBuildPermission())) {
            if (e.getItem().getType().equals(Material.WATER_BUCKET) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                e.setCancelled(true);
                e.getItem().setType(Material.BUCKET);
                if (e.getClickedBlock().getType().equals(Material.GRASS) || e.getClickedBlock().getType().equals(Material.SOIL)) {
                    e.getClickedBlock().setType(Material.DIRT);
                }
                if (!e.getClickedBlock().getRelative(e.getBlockFace()).getType().equals(Material.STATIONARY_WATER)) {
                    e.getClickedBlock().getRelative(e.getBlockFace()).setType(Material.WATER);
                    e.getClickedBlock().getRelative(e.getBlockFace()).setData((byte) 5);
                }
            } else if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && e.getItem().getType().equals(Material.BUCKET)) {
                List<Block> los = e.getPlayer().getLineOfSight((Set<Material>) null, 5);
                for (Block b : los) {
                    if (b.getType() == Material.STATIONARY_WATER) {
                        e.setCancelled(true);
                        if (e.getItem().getAmount() > 1) {
                            e.getItem().setAmount(e.getItem().getAmount() - 1);
                            e.getPlayer().getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
                        } else {
                            e.getItem().setType(Material.WATER_BUCKET);
                        }
                        break;
                    }
                }
            }
        }
    }

}
