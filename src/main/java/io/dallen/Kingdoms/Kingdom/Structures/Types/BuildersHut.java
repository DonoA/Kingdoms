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
package io.dallen.Kingdoms.Kingdom.Structures.Types;

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.ChestGUI;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class BuildersHut extends Plot implements Storage{
    
    @Getter
    private BuildingVault Storage;
    @Getter
    private int workerCapacity;
    @Getter
    private static ChestGUI EditPlot;
    
    static{
        EditPlot.setName("Builder's Hut");
        EditPlot.setHandler(new MenuHandler());
    }
    
    public BuildersHut(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
        Storage = new BuildingVault(30, 30 * 100, this);
    }
    
    public boolean hasMaterials(Class type){
        //test if has needed mats
        return true;
    }
    
    @Override
    public boolean interact(PlayerInteractEvent e){
        if(e.getClickedBlock().getType().equals(Material.CHEST)){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(Storage.CanOpen(e.getPlayer())){
                    Storage.SendToPlayer(e.getPlayer());
                    return true;
                }
            }else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                if(e.hasItem() && this.hasSpace()){
                    Storage.addItem(e.getItem());
                    e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean hasSpace(){
        return Storage.getFullSlots() < Storage.getUniqueSize() && Storage.getAmountFull() < Storage.getCapacity();
    }
    
    @Override
    public boolean supplyNPC(NPC npc){
        return true;
    }
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Train Builder", this);
        super.sendEditMenu(p);
    }
    
    public static class MenuHandler implements ChestGUI.OptionClickEventHandler{
        
        @Override
        public void onOptionClick(ChestGUI.OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("Builder's Hut")){
                BuildersHut hut = (BuildersHut) e.getMenuData();
                if(e.getName().equalsIgnoreCase("Train Builder")){
                    Main.getNPCs().spawnBuilder("Dallen", hut.getCenter());
                }
            }
        }
    }
}
