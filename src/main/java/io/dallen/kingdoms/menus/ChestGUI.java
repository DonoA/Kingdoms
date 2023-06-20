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
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

@Getter
public class ChestGUI {

    private static HashMap<String, MenuInstance> openMenus = new HashMap<>();

    private static HashMap<String, Long> cooldown = new HashMap<>();

    @Setter
    private String name;
    @Setter
    private int size;
    @Setter
    private InventoryType type;
    @Setter
    private Consumer<OptionClickEvent> clickHandler;
    @Setter
    private Consumer<CloseEvent>  closeHandler;

    private Object menuData;
    private String[] optionNames;
    private ItemStack[] optionIcons;
    private Object[] optionData;

    public ChestGUI(String name, InventoryType type, Consumer<OptionClickEvent> clickHandler) {
        this.type = type;
        this.name = name;
        this.clickHandler = clickHandler;
        this.size = type.getDefaultSize();
        optionNames = new String[this.size];
        optionIcons = new ItemStack[this.size];
        optionData = new Object[this.size];
    }

    public ChestGUI(String name, int size, Consumer<OptionClickEvent> clickHandler) {
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
        this.clickHandler = clickHandler;
    }

    public ChestGUI setCloseEvent(Consumer<CloseEvent> closeHandler) {
        this.closeHandler = closeHandler;
        return this;
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
//        ContainerAccess access = ContainerAccess.a(((CraftWorld) player.getWorld()).getHandle(),
//                ((CraftBlock) player.getLocation().getBlock()).getPosition());
//        ContainerAnvil anvilContainer = new ContainerAnvil(-1, ((CraftInventoryPlayer) player.getInventory()).getInventory(), access);
//        anvilContainer.setTitle(CraftChatMessage.fromStringOrNull("Claim"));
//        anvilContainer.maximumRepairCost = 0;
//        Inventory inventory = anvilContainer.getBukkitView().getTopInventory();

        Inventory inventory = Bukkit.createInventory(player, type, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        MenuInstance menu;
        if (type == InventoryType.ANVIL) {
            menu = new AnvilMenuInstance(this, inventory);
        } else {
            menu = new MenuInstance(this, inventory);
        }
        player.openInventory(inventory);
        openMenus.put(player.getName(), menu);
    }

    @Getter
    public static class MenuInstance {
        private String name;
        private int size;
        private Consumer<OptionClickEvent> clickHandler;
        private Consumer<CloseEvent>  closeHandler;
        private String[] optionNames;
        private ItemStack[] optionIcons;
        private Inventory inventory;

        public MenuInstance(ChestGUI menu, Inventory inv) {
            this.name = menu.name;
            this.size = menu.size;
            this.optionNames = menu.optionNames;
            this.optionIcons = menu.optionIcons;
            this.clickHandler = menu.clickHandler;
            this.closeHandler = menu.closeHandler;
            this.inventory = inv;
        }
    }

    @Getter
    public static class AnvilMenuInstance extends MenuInstance {

        @Setter
        private String currentItemName = "";

        public AnvilMenuInstance(ChestGUI menu, Inventory inv) {
            super(menu, inv);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class OptionClickEvent {
        private final Player player;
        private final MenuInstance menu;
        private final ItemStack clicked;

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

            MenuInstance menu = openMenus.get(playerName);
            if (menu.closeHandler == null) {
                return;
            }

            var closeEvent = new CloseEvent(e.getPlayer(), menu);
            menu.closeHandler.accept(closeEvent);
            // Trigger close event
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
                    menu, clicked);
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
