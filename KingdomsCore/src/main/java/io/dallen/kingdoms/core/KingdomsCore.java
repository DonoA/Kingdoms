/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of KingdomsCore for the Morphics Network.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.kingdoms.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.dallen.kingdoms.core.Handlers.CraftingHandler;
import io.dallen.kingdoms.core.Handlers.DecayHandler.ChangeTracker;
import io.dallen.kingdoms.core.Handlers.MenuHandlers.MainMenuHandler;
import io.dallen.kingdoms.core.Handlers.MultiBlocks.MultiBlock;
import io.dallen.kingdoms.core.Handlers.SkinHandler.SkinPacketHandler;
import io.dallen.kingdoms.core.NPCs.NpcManager;
import io.dallen.kingdoms.core.Storage.DataLoadHelper;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

/**
 *
 * @author Donovan Allen
 */
public class KingdomsCore extends KingdomModual {

    @Getter
    private static KingdomsCore Plugin;

    @Getter
    @Setter
    private static NpcManager NPCs;

    @Getter
    private static List<Class<? extends Plot>> StructureClasses = new ArrayList<Class<? extends Plot>>();

    @Getter
    private static List<Class<? extends SaveType.NativeType>> NativeTypes = new ArrayList<Class<? extends SaveType.NativeType>>();

    @Getter
    private static ProtocolManager protocolManager;

    @Getter
    private static ArrayList<Kingdom> Kingdoms = new ArrayList<Kingdom>();

    @Getter
    private static ArrayList<Municipality> Municipals = new ArrayList<Municipality>();

    @Getter
    private static SkinPacketHandler skinHandler;

    @Getter
    private static ArrayList<KingdomModual> registeredModuals = new ArrayList<KingdomModual>();

    @Getter
    private static ChangeTracker changes;

    @Getter
    private boolean hasStructures = true;

    @Getter
    private static Runnable onServerLoad = new Runnable() { // great api bukkit... really
        @Override
        public void run() {
            for (String d : Plugin.getDescription().getDepend()) {
                if ((Plugin.getServer().getPluginManager().getPlugin(d) == null)
                        || (!Plugin.getServer().getPluginManager().getPlugin(d).isEnabled())) {
                    //Check version
                    LogUtil.printErr(d + " not found!");
                    LogUtil.printErr("Shutting Down!");
                    Plugin.getServer().getPluginManager().disablePlugin(Plugin);
                    return;
                }
            }
            NPCs = new NpcManager();
            if (Plugin.getConfig().getBoolean("debug.enabled")) {
                DebugCommands dbg = new DebugCommands(new File(Plugin.getConfig().getString("debug.buildfolder")));
                Plugin.getCommand("fillplot").setExecutor(dbg);
                Plugin.getCommand("setskins").setExecutor(dbg);
                Plugin.getCommand("cleannpcs").setExecutor(dbg);
                Plugin.getCommand("decayall").setExecutor(dbg);
                Plugin.getCommand("save-kingdoms").setExecutor(dbg);
            }
            SkinPacketHandler SkinHandler = new SkinPacketHandler();
            protocolManager.addPacketListener(SkinHandler.getAdapter());
            Bukkit.getScheduler().runTaskAsynchronously(Plugin, SkinHandler);
            skinHandler = SkinHandler;
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        Plugin = this;
        setupDatabase();
        protocolManager = ProtocolLibrary.getProtocolManager();
        CraftingHandler crafting = new CraftingHandler(this);
        for (Class<? extends MultiBlock> mb : MultiBlock.getMultiBlockClasses()) {
            try {
                mb.getMethod("loadForm").invoke(mb);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LogUtil.printErr("error loading multiblock forms");
            }
        }
        if (this.getConfig().getBoolean("decay")) {
            changes = new ChangeTracker(Plugin);
        }
        MainMenuHandler mmh = new MainMenuHandler();
        this.getCommand("menu").setExecutor(mmh);
        Reflections reflections = new Reflections(getClass().getPackage().getName());
        setLstn(reflections.getSubTypesOf(Listener.class));
        registerModule(this);
        StructureClasses.addAll(reflections.getSubTypesOf(Plot.class));
        NativeTypes.addAll(reflections.getSubTypesOf(SaveType.NativeType.class));
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, onServerLoad);
    }

    @Override
    public void onDisable() {
        DataLoadHelper.SaveKingdomData();
    }

    public void registerModule(KingdomModual modual) {
        for (Class<? extends Listener> l : modual.getLstn()) {
            boolean hasEventHandler = false;
            for (Method m : l.getDeclaredMethods()) {
                if (m.isAnnotationPresent(EventHandler.class)) {
                    hasEventHandler = true;
                }
            }
            if (hasEventHandler) {
                try {
                    l.getConstructor();
                    Bukkit.getPluginManager().registerEvents((Listener) l.newInstance(), this);
                    LogUtil.printDebug("Registered Listener " + l.getSimpleName());
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException ex) {
                }
            }
        }
        modual.setModualName(modual.getClass().getSimpleName());
        registeredModuals.add(modual);
    }

    /*
     Kingdoms
     |   config.yml
     |
     +---multiblock
     |   \---Forge
     +---playerdata
     +---prefabs
     |   +---Armory
     |   +---Bank
     |   +---Barracks
     |   +---Blacksmith
     |   +---BuildersHut
     |   +---Castle
     |   +---Dungeon
     |   +---Farm
     |   +---Marketplace
     |   +---Stable
     |   +---Storeroom
     |   +---TownHall
     |   \---TrainingGround
     \---savedata
     +---kingdoms
     +---municipals
     \---plots
     */
    public static void setupDatabase() {
        File plugin = KingdomsCore.getPlugin().getDataFolder();
        for (String s : new String[]{"prefabs", "playerdata", "multiblock", "savedata"}) {
            if (!new File(plugin.getPath() + DBmanager.getFileSep() + s).exists()) {
                new File(plugin.getPath() + DBmanager.getFileSep() + s).mkdir();
            }
        }
        for (Class<?> c : StructureClasses) {
            if (!new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).exists()) {
                new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).mkdir();
            }
        }
        for (Class<?> c : MultiBlock.getMultiBlockClasses()) {
            if (!new File(plugin.getPath() + DBmanager.getFileSep() + "multiblock" + DBmanager.getFileSep() + c.getSimpleName()).exists()) {
                new File(plugin.getPath() + DBmanager.getFileSep() + "multiblock" + DBmanager.getFileSep() + c.getSimpleName()).mkdir();
            }
        }
        for (String s : new String[]{"kingdoms", "municipals", "plots"}) {
            if (!new File(plugin.getPath() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + s).exists()) {
                new File(plugin.getPath() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + s).mkdir();
            }
        }
    }
}
