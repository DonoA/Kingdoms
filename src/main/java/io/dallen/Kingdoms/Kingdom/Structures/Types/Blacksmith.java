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

import io.dallen.Kingdoms.Handlers.BuildingHandler;
import io.dallen.Kingdoms.Kingdom.Structures.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.Annotations.SaveData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to manufacture weapons and armor
 * 
 * @author donoa_000
 */
public class Blacksmith extends Plot implements Storage{
    
    @Getter @Setter @SaveData
    private BuildingVault Storage;

    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;
    @Getter
    private ChestGUI ForgeMenu;
    
    public Blacksmith(Plot p) {
        super(p);
        Storage = new BuildingVault(18, 18 * 100, this);
        EditPlot = new ChestGUI("Blacksmith", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
        
        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }};
        ForgeMenu = new ChestGUI("Forge", 9*6, new MenuHandler()){{
            String[] sets = new String[] {"LEATHER", "CHAINMAIL", "IRON", "GOLD", "DIAMOND"};
            String[] types = new String[] {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
            int dwnPos = 0;
            for(String set : sets){
                int accPos = 0;
                for(String type : types){
                    setOption(9*dwnPos + accPos, new ItemStack(Material.valueOf(set + "_" + type)), set + " " + type);
                    accPos+=2;
                }
                dwnPos++;
            }
        }};
    }
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.sendMenu(p);
    }
    
    public class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equals(EditPlot.getName())){
                BuildingHandler.chestBuildOptions(e, BuildMenu, Blacksmith.this);
            }else if(e.getMenuName().equals(BuildMenu.getName())){
                BuildingHandler.getBuildChestHandler().onOptionClick(e);
            }else if(e.getMenuName().equals(ForgeMenu.getName())){
                ItemStack craft = new ItemStack(Material.valueOf(e.getName().replace(" ", "_")));
                Blacksmith.this.getStorage().addItem(craft);
                e.getPlayer().sendMessage("Forged " + e.getName());
            }
        }
    }

    @Override
    public boolean interact(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.ANVIL)){
            e.setCancelled(true);
            final PlayerInteractEvent ev = e;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
                @Override
                public void run(){
                    ev.getPlayer().closeInventory();
                    ForgeMenu.sendMenu(ev.getPlayer());
                }
            }, 1);
        }else if(e.getClickedBlock().getType().equals(Material.CHEST)){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(Storage.CanOpen(e.getPlayer())){
                    Storage.SendToPlayer(e.getPlayer());
                    return true;
                }
            }else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                if(e.hasItem() && this.hasSpace()){
                    if(Storage.addItem(e.getItem())){
                        e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasSpace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean supplyNPC(NPC npc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
