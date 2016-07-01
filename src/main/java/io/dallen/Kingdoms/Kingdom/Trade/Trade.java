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
package io.dallen.Kingdoms.Kingdom.Trade;

import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Overrides.KingdomMaterial;
import io.dallen.Kingdoms.Util.ChestGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Trade {
    
    private static HashMap<String, Trade> openTrades = new HashMap<String, Trade>();
    
    private static HashMap<String, Long> cooldown = new HashMap<String, Long>();
    
    private ArrayList<ItemStack> Items1 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Items2 = new ArrayList<ItemStack>();
    
    private TradeChestGUI gui;
    
    public Trade(ItemStack[] Items1, ItemStack[] Items2){
        this.Items1 = new ArrayList<ItemStack>(Arrays.asList(Items1));
        this.Items2 = new ArrayList<ItemStack>(Arrays.asList(Items2));
    }
    
    public boolean completeTrade(){
        throw new UnsupportedOperationException();
//        return true;
    }
    
    public static class TradeChestGUI {
        
        private boolean confirm1 = false;
        private boolean confirm2 = false;
        
        private ArrayList<Player> viewers = new ArrayList<Player>();
        
        private String title;
        
        private Trade trade;
        
        public TradeChestGUI(String title, Trade trade){
            this.title = title;
            this.trade = trade;
        }
                
        public void sendToPlayer(Player...p){
            Inventory view = Bukkit.createInventory(null, 9*3, title);
            for(int i = 0; i <= 3; i++){
                view.setItem(9*i + 4, KingdomMaterial.TRADE_DIVIDER.getItemStack());
            }
            view.setItem(9*3 + 3, KingdomMaterial.TRADE_CONFIRM.getItemStack());
            view.setItem(9*3 + 5, KingdomMaterial.TRADE_CONFIRM.getItemStack());
            for(Player plr : p){
                plr.openInventory(view);
                viewers.add(plr);
                openTrades.put(plr.getName(), trade);
            }
        }
        
        public void updateForPlayers(){
            
        }
        
        public static class TradeChestHandler implements Listener {
            
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onInventoryClick(InventoryClickEvent event){
                if((!cooldown.containsKey(event.getWhoClicked().getName())) || 
                    (cooldown.containsKey(event.getWhoClicked().getName()) && cooldown.get(event.getWhoClicked().getName()) < System.currentTimeMillis() - 100)){
                    cooldown.put(event.getWhoClicked().getName(), System.currentTimeMillis());
                    if(!openTrades.containsKey(event.getWhoClicked().getName()))
                        return;
                    Trade trade = openTrades.get(event.getWhoClicked().getName());
                    if(event.getInventory().getTitle().equals(trade.gui.title)){
                        if(event.getRawSlot() % 9 < 4){
                            
                        }
                    }
                }
            }
            
        }
    }
}
