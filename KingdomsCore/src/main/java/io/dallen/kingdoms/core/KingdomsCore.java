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
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

/**
 *
 * @author Donovan Allen
 */
public class KingdomsCore extends JavaPlugin{
    
    @Getter
    private static KingdomsCore Plugin;
    
    @Getter @Setter
    private static NpcManager NPCs;
    
    @Getter
    private static Set<Class<? extends Plot>> StructureClasses;
    
    @Getter
    private static Set<Class<? extends SaveType.NativeType>> NativeTypes;
    
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
    
    @Override
    public void onEnable(){
        Plugin = this;
        Reflections reflections = new Reflections("io.dallen.Kingdoms.core");
        Set<Class<? extends Listener>> lstn = reflections.getSubTypesOf(Listener.class);
        for(Class<? extends Listener> l : lstn){
            boolean hasEventHandler = false;
            for(Method m : l.getDeclaredMethods()){
                if(m.isAnnotationPresent(EventHandler.class)){
                    hasEventHandler = true;
                }
            }
            if(hasEventHandler){
                try {
                    l.getConstructor();
                    Bukkit.getPluginManager().registerEvents((Listener) l.newInstance(), this);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException ex) {}
            }
        }
        reflections = new Reflections("io.dallen.Kingdoms.core.Structures.Types");
        StructureClasses = reflections.getSubTypesOf(Plot.class);
        reflections = new Reflections("io.dallen.Kingdoms.core.Storage");
        NativeTypes = reflections.getSubTypesOf(SaveType.NativeType.class);
        setupDatabase();
        protocolManager = ProtocolLibrary.getProtocolManager();
        SkinPacketHandler SkinHandler = new SkinPacketHandler();
        protocolManager.addPacketListener(SkinHandler.getAdapter());
        Bukkit.getScheduler().runTaskAsynchronously(this, SkinHandler);
        skinHandler = SkinHandler;
        CraftingHandler crafting = new CraftingHandler(this);
        for(Class<MultiBlock> mb : MultiBlock.getMultiBlockClasses()){
            try {
                mb.getMethod("loadForm").invoke(mb);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LogUtil.printErr("error loading multiblock forms");
            }
        }
        for(String d : this.getDescription().getDepend()){
            if((this.getServer().getPluginManager().getPlugin(d) == null) || 
                (!this.getServer().getPluginManager().getPlugin(d).isEnabled())){
                //Check version
                LogUtil.printErr(d + " not found!");
                LogUtil.printErr("Shutting Down!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        if(this.getConfig().getBoolean("decay")){
            changes = new ChangeTracker(Plugin);
        }
        NPCs = new NpcManager();
//        ModerationCommands moderation = new ModerationCommands();
//        GeneralCommands general = new GeneralCommands();
//        AdminCommands admin = new AdminCommands();
        MainMenuHandler mmh = new MainMenuHandler();
        if(this.getConfig().getBoolean("debug.enabled")){
            DebugCommands dbg = new DebugCommands(new File(this.getConfig().getString("debug.buildfolder")));
            this.getCommand("fillplot").setExecutor(dbg);
            this.getCommand("setskins").setExecutor(dbg);
            this.getCommand("cleannpcs").setExecutor(dbg);
            this.getCommand("decayall").setExecutor(dbg);
            this.getCommand("save-kingdoms").setExecutor(dbg);
        }
        this.getCommand("menu").setExecutor(mmh);
//        this.getCommand("crash").setExecutor(admin);
//        this.getCommand("editschem").setExecutor(admin);
//        this.getCommand("strack").setExecutor(admin);
//        this.getCommand("message").setExecutor(general);
//        this.getCommand("reply").setExecutor(general);
//        this.getCommand("info").setExecutor(general);
//        this.getCommand("help").setExecutor(general);
//        this.getCommand("list").setExecutor(general);
//        this.getCommand("self").setExecutor(general);
//        this.getCommand("is").setExecutor(general);
//        this.getCommand("setpost").setExecutor(general);
//        this.getCommand("where").setExecutor(moderation);
//        this.getCommand("ban").setExecutor(moderation);
//        this.getCommand("unban").setExecutor(moderation);
//        this.getCommand("ipban").setExecutor(moderation);
//        this.getCommand("unipban").setExecutor(moderation);
//        this.getCommand("kick").setExecutor(moderation);
//        this.getCommand("tmpban").setExecutor(moderation);
//        this.getCommand("invspy").setExecutor(moderation);
//        this.getCommand("fjail").setExecutor(moderation);
//        this.getCommand("vanish").setExecutor(moderation);
//        this.getCommand("uuid").setExecutor(moderation);
//        this.getCommand("broadcast").setExecutor(moderation);
//        this.getCommand("kingdom").setExecutor(new KingdomCommands());
//        this.getCommand("chat").setExecutor(new ChatHandler());
//        this.getCommand("party").setExecutor(new Party.PartyCommands());
//        this.getCommand("mute").setExecutor(new MuteCommand());
//        RedisManager RM = new RedisManager();
    }
    
    @Override
    public void onDisable(){
        DataLoadHelper.SaveKingdomData();
    }
    
    public void registerModule(KingdomModual modual){
        registeredModuals.add(modual);
        Reflections reflections = new Reflections(modual.getClassPath());
        Set<Class<? extends Listener>> lstn = reflections.getSubTypesOf(Listener.class);
        for(Class<? extends Listener> l : lstn){
            boolean hasEventHandler = false;
            for(Method m : l.getDeclaredMethods()){
                if(m.isAnnotationPresent(EventHandler.class)){
                    hasEventHandler = true;
                }
            }
            if(hasEventHandler){
                try {
                    l.getConstructor();
                    Bukkit.getPluginManager().registerEvents((Listener) l.newInstance(), this);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException ex) {}
            }
        }
        if(modual.isHasStructures()){
            reflections = new Reflections(modual.getStructurePath());
            StructureClasses.addAll(reflections.getSubTypesOf(Plot.class));
        }
        reflections = new Reflections(modual.getStoragePath());
        NativeTypes.addAll(reflections.getSubTypesOf(SaveType.NativeType.class));
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
    
    public static void setupDatabase(){
        File plugin = KingdomsCore.getPlugin().getDataFolder();
        for(String s : new String[] {"prefabs", "playerdata", "multiblock", "savedata"}){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + s).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + s).mkdir();
            }
        }
        for(Class<?> c : StructureClasses){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).mkdir();
            }
        }
        for(Class<?> c : MultiBlock.getMultiBlockClasses()){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + "multiblock" + DBmanager.getFileSep() + c.getSimpleName()).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + "multiblock" + DBmanager.getFileSep() + c.getSimpleName()).mkdir();
            }
        }
        for(String s : new String[] {"kingdoms", "municipals", "plots"}){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + s).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + s).mkdir();
            }
        }
    }
}
