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

import io.dallen.Kingdoms.Main;
import java.util.Arrays;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author donoa_000
 */
public class ChestGUI implements Listener{
    
    private String name;
    private int size;
    private InventoryType type;
    private OptionClickEventHandler handler;
   
    private String[] optionNames;
    private ItemStack[] optionIcons;
    private HashMap<String, Object[]> optionData;
    
    public ChestGUI(String name, InventoryType type, OptionClickEventHandler handler){
        this.type = type;
        this.name = name;
        this.handler = handler;
        this.size = 5*9;
        optionNames = new String[5*9];
        optionIcons = new ItemStack[5*9];
        final int s = this.size;
        optionData = new HashMap<String, Object[]>() {{
            put("all", new Object[s]);
        }};
        Main.getPlugin().getServer().getPluginManager().registerEvents(this, Main.getPlugin());
    }
    
    public ChestGUI(String name, int size, OptionClickEventHandler handler){
        this.type = InventoryType.CHEST;
        this.name = name;
        this.size = size*9;
        optionNames = new String[this.size];
        optionIcons = new ItemStack[this.size];
        final int s = this.size;
        optionData = new HashMap<String, Object[]>() {{
            put("all", new Object[s]);
        }};
        this.handler = handler;
        Main.getPlugin().getServer().getPluginManager().registerEvents(this, Main.getPlugin());
    }
   
    public ChestGUI setOption(int pos, ItemStack icon, String name, String... info){
        optionNames[pos] = name;
        optionIcons[pos] = setItemNameAndLore(icon, name, info);
        return this;
    }
    
    public ChestGUI setOption(int pos, ItemStack icon, String name, Object data, String... info){
        optionNames[pos] = name;
        optionIcons[pos] = setItemNameAndLore(icon, name, info);
        optionData.get("all")[pos] = data; 
        return this;
    }
//      WILL USE IF NEED INSTANCED OPTION TITLES
//
//    public ChestGUI setOption(int pos, ItemStack icon, String name, Player p, String... info){
//        optionNames[pos] = name;
//        optionIcons[pos] = setItemNameAndLore(icon, name, info);
//        return this;
//    }
    
    public ChestGUI setOption(int pos, ItemStack icon, String name, Object data, Player p, String... info){
        optionNames[pos] = name;
        optionIcons[pos] = setItemNameAndLore(icon, name, info);
        if(!optionData.containsKey(p.getName())){
            optionData.put(p.getName(), new Object[this.size]);
        }
        optionData.get(p.getName())[pos] = data;
       return this;
    }
   
    public void sendMenu(Player player){
        Inventory inventory = null;
        if(type.equals(InventoryType.CHEST)){
            inventory = Bukkit.createInventory(player, size, name);
            for (int i = 0; i < optionIcons.length; i++){
                if (optionIcons[i] != null){
                    inventory.setItem(i, optionIcons[i]);
                }
            }
        }else{
            inventory = Bukkit.createInventory(player, type, name);
            for (int i = 0; i < optionIcons.length; i++){
                if (optionIcons[i] != null){
                    inventory.setItem(i, optionIcons[i]);
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
        optionData = null;
    }
   
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getInventory().getTitle().equals(name)){
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot >= 0 && optionNames[slot] != null){
                Object dat = optionData.get("all")[slot];
                if(optionData.containsKey(((Player) event.getWhoClicked()).getName())){
                    dat = optionData.get(((Player) event.getWhoClicked()).getName())[slot];
                }
                OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, dat, optionNames[slot], name);
                handler.onOptionClick(e);
                if (e.isClose()){
                    final Player p = (Player) event.getWhoClicked();
                    final OptionClickEvent ev = e;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
                        @Override
                        public void run(){
                            if(ev.getNext() != null){
                                ev.getNext().sendMenu(p);
                            }else{
                                p.closeInventory();
                            }
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
        
        @Getter
        private String menuName;
        
        @Getter @Setter
        private boolean close;
        
        @Getter @Setter
        private ChestGUI next;
        
        @Getter @Setter
        private boolean destroy;
        
        @Getter
        private Object data;
       
        public OptionClickEvent(Player player, int position, Object data, String name, String menuName){
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = false;
            this.menuName = menuName;
            this.next = null;
            this.data = data;
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
