package io.dallen.kingdoms.menus;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.events.AnvilRenameEvent;
import io.dallen.kingdoms.util.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

@Getter
public class ChestGUI {

    private static HashMap<String, MenuInstance> openMenus = new HashMap<>();

    private static HashMap<String, Long> cooldown = new HashMap<>();

    @Setter
    private String name;

    private int size;
    private InventoryType type;
    private Consumer<OptionClickEvent> clickHandler;
    private Consumer<CloseEvent>  closeHandler;

    private String[] optionNames;
    private ItemStack[] optionIcons;
    private Object[] optionData;

    public ChestGUI(String name, InventoryType type) {
        this.type = type;
        this.name = name;
        this.size = type.getDefaultSize();
        optionNames = new String[this.size];
        optionIcons = new ItemStack[this.size];
        optionData = new Object[this.size];
    }

    public ChestGUI(String name, int size) {
        this.type = InventoryType.CHEST;
        this.name = name;
        this.size = size;
        optionNames = new String[this.size];
        optionIcons = new ItemStack[this.size];
        optionData = new Object[this.size];
    }

    public ChestGUI setClickHandler(Consumer<OptionClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public ChestGUI setCloseEvent(Consumer<CloseEvent> closeHandler) {
        this.closeHandler = closeHandler;
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

    public void setOptions(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }

            setOption(i, items[i]);
        }
    }

    public MenuInstance sendMenu(Player player) {
        Inventory inventory;
        if (type == InventoryType.CHEST) {
            inventory = Bukkit.createInventory(player, size, name);
        } else {
            inventory = Bukkit.createInventory(player, type, name);
        }

        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        var view = player.openInventory(inventory);

        MenuInstance menuInstance;
        if (type == InventoryType.ANVIL) {
            menuInstance = new AnvilMenuInstance(this, view);
        } else {
            menuInstance = new MenuInstance(this, view);
        }
        openMenus.put(player.getName(), menuInstance);
        return menuInstance;
    }

    public void refreshAllViewers() {
        for (var menu : openMenus.values()) {
            if (menu.getMenu() != this) {
                continue;
            }

            menu.refresh();
        }
    }

    @Getter
    public static class MenuInstance {
        @Setter
        private String name;
        @Setter
        private ItemStack[] optionIcons;

        private final int size;
        private final Consumer<OptionClickEvent> clickHandler;
        private final Consumer<CloseEvent>  closeHandler;
        private final Inventory inventory;
        private final InventoryView view;
        private final ChestGUI menu;

        public MenuInstance(ChestGUI menu, InventoryView inventoryView) {
            this.name = menu.name;
            this.size = menu.size;
            this.optionIcons = menu.optionIcons;
            this.clickHandler = menu.clickHandler;
            this.closeHandler = menu.closeHandler;
            this.menu = menu;
            this.inventory = inventoryView.getTopInventory();
            this.view = inventoryView;
        }

        public void refresh() {
            this.name = this.menu.getName();
            this.optionIcons = this.menu.getOptionIcons();

            var beingViewed = view.getPlayer().getOpenInventory();
            if (beingViewed != this.view) {
                return;
            }

            this.view.setTitle(this.name);
            for (int i = 0; i < this.optionIcons.length; i++) {
                var icon = this.optionIcons[i];
                if (icon == null) {
                    continue;
                }

                this.view.setItem(i, icon);
            }
        }
    }

    @Getter
    public static class AnvilMenuInstance extends MenuInstance {

        @Setter
        private String currentItemName = "";

        public AnvilMenuInstance(ChestGUI menu, InventoryView inv) {
            super(menu, inv);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class OptionClickEvent {
        private final Player player;
        private final MenuInstance menu;
        private final ItemStack clicked;
        private final int slot;

        @Setter
        private boolean close = true;

        @Setter
        private ChestGUI next = null;

        @Setter
        private boolean destroy = false;
    }

    @Getter
    @AllArgsConstructor
    public static class CloseEvent {
        private HumanEntity player;
        private MenuInstance menu;
    }

    public static class ChestGUIHandler implements Listener {

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            openMenus.remove(e.getPlayer().getName());
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            var playerName = e.getPlayer().getName();
            if (!openMenus.containsKey(playerName)) {
                return;
            }

            MenuInstance menu = openMenus.remove(playerName);
            if (menu.closeHandler == null) {
                return;
            }

            var closeEvent = new CloseEvent(e.getPlayer(), menu);
            menu.closeHandler.accept(closeEvent);
        }


        @EventHandler
        public void onRename(AnvilRenameEvent e) {
            var playerName = e.getPlayer().getName();
            if (!openMenus.containsKey(playerName)) {
                return;
            }

            MenuInstance menu = openMenus.get(playerName);
            if (!(menu instanceof AnvilMenuInstance)) {
                return;
            }

            ((AnvilMenuInstance) menu).setCurrentItemName(e.getNewName());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClick(InventoryClickEvent event) {
            var playerName = event.getWhoClicked().getName();
            if (cooldown.containsKey(playerName) && cooldown.get(playerName) > System.currentTimeMillis() - 100) {
                return;
            }
            cooldown.put(playerName, System.currentTimeMillis());
            if (!openMenus.containsKey(playerName)) {
                return;
            }
            MenuInstance menu = openMenus.get(playerName);
            if (!event.getInventory().equals(menu.inventory)) {
                return;
            }
            openMenus.remove(playerName);

            var clicked = event.getCursor();
            event.getCursor().setType(Material.AIR);
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot < 0 || slot >= menu.size) {
                return;
            }

            if (clicked == null || clicked.getType() == Material.AIR) {
                clicked = menu.inventory.getItem(slot);
            }
            OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(),
                    menu, clicked, slot);
            menu.clickHandler.accept(e);
            if (e.close) {
                final Player p = (Player) event.getWhoClicked();
                final OptionClickEvent ev = e;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Kingdoms.instance, () -> {
                    p.getOpenInventory().getCursor().setType(Material.AIR);
                    if (ev.next != null) {
                        ev.next.sendMenu(p);
                    } else {
                        p.closeInventory();
                    }
                }, 1);
            }
            openMenus.remove(event.getWhoClicked().getName());
        }
    }
}
