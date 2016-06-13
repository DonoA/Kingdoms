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
import io.dallen.Kingdoms.Util.PermissionManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Donovan Allen
 */
public class PlotProtectionHandler implements Listener{
    
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission(PermissionManager.getBuildPermission())) {
            event.setCancelled(false);
        } else {
            Plot p = Plot.inPlot(event.getBlockPlaced().getLocation());
            if(p != null && p.getMunicipal()!=null){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission(PermissionManager.getBuildPermission())) {
            event.setCancelled(false);
        } else {
            Plot p = Plot.inPlot(event.getBlock().getLocation());
            if(p != null && p.getMunicipal()!=null){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getRemover() instanceof Player && ((Player)event.getRemover()).hasPermission(PermissionManager.getBuildPermission())) {
            event.setCancelled(false);
        } else {
            Plot p = Plot.inPlot(event.getEntity().getLocation());
            if(p != null && p.getMunicipal()!=null){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        boolean isFlower = false;
        boolean restricted = false;
        Player player = (Player) event.getPlayer();
        Material halfSlab = Material.getMaterial(44);
        final Block block = event.getClickedBlock();
        final BlockFace blockFace = event.getBlockFace();
        final Block relativeBlock = block.getRelative(blockFace);
        final Material fireMaterial = Material.FIRE;
        if (player.hasPermission(PermissionManager.getBuildPermission())) {
            event.setCancelled(false);
        } else if (event.hasItem() && event.hasBlock()) {
            if (event.getItem().getType().equals(Material.INK_SACK)) {
                if (event.getItem().getData().getData() == 15
                        && (event.getClickedBlock().getType() == Material.GRASS
                        || event.getClickedBlock().getType() == Material.SAPLING
                        || event.getClickedBlock().getType() == Material.CROPS
                        || event.getClickedBlock().getType() == Material.BROWN_MUSHROOM
                        || event.getClickedBlock().getType() == Material.RED_MUSHROOM
                        || event.getClickedBlock().getType() == Material.PUMPKIN_STEM
                        || event.getClickedBlock().getType() == Material.MELON_STEM
                        || event.getClickedBlock().getType() == Material.POTATO
                        || event.getClickedBlock().getType() == Material.CARROT
                        || event.getClickedBlock().getType() == Material.COCOA
                        || event.getClickedBlock().getType() == Material.LONG_GRASS)) {
                    restricted = true;
                } else if (event.getItem().getData().getData() == 3) {
                    restricted = true;
                }
            }
            if (event.getClickedBlock().getType().equals(Material.FLOWER_POT)) {
                //cancelling this currently does nothing.
                if (event.getItem().getType() == Material.RED_ROSE
                        || event.getItem().getType() == Material.YELLOW_FLOWER
                        || event.getItem().getType() == Material.SAPLING
                        || event.getItem().getType() == Material.RED_MUSHROOM
                        || event.getItem().getType() == Material.BROWN_MUSHROOM
                        || event.getItem().getType() == Material.CACTUS
                        || event.getItem().getType() == Material.LONG_GRASS
                        || event.getItem().getType() == Material.DEAD_BUSH) {
                    restricted = true;
                    player.sendBlockChange(event.getClickedBlock().getLocation(), Material.STONE, (byte)0);
                }
            }
            if(event.getItem().getType().getId() == halfSlab.getId()){
                restricted = true;
            }
            if(event.getItem().getType().equals(Material.WATER_LILY)){
                event.setCancelled(true);
                restricted=true;
            }
        }
        if (event.hasBlock() && relativeBlock.getType() == fireMaterial) {
            player.sendBlockChange(relativeBlock.getLocation(), fireMaterial, (byte)0);
            event.setCancelled(true);
            restricted = true;
        }
        if (restricted) {
            event.setCancelled(true);
        } else {
        }
    }
    
}
