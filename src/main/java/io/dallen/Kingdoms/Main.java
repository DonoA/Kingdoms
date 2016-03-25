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
import io.dallen.Kingdoms.Terrain.KingdomTerrainGeneration;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Commands.MuteCommand;
import io.dallen.Kingdoms.Util.NBTmanager;
import io.dallen.Kingdoms.Util.RedisManager;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import lombok.Getter;
import lombok.Setter;
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
    
    @Getter @Setter
    private static KingdomTerrainGeneration Overworld;
    
    @Getter
    private static ProtocolManager protocolManager;
    
    @Getter
    private static Runtime runtime = Runtime.getRuntime();
    
    @Getter
    private static ChangeTracker changes;
    
    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Getter
    private static Runnable onServerLoad = new Runnable(){ //put normal onEnable code here
            @Override
            public void run(){
                if((Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens").isEnabled())){
                    LogUtil.printErr("Citizens 2 not found!");
                    LogUtil.printErr("Shutting Down!");
                    Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
                    return;
                }
                changes = new ChangeTracker(Plugin);
                NPCs = new NpcManager();
                ModerationCommands moderation = new ModerationCommands();
                GeneralCommands general = new GeneralCommands();
                AdminCommands admin = new AdminCommands();
                MainMenuHandler mmh = new MainMenuHandler();
                DebugCommands dbg = new DebugCommands();
                Main.getPlugin().getCommand("fillplot").setExecutor(dbg);
                Main.getPlugin().getCommand("menu").setExecutor(mmh);
                Main.getPlugin().getCommand("crash").setExecutor(admin);
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
                Main.getPlugin().getCommand("fjail").setExecutor(moderation);
                Main.getPlugin().getCommand("uuid").setExecutor(moderation);
                Main.getPlugin().getCommand("broadcast").setExecutor(moderation);
                Main.getPlugin().getCommand("kingdom").setExecutor(new KingdomCommands());
                Main.getPlugin().getCommand("chat").setExecutor(new ChatHandler());
                Main.getPlugin().getCommand("party").setExecutor(new Party.PartyCommands());
                Main.getPlugin().getCommand("mute").setExecutor(new MuteCommand());
//                Main.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(this, new DisguiseTask(this), 1200L, 1200L);
            }
        };
    
    @Override
    public void onEnable(){ //Called pre-server enable
        Plugin = this;
        this.saveDefaultConfig();
        World mainworld = Bukkit.getWorld(this.getConfig().getString("MainWorld"));
        Overworld = new KingdomTerrainGeneration(mainworld);
        MultiBlockHandler mbh = new MultiBlockHandler();
        Bukkit.getPluginManager().registerEvents(mbh, this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveHandler(), this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, onServerLoad);
        
        RedisManager RM = new RedisManager();
    }
    
    @Override
    public void onDisable(){
        
    }
}
