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
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonStructure;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Util.Annotations.SaveData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to store its subjects’ wealth safely
 * 
 * @author donoa_000
 */
public class Bank extends Plot implements Storage{
    @Getter
    private int securityLevel;
    @Getter
    private int vaultNumber;
    @Getter @Setter @SaveData
    private HashMap<Player, BuildingVault> vaults = new HashMap<Player, BuildingVault>();
    @Getter
    private boolean MunicipalBank;
    @Getter
    private boolean KingdomBank;
    @Getter
    private BuildingVault Storage;
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;
    
    public Bank(Plot p) {
        super(p);
        EditPlot = new ChestGUI("Bank", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
        
        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Light Builder's Hut");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Dark Builder's Hut");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }};
    }
    
    @Override
    public boolean interact(PlayerInteractEvent e){
        if(e.getClickedBlock().getType().equals(Material.THIN_GLASS)){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                PlayerData pd = PlayerData.getData(e.getPlayer());
                pd.getVault().SendToPlayer(e.getPlayer());
            }
        }
        return false;
    }
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.sendMenu(p);
    }
    
    @Override
    public boolean hasSpace(){
        return Storage.getFullSlots() < Storage.getUniqueSize() && Storage.getAmountFull() < Storage.getCapacity();
    }
    
    @Override
    public boolean supplyNPC(NPC npc){
        
        return true;
    }
    
    public class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equals(EditPlot.getName())){
                BuildingHandler.chestBuildOptions(e, BuildMenu, Bank.this);
            }else if(e.getMenuName().equals(BuildMenu.getName())){
                if(e.getName().equalsIgnoreCase("Other")){
                    BuildingHandler.getBuildChestHandler().onOptionClick(e);
                }else{
                    e.getPlayer().sendMessage("Default option called");
                }
            }
        }
    }
    
    

}
