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
package io.dallen.kingdoms.utilities.Utils;

import io.dallen.kingdoms.utilities.KingdomsUtilities;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class ChestGUI {

    private static HashMap<String, MenuInstance> openMenus = new HashMap<String, MenuInstance>();

    private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private int size;
    @Setter
    @Getter
    private InventoryType type;
    @Setter
    private OptionClickEventHandler handler;
    @Getter
    private Object menuData;

    private String[] optionNames;
    private ItemStack[] optionIcons;
    @Getter
    private Object[] optionData;

    public ChestGUI(String name, InventoryType type, OptionClickEventHandler handler) {
        this.type = type;
        this.name = name;
        this.handler = handler;
        this.size = type.getDefaultSize();
        optionNames = new String[this.size];
        optionIcons = new ItemStack[this.size];
        optionData = new Object[this.size];
    }

    public ChestGUI(String name, int size, OptionClickEventHandler handler) {
        this.type = InventoryType.CHEST;
        this.name = name;
        if (size > 8) {
            this.size = size;
        } else {
            this.size = size * 9;
        }
        optionNames = new String[this.size];
        optionIcons = new ItemStack[this.size];
        optionData = new Object[this.size];
        this.handler = handler;
    }

    public ChestGUI setMenuData(Object o) {
        this.menuData = o;
        return this;
    }

    public ChestGUI setOption(int pos, ItemStack icon) {
        optionNames[pos] = name;
        optionIcons[pos] = icon;
        return this;
    }

    public ChestGUI setOption(int pos, ItemStack icon, String name, String... info) {
        optionNames[pos] = name;
        optionIcons[pos] = ItemUtil.setItemNameAndLore(icon, name, info);
        return this;
    }

    public ChestGUI setOption(int pos, ItemStack icon, String name, Object data, String... info) {
        optionNames[pos] = name;
        optionIcons[pos] = ItemUtil.setItemNameAndLore(icon, name, info);
        optionData[pos] = data;
        return this;
    }

    public ChestGUI removeOption(int pos) {
        optionNames[pos] = null;
        optionIcons[pos] = null;
        optionData[pos] = null;
        return this;
    }

    public ChestGUI clearOptions() {
        optionNames = new String[this.size];
        optionIcons = new ItemStack[this.size];
        optionData = new Object[this.size];
        return this;
    }

    public void sendMenu(Player player) {
        Inventory inventory = null;
        if (type.equals(InventoryType.CHEST)) {
            inventory = Bukkit.createInventory(player, (int) Math.ceil(((double) size) / 9) * 9, name);
            for (int i = 0; i < optionIcons.length; i++) {
                if (optionIcons[i] != null) {
                    inventory.setItem(i, optionIcons[i]);
                }
            }
        } else {
            inventory = Bukkit.createInventory(player, type, name);
            for (int i = 0; i < optionIcons.length; i++) {
                if (optionIcons[i] != null) {
                    inventory.setItem(i, optionIcons[i]);
                }
            }
        }
        final MenuInstance menu = new MenuInstance(this);
        player.openInventory(inventory);
        openMenus.put(player.getName(), menu);
    }

    public static class MenuInstance {

        private String name;
        private int size;
        private OptionClickEventHandler handler;
        private String[] optionNames;
        private ItemStack[] optionIcons;
        private Object[] optionData;
        private Object menuData;

        public MenuInstance(ChestGUI menu) {
            this.name = menu.name;
            this.size = menu.size;
            this.handler = menu.handler;
            this.optionData = menu.optionData;
            this.optionNames = menu.optionNames;
            this.optionIcons = menu.optionIcons;
            this.menuData = menu.menuData;
        }
    }

    public interface OptionClickEventHandler {

        public void onOptionClick(OptionClickEvent event);
    }

    public static class OptionClickEvent {

        @Getter
        private Player player;

        @Getter
        private int position;

        @Getter
        private String name;

        @Getter
        private String menuName;

        @Getter
        @Setter
        private boolean close;

        @Getter
        @Setter
        private ChestGUI next;

        @Getter
        @Setter
        private boolean destroy;

        @Getter
        private Object data;

        @Getter
        private Object menuData;

        public OptionClickEvent(Player player, int position, Object data, String name, String menuName, Object menuData) {
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

    public static class ChestGUIHandler implements Listener {

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (openMenus.containsKey(e.getPlayer().getName())) {
                openMenus.remove(e.getPlayer().getName());
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClick(InventoryClickEvent event) {
            if ((!cooldown.containsKey(event.getWhoClicked().getName()))
                    || (cooldown.containsKey(event.getWhoClicked().getName()) && cooldown.get(event.getWhoClicked().getName()) < System.currentTimeMillis() - 100)) {
                cooldown.put(event.getWhoClicked().getName(), System.currentTimeMillis());
                if (!openMenus.containsKey(event.getWhoClicked().getName())) {
                    return;
                }
                MenuInstance menu = openMenus.get(event.getWhoClicked().getName());
                if (event.getInventory().getTitle().equals(menu.name)) {
                    event.getCursor().setType(Material.AIR);
                    event.setCancelled(true);
                    int slot = event.getRawSlot();
                    if (slot >= 0 && slot < menu.size && menu.optionNames[slot] != null) {
                        OptionClickEvent e
                                = new OptionClickEvent((Player) event.getWhoClicked(), slot, menu.optionData[slot], menu.optionNames[slot], menu.name, menu.menuData);
                        menu.handler.onOptionClick(e);
                        if (e.isClose()) {
                            final Player p = (Player) event.getWhoClicked();
                            final OptionClickEvent ev = e;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomsUtilities.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    p.getOpenInventory().getCursor().setType(Material.AIR);
                                    if (ev.getNext() != null) {
                                        ev.getNext().sendMenu(p);
                                    } else {
                                        p.closeInventory();
                                    }
                                }
                            }, 1);
                        }
                    }
                }
                openMenus.remove(event.getWhoClicked().getName());
            }
        }
    }
}
