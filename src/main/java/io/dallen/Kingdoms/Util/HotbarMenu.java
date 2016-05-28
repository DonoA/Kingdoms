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
package io.dallen.Kingdoms.Util;

import io.dallen.Kingdoms.Main;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class HotbarMenu implements Listener{
    
    private static HashMap<String, HotbarInstance> openMenus = new HashMap<String, HotbarInstance>();
    
    private static HashMap<String, Long> cooldown = new HashMap<String, Long>();
    @Setter
    private String name;
    @Setter
    private OptionClickEventHandler handler;
    @Setter
    private Object menuData;
   
    private String[] optionNames;
    private ItemStack[] optionIcons;
    private Object[] optionData;
    
    public HotbarMenu(String name, OptionClickEventHandler handler){
        this.name = name;
        this.handler = handler;
        optionNames = new String[9];
        optionIcons = new ItemStack[9];
        optionData = new Object[9];
        optionNames[8] = "Cancel";
        optionIcons[8] = ItemUtil.setItemNameAndLore(Material.BARRIER, "Cancel");
        Main.getPlugin().getServer().getPluginManager().registerEvents(this, Main.getPlugin());
    }
    
    public HotbarMenu setOption(int pos, ItemStack icon, String name, String... info){
        optionNames[pos] = name;
        optionIcons[pos] = ItemUtil.setItemNameAndLore(icon, name, info);
        return this;
    }
    
    public HotbarMenu setOption(int pos, ItemStack icon, String name, Object data, String... info){
        optionNames[pos] = name;
        optionIcons[pos] = ItemUtil.setItemNameAndLore(icon, name, info);
        optionData[pos] = data;
        return this;
    }
    
    public HotbarMenu clearOptions(){
        optionNames = new String[9];
        optionIcons = new ItemStack[9];
        optionData = new Object[9];
        optionNames[8] = "Cancel";
        optionIcons[8] = ItemUtil.setItemNameAndLore(Material.BARRIER, "Cancel");
        return this;
    }
    
    public void sendMenu(Player player){
        ItemStack[] saveBar = new ItemStack[9];
        for(int i = 36; i <= 44; i++){
            saveBar[i-36] = player.getInventory().getItem(i);
            player.getInventory().setItem(i, optionIcons[i-36]);
        }
        final HotbarInstance menu = new HotbarInstance(this);
        menu.setPlayerOldHotbar(saveBar);
        openMenus.put(player.getName(), menu);
    }
    
    public static void closeMenu(Player player){
        openMenus.remove(player.getName());
    }
   
    public void destroy(){
        HandlerList.unregisterAll(this);
        handler = null;
        optionNames = null;
        optionIcons = null;
        optionData = null;
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e){
        if(openMenus.containsKey(e.getPlayer().getName())){
            e.setCancelled(true);
            e.getPlayer().sendMessage("You cannot open your inventory in this menu!");
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if((!cooldown.containsKey(event.getPlayer().getName())) || 
            (cooldown.containsKey(event.getPlayer().getName()) && cooldown.get(event.getPlayer().getName()) < System.currentTimeMillis() - 500)){
            cooldown.put(event.getPlayer().getName(), System.currentTimeMillis());
            if(!openMenus.containsKey(event.getPlayer().getName()))
                return;
            if(event.getAction().equals(Action.RIGHT_CLICK_AIR) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                HotbarInstance menu = openMenus.get(event.getPlayer().getName());
                event.setCancelled(true);
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                if(slot >= 0 && slot < 9 && menu.optionNames[slot] != null){
                    if(menu.optionNames[slot].equalsIgnoreCase("Cancel")){
                        HotbarMenu.closeMenu(event.getPlayer());
                        return;
                    }
                    OptionClickEvent e = new OptionClickEvent(event.getPlayer(), slot, menu.optionData[slot], menu.optionNames[slot], menu.name, menu.menuData);
                    menu.handler.onOptionClick(e);
                    if(e.isClose()){
                        final Player p = (Player) event.getPlayer();
                        final OptionClickEvent ev = e;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
                            @Override
                            public void run(){
                                if(ev.getNext() != null){
                                    ev.getNext().sendMenu(p);
                                }else{
                                    HotbarMenu.closeMenu(p);
                                }
                            }
                        }, 1);
                    }
                    if (e.isDestroy()){
                        destroy();
                    }
                }
                HotbarMenu.closeMenu(event.getPlayer());
            }
        }
    }
    
    public static class HotbarInstance{
        private String name;
        private OptionClickEventHandler handler;
        private String[] optionNames;
        private ItemStack[] optionIcons;
        private Object[] optionData;
        private Object menuData;
        
        @Getter @Setter
        private ItemStack[] playerOldHotbar;
        
        public HotbarInstance(HotbarMenu menu){
            this.name = menu.name;
            this.handler = menu.handler;
            this.optionData = menu.optionData;
            this.optionNames = menu.optionNames;
            this.optionIcons = menu.optionIcons;
            this.menuData = menu.menuData;
        }
    }
    
    public interface OptionClickEventHandler{
        public void onOptionClick(OptionClickEvent event);       
    }
    
    public class OptionClickEvent{
        @Getter
        private Player player;
        
        @Getter
        private int position;
        
        @Getter
        private String name;
        
        @Getter
        private String menuName;
        
        @Getter @Setter
        private boolean close;
        
        @Getter @Setter
        private HotbarMenu next;
        
        @Getter @Setter
        private boolean destroy;
        
        @Getter
        private Object data;
        
        @Getter
        private Object menuData;
       
        public OptionClickEvent(Player player, int position, Object data, String name, String menuName, Object menuData){
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = false;
            this.menuName = menuName;
            this.next = null;
            this.data = data;
            this.menuData = menuData;
        }
    }
}
