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
package io.dallen.Kingdoms.RPG.Contract.Types;

import io.dallen.Kingdoms.Handlers.ContractHandler;
import io.dallen.Kingdoms.Kingdom.Structures.Plot;
import io.dallen.Kingdoms.Overrides.KingdomMaterial;
import io.dallen.Kingdoms.RPG.Contract.Contract;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ItemUtil;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class DemolishContract implements Contract, ChestGUI.OptionClickEventHandler {
    
    @Getter
    private int id;
    
    @Getter
    private ContractTarget contractTarget;
    
    @Getter
    private Player contractor;
    
    @Getter
    private Object contractee;
    
    @Getter
    private RewardType rewardType;
    
    @Getter
    private Object reward;
    
    @Getter
    private Plot plot;
    
    @Getter
    private ItemStack contractItem;
    
    @Getter @Setter
    private boolean workerFinished;
    
    @Getter @Setter
    private boolean contractorFinished;
    
    @Getter
    private static HashMap<String, DemolishContract> openInputs = new HashMap<String, DemolishContract>();
    
    public DemolishContract(Player contractor, ChestGUI.OptionClickEvent e){
        this.contractor = contractor;
        Plot p = Plot.inPlot(e.getPlayer().getLocation());
        if(p != null){
            this.plot = p;
            this.contractItem = e.getPlayer().getItemInHand();
            this.id = ContractHandler.geCurrentID();
            ContractHandler.getAllContracts().put(id, this);
            selectReward(e.getPlayer());
        }else{
            e.getPlayer().sendMessage("You must be in a plot to create this type of contract");
        }
    }
    
    @Override
    public boolean isFinished(){
        return false;
    }
    
    public void selectReward(Player p){
        new ChestGUI("Select Reward Type", InventoryType.HOPPER, this){{
            setOption(2, KingdomMaterial.DEFAULT.getItemStack(), "Gold");
            setOption(4, KingdomMaterial.DEFAULT.getItemStack(), "Item");
        }}.sendMenu(p);
    }
    
    @Override
    public void onOptionClick(ChestGUI.OptionClickEvent e){
        if(e.getMenuName().equals("Select Reward Type")){
            if(e.getName().equals("Gold")){
                e.getPlayer().sendMessage("Enter amount of gold in chat");
                openInputs.put(e.getPlayer().getName(), this);
            }else if(e.getName().equals("Item")){
                Inventory itm = Bukkit.createInventory(e.getPlayer(), 9*2, "Contract Reward");
                itm.setItem(9*2 + 8, KingdomMaterial.TRADE_CONFIRM.getItemStack());
                openInputs.put(e.getPlayer().getName(), this);
            }
        }
    }
    
    @Override
    public void interact(PlayerInteractEvent e){
        
    }
    
    public static class DemolishHandler implements Listener{
        
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClose(InventoryCloseEvent event){
            if(openInputs.containsKey(event.getPlayer().getName())){
                DemolishContract demolishContract = openInputs.get(event.getPlayer().getName());
                demolishContract.rewardType = RewardType.ITEM;
                demolishContract.reward = event.getInventory().getContents();
                event.getPlayer().setItemInHand(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_FILLED.getItemStack(), "Demolish Contract"));
                openInputs.remove(event.getPlayer().getName());
            }
        }
        
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onChat(AsyncPlayerChatEvent ev){
            if(openInputs.containsKey(ev.getPlayer().getName())){
                ev.setCancelled(true);
                Integer amount = -1;
                boolean failed = false;
                try{
                    amount = Integer.parseInt(ev.getMessage());
                } catch (NumberFormatException ex){
                    ev.getPlayer().sendMessage("That is not a valid gold amount, exiting contract setup");
                    openInputs.remove(ev.getPlayer().getName());
                    failed = true;
                }
                if(!failed){
                    DemolishContract demolishContract = openInputs.get(ev.getPlayer().getName());
                    demolishContract.rewardType = RewardType.GOLD;
                    demolishContract.reward = amount;
                    ev.getPlayer().setItemInHand(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_FILLED.getItemStack(), "Demolish Contract"));
                    openInputs.remove(ev.getPlayer().getName());
                }
            }
        }
    }
}
