package io.dallen.kingdoms.menus;

import java.util.HashMap;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.util.ItemUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HotbarMenu {

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

    public HotbarMenu(String name, OptionClickEventHandler handler) {
        this.name = name;
        this.handler = handler;
        optionNames = new String[9];
        optionIcons = new ItemStack[9];
        optionData = new Object[9];
        optionNames[8] = "Cancel";
        optionIcons[8] = ItemUtil.setItemNameAndLore(Material.ENCHANTED_BOOK, "Cancel");
    }

    public HotbarMenu setOption(int pos, ItemStack icon, String name, String... info) {
        optionNames[pos] = name;
        optionIcons[pos] = ItemUtil.setItemNameAndLore(icon, name, info);
        return this;
    }

    public HotbarMenu setOption(int pos, ItemStack icon, String name, Object data, String... info) {
        optionNames[pos] = name;
        optionIcons[pos] = ItemUtil.setItemNameAndLore(icon, name, info);
        optionData[pos] = data;
        return this;
    }

    public HotbarMenu clearOptions() {
        optionNames = new String[9];
        optionIcons = new ItemStack[9];
        optionData = new Object[9];
        optionNames[8] = "Cancel";
        optionIcons[8] = ItemUtil.setItemNameAndLore(Material.ENCHANTED_BOOK, "Cancel");
        return this;
    }

    public void sendMenu(Player player) {
        ItemStack[] saveBar = new ItemStack[9];
        for (int i = 0; i <= 8; i++) {
            saveBar[i] = player.getInventory().getItem(i);
            player.getInventory().setItem(i, optionIcons[i]);
        }
        final HotbarInstance menu = new HotbarInstance(this);
        menu.setPlayerOldHotbar(saveBar);
        openMenus.put(player.getName(), menu);
    }

    public static void closeMenu(Player player) {
        ItemStack[] hotbar = openMenus.get(player.getName()).getPlayerOldHotbar();
        for (int i = 0; i <= 8; i++) {
            player.getInventory().setItem(i, hotbar[i]);
        }
        openMenus.remove(player.getName());
    }

    public static class HotbarInstance {

        private String name;
        private OptionClickEventHandler handler;
        private String[] optionNames;
        private ItemStack[] optionIcons;
        private Object[] optionData;
        private Object menuData;

        @Getter
        @Setter
        private ItemStack[] playerOldHotbar;

        public HotbarInstance(HotbarMenu menu) {
            this.name = menu.name;
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

    @Getter
    public static class OptionClickEvent {

        private Player player;
        private int position;
        private String name;
        private String menuName;

        @Setter
        private boolean close;

        @Setter
        private HotbarMenu next;

        @Setter
        private boolean destroy;

        private Object data;
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

    public static class HotbarHandler implements Listener {

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (openMenus.containsKey(e.getPlayer().getName())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("You cannot open your inventory in this menu!");
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerInteract(PlayerInteractEvent event) {
            if ((!cooldown.containsKey(event.getPlayer().getName()))
                    || (cooldown.containsKey(event.getPlayer().getName()) && cooldown.get(event.getPlayer().getName()) < System.currentTimeMillis() - 100)) {
                cooldown.put(event.getPlayer().getName(), System.currentTimeMillis());
                if (!openMenus.containsKey(event.getPlayer().getName())) {
                    return;
                }
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    HotbarInstance menu = openMenus.get(event.getPlayer().getName());
                    event.setCancelled(true);
                    int slot = event.getPlayer().getInventory().getHeldItemSlot();
                    if (slot >= 0 && slot < 9 && menu.optionNames[slot] != null) {
                        OptionClickEvent e = new OptionClickEvent(event.getPlayer(), slot, menu.optionData[slot], menu.optionNames[slot], menu.name, menu.menuData);
                        if (menu.optionNames[slot].equalsIgnoreCase("Cancel")) {
                            event.getPlayer().sendMessage("Canceled");
                            e.setClose(true);
                        }
                        menu.handler.onOptionClick(e);
                        if (e.isClose()) {
                            final Player p = event.getPlayer();
                            final OptionClickEvent ev = e;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Kingdoms.instance, () -> {
                                if (ev.getNext() != null) {
                                    throw new UnsupportedOperationException();
                                } else {
                                    HotbarMenu.closeMenu(p);
                                }
                            }, 1);
                        }
                    }
                }
            }
        }
    }
}
