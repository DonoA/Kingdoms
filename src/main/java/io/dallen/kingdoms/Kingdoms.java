package io.dallen.kingdoms;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.customblocks.CustomBlockIndex;
import io.dallen.kingdoms.customblocks.CustomBlockListener;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.packets.PacketListeners;
import io.dallen.kingdoms.update.UpdateCommand;
import io.dallen.kingdoms.worldgen.WorldGenCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Kingdoms extends JavaPlugin {

    public static Kingdoms instance;
    public static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        PacketListeners.registerListeners(this, protocolManager);

        getServer().getPluginManager().registerEvents(new CustomBlockListener(), this);
        getServer().getPluginManager().registerEvents(new ChestGUI.ChestGUIHandler(), this);

        getCommand("update").setExecutor(new UpdateCommand());
        getCommand("genworld").setExecutor(new WorldGenCommand());

        setupCrafting();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setupCrafting() {
        var namespace = new NamespacedKey(this, "claim");
        var claim = new ShapedRecipe(namespace, CustomBlockIndex.CLAIM.itemStack());
        claim.shape(" P ", "PIP", " P ");
        claim.setIngredient('P', Material.OAK_PLANKS);
        claim.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(claim);
    }
}
