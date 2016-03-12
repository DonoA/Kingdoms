/*
 * This file is part of Kingdoms.
 * 
 * Kingdom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdom.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.dallen.Kingdoms.Commands.AdminCommands;
import io.dallen.Kingdoms.Commands.GeneralCommands;
import io.dallen.Kingdoms.Commands.ModerationCommands;
import io.dallen.Kingdoms.NPCs.NpcManager;
import io.dallen.Kingdoms.Handlers.ChatHandler;
import io.dallen.Kingdoms.Handlers.Party;
import io.dallen.Kingdoms.MenuHandlers.MainMenuHandler;
import io.dallen.Kingdoms.Terrain.KingdomTerrainGeneration;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Util.MuteCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.BanList;
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
    
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Getter
    private static Runnable onServerLoad = new Runnable(){ //put normal onEnable code here
            @Override
            public void run(){
                if ((Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens").isEnabled())){
                    LogUtil.printErr("Citizens 2 not found!");
                    LogUtil.printErr("Shutting Down!");
                    Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
                    return;
                }
                NPCs = new NpcManager();
                ModerationCommands moderation = new ModerationCommands();
                GeneralCommands general = new GeneralCommands();
                AdminCommands admin = new AdminCommands();
                Main.getPlugin().getCommand("menu").setExecutor(new MainMenuHandler());
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, onServerLoad);
    }
    
    @Override
    public void onDisable(){
        
    }
}
