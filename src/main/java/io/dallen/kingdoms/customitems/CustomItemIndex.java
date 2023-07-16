package io.dallen.kingdoms.customitems;

import org.bukkit.Material;

public class CustomItemIndex {
    public final static CustomItem SUBMIT = new CustomMenuItem(CustomMenuItem.SUBMIT, Material.ANGLER_POTTERY_SHERD);
    public final static CustomItem CANCEL = new CustomMenuItem(CustomMenuItem.CANCEL, Material.ARCHER_POTTERY_SHERD);
    public final static CustomItem EMPTY = new CustomMenuItem(CustomMenuItem.EMPTY, Material.ARMS_UP_POTTERY_SHERD);
    public final static CustomItem INCREASE = new CustomMenuItem(CustomMenuItem.EMPTY, Material.BLADE_POTTERY_SHERD);
    public final static CustomItem DECREASE = new CustomMenuItem(CustomMenuItem.EMPTY, Material.BREWER_POTTERY_SHERD);
    public final static CustomItem RECYCLE = new CustomMenuItem(CustomMenuItem.EMPTY, Material.BURN_POTTERY_SHERD);

    public final static CustomItem SET_CHEST_TYPE = new ChestConfigurator(Material.DANGER_POTTERY_SHERD);

}
