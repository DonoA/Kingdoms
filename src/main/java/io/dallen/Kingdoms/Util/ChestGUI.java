/*
 * This file is part of Kingdoms.
 * 
 * Kingdoms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdoms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdoms.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Util;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author donoa_000
 */
public class ChestGUI implements Listener{
    
    private String name;
    private int size;
    private InventoryType type;
    private OptionClickEventHandler handler;
   
    private ArrayList<String> optionNames = new ArrayList<String>();
    private ArrayList<ItemStack> optionIcons = new ArrayList<ItemStack>();
    
    @Setter
    private static JavaPlugin plugin = null;
    
    public ChestGUI(String name, InventoryType type, OptionClickEventHandler handler){
        if(plugin == null){
            LogUtil.printErr("Cannot create chest GUI, no plugin set");
            return;
        }
        this.type = type;
        this.name = name;
        this.handler = handler;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public ChestGUI(String name, int size, OptionClickEventHandler handler){
        if(plugin == null){
            LogUtil.printErr("Cannot create chest GUI, no plugin set");
            return;
        }
        this.type = InventoryType.CHEST;
        this.name = name;
        this.size = size*9;
        this.handler = handler;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
   
    public ChestGUI setOption(int pos, ItemStack icon, String name, String... info){
        optionNames.set(pos, name);
        optionIcons.set(pos, setItemNameAndLore(icon, name, info));
        return this;
    }
   
    public void sendMenu(Player player){
        Inventory inventory = null;
        if(type.equals(InventoryType.CHEST)){
            inventory = Bukkit.createInventory(player, size, name);
            for (int i = 0; i < optionIcons.size(); i++){
                if (optionIcons.get(i) != null){
                    inventory.setItem(i, optionIcons.get(i));
                }
            }
        }else{
            inventory = Bukkit.createInventory(player, type, name);
            for (int i = 0; i < optionIcons.size(); i++){
                if (optionIcons.get(i) != null){
                    inventory.setItem(i, optionIcons.get(i));
                }
            }
        }
        player.openInventory(inventory);
    }
   
    public void destroy(){
        HandlerList.unregisterAll(this);
        handler = null;
        optionNames = null;
        optionIcons = null;
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event){
        if (event.getInventory().getTitle().equals(name)){
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames.get(slot) != null){
                OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, optionNames.get(slot));
                handler.onOptionClick(e);
                if (e.isClose()){
                    final Player p = (Player) event.getWhoClicked();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                        @Override
                        public void run(){
                            p.closeInventory();
                        }
                    }, 1);
                }
                if (e.isDestroy()){
                    destroy();
                }
            }
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
        
        @Getter @Setter
        private boolean close;
        
        @Getter
        private boolean destroy;
       
        public OptionClickEvent(Player player, int position, String name){
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = false;
        }
    }
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore){
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
    
}
