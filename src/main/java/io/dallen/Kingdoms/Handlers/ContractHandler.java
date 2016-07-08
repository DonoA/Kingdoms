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

import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Overrides.KingdomMaterial;
import io.dallen.Kingdoms.RPG.Contract.Contract;
import io.dallen.Kingdoms.RPG.Contract.Contract.RewardType;
import io.dallen.Kingdoms.RPG.PlayerData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import io.dallen.Kingdoms.Util.ItemUtil;
import io.dallen.Kingdoms.Util.LogUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
public class ContractHandler implements Listener, OptionClickEventHandler{
    
    private static int currentID = -1;
    
    private static HashMap<String, Long> cooldown = new HashMap<String, Long>();
    
    @Getter
    private static HashMap<Integer, Contract> allContracts = new HashMap<Integer, Contract>();
    
    @Getter
    private static HashMap<String, Contract> openInputs = new HashMap<String, Contract>();
    
    @Getter
    private static ContractHandler inst;
    
    public ContractHandler(){
        inst = this;
    }
    
    private ChestGUI createContract = new ChestGUI("",9*1,this){{
        setOption(9*0 + 0, KingdomMaterial.DEFAULT.getItemStack(), "Build");
        setOption(9*0 + 1, KingdomMaterial.DEFAULT.getItemStack(), "Demolish");
        setOption(9*0 + 2, KingdomMaterial.DEFAULT.getItemStack(), "Gather");
        setOption(9*0 + 3, KingdomMaterial.DEFAULT.getItemStack(), "Guard");
        setOption(9*0 + 4, KingdomMaterial.DEFAULT.getItemStack(), "Patrol");
        setOption(9*0 + 5, KingdomMaterial.DEFAULT.getItemStack(), "Transport");
    }};
    
    public static int geCurrentID(){
        currentID++;
        return currentID;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(((!cooldown.containsKey(e.getPlayer().getName())) || 
                (cooldown.containsKey(e.getPlayer().getName()) && cooldown.get(e.getPlayer().getName()) < System.currentTimeMillis() - 100)) && 
                e.hasItem() && 
                (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))){
            cooldown.put(e.getPlayer().getName(), System.currentTimeMillis());
            if(KingdomMaterial.CONTRACT_EMPTY.equals(e.getItem())){
                createContract.sendMenu(e.getPlayer());
            }else if(KingdomMaterial.CONTRACT_UNFINISHED.equals(e.getItem())){
                try {
                    LogUtil.printDebug("Unfinished Contract Called");
                    if(allContracts.containsKey(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)))){
                        Contract c = allContracts.get(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)));
                        c.interact(e, false);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ContractHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else if(KingdomMaterial.CONTRACT_FILLED.equals(e.getItem())){
                try {
                    LogUtil.printDebug("finished Contract Called");
                    if(allContracts.containsKey(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)))){
                        Contract c = allContracts.get(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)));
                        if(c.getContractee() != null && c.isFinished()){
                            e.getPlayer().sendMessage("Maked Contract as completed!");
                            if(c.getRewardType().equals(RewardType.ITEM)){
                                LogUtil.printDebug("Adding items");
                                for(ItemStack is : (ItemStack[]) c.getReward()){
                                    if(is != null){
                                        e.getPlayer().getInventory().addItem(is);
                                    }
                                }
                            }else if(c.getRewardType().equals(RewardType.GOLD)){
                                PlayerData.getData(e.getPlayer()).addGold((int) c.getReward());
                            }else if(c.getRewardType().equals(RewardType.PLOT)){

                            }
                            allContracts.remove(c.getID());
                            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                        }else{
                            c.interact(e, true);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ContractHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void onOptionClick(ChestGUI.OptionClickEvent e){
        if(e.getMenuName().equals(createContract.getName())){
            try {
                Class<?> contract = Class.forName("io.dallen.Kingdoms.RPG.Contract.Types."+e.getName().replace(" ", "").replace("'", "") + "Contract");
                contract.getConstructor(Player.class, ChestGUI.OptionClickEvent.class).newInstance(e.getPlayer(), e);
            } catch (ClassNotFoundException | IllegalArgumentException | SecurityException | NoSuchMethodException | IllegalAccessException 
                    | InvocationTargetException | InstantiationException ex) {
                ex.printStackTrace();
                e.getPlayer().sendMessage("Contract name not found!");
            }
        }else if(e.getMenuName().equals("Select Reward Type")){
            if(e.getName().equals("Gold")){
                e.getPlayer().sendMessage("Enter amount of gold in chat");
                openInputs.put(e.getPlayer().getName(), (Contract) e.getMenuData());
            }else if(e.getName().equals("Item")){
                final Inventory itm = Bukkit.createInventory(e.getPlayer(), 9*2, "Contract Reward");
                final ChestGUI.OptionClickEvent ev = e;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
                    @Override
                    public void run(){
                        ev.getPlayer().openInventory(itm);
                        openInputs.put(ev.getPlayer().getName(), (Contract) ev.getMenuData());
                    }
                }, 2);
            }
        }
    }
    
    public static void setReward(final Contract c, Player p){
        new ChestGUI("Select Reward Type", InventoryType.HOPPER, ContractHandler.inst){{
            setOption(2, KingdomMaterial.DEFAULT.getItemStack(), "Gold");
            setOption(4, KingdomMaterial.DEFAULT.getItemStack(), "Item");
            setMenuData(c);
        }}.sendMenu(p);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event){
        if(openInputs.containsKey(event.getPlayer().getName())){
            Contract contract = openInputs.get(event.getPlayer().getName());
            contract.setRewardType(RewardType.ITEM);
            contract.setReward(event.getInventory().getContents());
            LogUtil.printDebug(Arrays.toString(event.getInventory().getContents()));
            contract.setContractItem(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_FILLED.getItemStack(), 
                    contract.getClass().getSimpleName(), String.valueOf(contract.getID())));
            event.getPlayer().setItemInHand(contract.getContractItem());
            openInputs.remove(event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        if(openInputs.containsKey(event.getPlayer().getName())){
            event.setCancelled(true);
            Integer amount = -1;
            boolean failed = false;
            try{
                amount = Integer.parseInt(event.getMessage());
            } catch (NumberFormatException ex){
                event.getPlayer().sendMessage("That is not a valid gold amount, exiting contract setup");
                openInputs.remove(event.getPlayer().getName());
                failed = true;
            }
            if(!failed){
                Contract contract = openInputs.get(event.getPlayer().getName());
                contract.setRewardType(RewardType.GOLD);
                contract.setReward(amount);
                contract.setContractItem(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_FILLED.getItemStack(), 
                        contract.getClass().getSimpleName(), String.valueOf(contract.getID())));
                event.getPlayer().setItemInHand(contract.getContractItem());
                openInputs.remove(event.getPlayer().getName());
            }
        }
    }
}