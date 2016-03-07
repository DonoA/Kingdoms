/*
 * This file is part of Kingdom.
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
package io.dallen.Kingdom;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.dallen.Kingdom.Handlers.ChatHandler;
import io.dallen.Kingdom.Handlers.Party;
import io.dallen.Kingdom.Util.LogUtil;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    private static Runnable onServerLoad = new Runnable(){ //put normal onEnable code here
            @Override
            public void run(){
                if ((Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("Citizens").isEnabled())){
                    LogUtil.printErr("Citizens 2 not found!");
                    Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
                    return;
                }
                NPCs = new NpcManager();
                Main.getPlugin().getCommand("kingdom").setExecutor(new KingdomCommands());
                Main.getPlugin().getCommand("chat").setExecutor(new ChatHandler());
                Main.getPlugin().getCommand("party").setExecutor(new Party());
//                Main.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(this, new DisguiseTask(this), 1200L, 1200L);
            }
        };
    
    @Override
    public void onEnable(){ //Called pre-server enable
        Plugin = this;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, onServerLoad);
    }
    
    @Override
    public void onDisable(){
        
    }
}
