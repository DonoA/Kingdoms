package io.dallen.kingdoms.customitems;

import org.bukkit.Material;

public class CustomMenuItem extends CustomItem {

    public final static String SUBMIT = "Submit";
    public final static String CANCEL = "Cancel";
    public final static String EMPTY = " ";

    public CustomMenuItem(String name, Material baseItem) {
        super(name, baseItem);
    }
}
