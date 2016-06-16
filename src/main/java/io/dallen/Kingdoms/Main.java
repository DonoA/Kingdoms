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
package io.dallen.Kingdoms;

import io.dallen.Kingdoms.Commands.KingdomCommands;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.dallen.Kingdoms.Handlers.JoinLeaveHandler;
import io.dallen.Kingdoms.Commands.AdminCommands;
import io.dallen.Kingdoms.Commands.DebugCommands;
import io.dallen.Kingdoms.Commands.GeneralCommands;
import io.dallen.Kingdoms.Commands.ModerationCommands;
import io.dallen.Kingdoms.Handlers.DecayHandler.ChangeTracker;
import io.dallen.Kingdoms.NPCs.NpcManager;
import io.dallen.Kingdoms.Handlers.ChatHandler;
import io.dallen.Kingdoms.Handlers.Party;
import io.dallen.Kingdoms.Handlers.MenuHandlers.MainMenuHandler;
import io.dallen.Kingdoms.Handlers.MultiBlockHandler;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Commands.MuteCommand;
import io.dallen.Kingdoms.Handlers.BuildingHandler;
import io.dallen.Kingdoms.Handlers.CraftingHandler;
import io.dallen.Kingdoms.Handlers.MultiBlocks.Forge;
import io.dallen.Kingdoms.Handlers.MultiBlocks.MultiBlock;
import io.dallen.Kingdoms.Handlers.PlantGrowthHandler;
import io.dallen.Kingdoms.Handlers.PlotProtectionHandler;
import io.dallen.Kingdoms.Handlers.SkinHandler.SkinPacketHandler;
import io.dallen.Kingdoms.Handlers.StorageHandler;
import io.dallen.Kingdoms.Handlers.WaterHandler;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.NPCs.Traits.Builder;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.Archer;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.Cavalry;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.General;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.Infantry;
import io.dallen.Kingdoms.Util.ChestGUI.ChestGUIHandler;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.HotbarMenu.HotbarHandler;
import java.io.File;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author donoa_000
 */
public class Main extends JavaPlugin {
    
    @Getter
    private static Main Plugin;
    
    @Getter @Setter
    private static NpcManager NPCs;
    
    @Getter
    private static ProtocolManager protocolManager;
    
    @Getter
    private static ArrayList<Kingdom> Kingdoms = new ArrayList<Kingdom>();
    
    @Getter
    private static ArrayList<Municipality> Municipals = new ArrayList<Municipality>();
    
    @Getter
    private static SkinPacketHandler skinHandler;
    
    @Getter
    private static Runtime runtime = Runtime.getRuntime();
    
    @Getter
    private static ChangeTracker changes;
    
    @Getter @Setter
    private static boolean TickStopped = false;
    
    @Override
    public void onLoad() {
        Plugin = this;
    }

    @Getter
    private static Runnable onServerLoad = new Runnable(){ //put normal onEnable code here
            @Override
            public void run(){
                protocolManager = ProtocolLibrary.getProtocolManager();
                SkinPacketHandler SkinHandler = new SkinPacketHandler();
                protocolManager.addPacketListener(SkinHandler.getAdapter());
                Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), SkinHandler);
                skinHandler = SkinHandler;
                CraftingHandler crafting = new CraftingHandler(Main.getPlugin());
                Forge.loadForm();
                if((Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens").isEnabled())){
                    //Check version
                    LogUtil.printErr("Citizens 2 not found!");
                    LogUtil.printErr("Shutting Down!");
                    Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
                    return;
                }
                if((Main.getPlugin().getServer().getPluginManager().getPlugin("ProtocolLib") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("ProtocolLib").isEnabled())){
                    //Check version
                    LogUtil.printErr("ProtocolLib not found!");
                    LogUtil.printErr("Shutting Down!");
                    Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
                    return;
                }
                if((Main.getPlugin().getServer().getPluginManager().getPlugin("BarAPI") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("BarAPI").isEnabled())){
                    //Check version
                    LogUtil.printErr("BarAPI not found!");
                    LogUtil.printErr("Shutting Down!");
                    Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
                    return;
                }
                if(Main.getPlugin().getConfig().getBoolean("decay")){
                    changes = new ChangeTracker(Plugin);
                }
                NPCs = new NpcManager();
                ModerationCommands moderation = new ModerationCommands();
                GeneralCommands general = new GeneralCommands();
                AdminCommands admin = new AdminCommands();
                MainMenuHandler mmh = new MainMenuHandler();
                if(Main.getPlugin().getConfig().getBoolean("debug.enabled")){
                    DebugCommands dbg = new DebugCommands(new File(Main.getPlugin().getConfig().getString("debug.buildfolder")));
                    Main.getPlugin().getCommand("fillplot").setExecutor(dbg);
                    Main.getPlugin().getCommand("setskins").setExecutor(dbg);
                    Main.getPlugin().getCommand("cleannpcs").setExecutor(dbg);
                    Main.getPlugin().getCommand("decayall").setExecutor(dbg);
                }
                Main.getPlugin().getCommand("menu").setExecutor(mmh);
                Main.getPlugin().getCommand("crash").setExecutor(admin);
                Main.getPlugin().getCommand("loadschem").setExecutor(admin);
                Main.getPlugin().getCommand("strack").setExecutor(admin);
                Main.getPlugin().getCommand("message").setExecutor(general);
                Main.getPlugin().getCommand("reply").setExecutor(general);
                Main.getPlugin().getCommand("info").setExecutor(general);
                Main.getPlugin().getCommand("help").setExecutor(general);
                Main.getPlugin().getCommand("list").setExecutor(general);
                Main.getPlugin().getCommand("self").setExecutor(general);
                Main.getPlugin().getCommand("is").setExecutor(general);
                Main.getPlugin().getCommand("where").setExecutor(moderation);
                Main.getPlugin().getCommand("ban").setExecutor(moderation);
                Main.getPlugin().getCommand("unban").setExecutor(moderation);
                Main.getPlugin().getCommand("ipban").setExecutor(moderation);
                Main.getPlugin().getCommand("unipban").setExecutor(moderation);
                Main.getPlugin().getCommand("kick").setExecutor(moderation);
                Main.getPlugin().getCommand("tmpban").setExecutor(moderation);
                Main.getPlugin().getCommand("invspy").setExecutor(moderation);
                Main.getPlugin().getCommand("fjail").setExecutor(moderation);
                Main.getPlugin().getCommand("vanish").setExecutor(moderation);
                Main.getPlugin().getCommand("uuid").setExecutor(moderation);
                Main.getPlugin().getCommand("broadcast").setExecutor(moderation);
                Main.getPlugin().getCommand("kingdom").setExecutor(new KingdomCommands());
                Main.getPlugin().getCommand("chat").setExecutor(new ChatHandler());
                Main.getPlugin().getCommand("party").setExecutor(new Party.PartyCommands());
                Main.getPlugin().getCommand("mute").setExecutor(new MuteCommand());
//                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), postServerLoad, 10);
//                Main.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(this, new DisguiseTask(this), 1200L, 1200L);
            }
        };
    
    @Getter
    private static Runnable postServerLoad = new Runnable(){ //called post server start (by 1/2 second)
        @Override
        public void run(){
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Builder.class));
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Archer.class));
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Cavalry.class));
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(General.class));
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Infantry.class));
        }
    };
    
    
    @Override
    public void onEnable(){ //Called pre-server enable
        Plugin = this;
        this.saveDefaultConfig();
        World mainworld = Bukkit.getWorld(this.getConfig().getString("MainWorld"));
        MultiBlockHandler mbh = new MultiBlockHandler();
        Bukkit.getPluginManager().registerEvents(mbh, this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveHandler(), this);
        Bukkit.getPluginManager().registerEvents(new StorageHandler(), this);
        Bukkit.getPluginManager().registerEvents(new BuildingHandler(), this);
        Bukkit.getPluginManager().registerEvents(new WaterHandler(), this);
        Bukkit.getPluginManager().registerEvents(new PlotProtectionHandler(), this);
        Bukkit.getPluginManager().registerEvents(new PlantGrowthHandler(), this);
        Bukkit.getPluginManager().registerEvents(new ChestGUIHandler(), this);
        Bukkit.getPluginManager().registerEvents(new HotbarHandler(), this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, onServerLoad);
        setupDatabase();
//        RedisManager RM = new RedisManager();
    }
    
    @Override
    public void onDisable(){
        
    }
    
    /**
     * Kingdoms
     *  |
     *  |--prefabs
     *  |  |
     * 
     */
    
    public static void setupDatabase(){
        File plugin = Main.getPlugin().getDataFolder();
        for(String s : new String[] {"prefabs", "playerdata", "multiblock", "savedata"}){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + s).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + s).mkdir();
            }
        }
        for(Class c : Municipality.getStructureClasses()){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).mkdir();
            }
        }
        for(Class c : MultiBlock.getMultiBlockClasses()){
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
