package io.dallen.kingdoms;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.dallen.kingdoms.commands.TestCommand;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.CustomBlockIndex;
import io.dallen.kingdoms.customblocks.CustomBlockListener;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.customitems.CustomItemListener;
import io.dallen.kingdoms.kingdom.CraftingListener;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.kingdom.MobListener;
import io.dallen.kingdoms.kingdom.MobSpawning;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.packets.PacketListeners;
import io.dallen.kingdoms.commands.update.UpdateCommand;
import io.dallen.kingdoms.commands.worldgen.WorldGenCommand;
import io.dallen.kingdoms.savedata.adapters.LocationAdapter;
import io.dallen.kingdoms.savedata.adapters.NPCAdapter;
import io.dallen.kingdoms.savedata.adapters.OfflinePlayerAdapter;
import io.dallen.kingdoms.savedata.adapters.PlayerAdapter;
import io.dallen.kingdoms.savedata.adapters.RefAdapter;
import io.dallen.kingdoms.util.ItemUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Kingdoms extends JavaPlugin {

    public static Kingdoms instance;
    public static ProtocolManager protocolManager;
    public static Gson gson;

    static {
        var builder = new GsonBuilder();

        LocationAdapter.register(builder);
        RefAdapter.register(builder);
        OfflinePlayerAdapter.register(builder);
        PlayerAdapter.register(builder);
        NPCAdapter.register(builder);

        gson = builder.create();
    }

    @Override
    public void onEnable() {
        instance = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        PacketListeners.registerListeners(this, protocolManager);

        var mainworld = Bukkit.getWorlds().get(0);
        MobSpawning.startSpawning(this);

        var worldGen = new WorldGenCommand(mainworld);
        getCommand("genworld").setExecutor(worldGen);

        getServer().getPluginManager().registerEvents(new CustomBlockListener(), this);
        getServer().getPluginManager().registerEvents(new CustomItemListener(), this);
        getServer().getPluginManager().registerEvents(new ChestGUI.ChestGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(), this);

        getCommand("update").setExecutor(new UpdateCommand());
        getCommand("test").setExecutor(new TestCommand());

        setupCrafting();
        setGameRules(mainworld);

//        CustomBlockData.loadAll();
        Kingdom.getKingdomIndex().loadAll();
    }

    @Override
    public void onDisable() {
//        CustomBlockData.saveAll();
        Kingdom.getKingdomIndex().saveAll();
    }

    private void setGameRules(World world) {
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }

    private void setupCrafting() {
//        New Recepies to be added
        CraftingListener.Recipe.builder()
                .name("claim")
                .result(CustomBlockIndex.CLAIM.itemStack())
                .topRow("SPS")
                .midRow("PSP")
                .btmRow("SPS")
                .itemMap('S', Material.COBBLESTONE)
                .itemMap('P', Material.OAK_PLANKS)
                .build()
                .register(this);

        CraftingListener.Recipe.builder()
                .name("plot")
                .result(CustomBlockIndex.PLOT.itemStack())
                .topRow("SPS")
                .midRow("P P")
                .btmRow("SPS")
                .itemMap('S', Material.COBBLESTONE)
                .itemMap('P', Material.OAK_PLANKS)
                .build()
                .register(this);

//        Old Recipies to be removed
        var cannotCraftItem = CustomItemIndex.EMPTY.toItemStack();
        ItemUtil.setItemNameAndLore(cannotCraftItem, "Cannot be crafted by hand");

        var toolMaterials = List.of(
                Pair.of(Material.COBBLESTONE, "stone"),
                Pair.of(Material.IRON_INGOT, "iron"),
                Pair.of(Material.DIAMOND, "diamond")
        );
        for (var material : toolMaterials) {
            var hoeItem = material.getRight() + "_hoe";
            CraftingListener.Recipe.builder()
                    .name(hoeItem)
                    .result(cannotCraftItem)
                    .toRemove(Material.valueOf(hoeItem.toUpperCase()))
                    .topRow(" CC")
                    .midRow(" S ")
                    .btmRow(" S ")
                    .itemMap('C', material.getLeft())
                    .itemMap('S', Material.STICK)
                    .build()
                    .register(this);

            var axeItem = material.getRight() + "_axe";
            CraftingListener.Recipe.builder()
                    .name(axeItem)
                    .result(cannotCraftItem)
                    .toRemove(Material.valueOf(axeItem.toUpperCase()))
                    .topRow("CC ")
                    .midRow("CS ")
                    .btmRow(" S ")
                    .itemMap('C', material.getLeft())
                    .itemMap('S', Material.STICK)
                    .build()
                    .register(this);

            var pickaxeItem = material.getRight() + "_pickaxe";
            CraftingListener.Recipe.builder()
                    .name(pickaxeItem)
                    .result(cannotCraftItem)
                    .toRemove(Material.valueOf(pickaxeItem.toUpperCase()))
                    .topRow("CCC")
                    .midRow(" S ")
                    .btmRow(" S ")
                    .itemMap('C', material.getLeft())
                    .itemMap('S', Material.STICK)
                    .build()
                    .register(this);
        }

        var armorMaterials = List.of(
                Pair.of(Material.LEATHER, "leather"),
                Pair.of(Material.IRON_INGOT, "iron"),
                Pair.of(Material.GOLD_INGOT, "golden"),
                Pair.of(Material.DIAMOND, "diamond"));
        for (var material : armorMaterials) {
            var helmetItem = material.getRight() + "_helmet";
            CraftingListener.Recipe.builder()
                    .name(helmetItem)
                    .result(cannotCraftItem)
                    .toRemove(Material.valueOf(helmetItem.toUpperCase()))
                    .topRow("LLL")
                    .midRow("L L")
                    .itemMap('L', material.getLeft())
                    .build()
                    .register(this);

            var chestItem = material.getRight() + "_chestplate";
            CraftingListener.Recipe.builder()
                    .name(chestItem)
                    .result(cannotCraftItem)
                    .toRemove(Material.valueOf(chestItem.toUpperCase()))
                    .topRow("L L")
                    .midRow("LLL")
                    .btmRow("LLL")
                    .itemMap('L', material.getLeft())
                    .build()
                    .register(this);

            var leggingsItem = material.getRight() + "_leggings";
            CraftingListener.Recipe.builder()
                    .name(leggingsItem)
                    .result(cannotCraftItem)
                    .toRemove(Material.valueOf(leggingsItem.toUpperCase()))
                    .topRow("LLL")
                    .midRow("L L")
                    .btmRow("L L")
                    .itemMap('L', material.getLeft())
                    .build()
                    .register(this);

            var bootsItem = material.getRight() + "_boots";
            CraftingListener.Recipe.builder()
                    .name(bootsItem)
                    .result(cannotCraftItem)
                    .toRemove(Material.valueOf(bootsItem.toUpperCase()))
                    .topRow("L L")
                    .midRow("L L")
                    .itemMap('L', material.getLeft())
                    .build()
                    .register(this);
        }
    }


}
