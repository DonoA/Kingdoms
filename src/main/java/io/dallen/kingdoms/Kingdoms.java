/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of Kingdoms for the Morphics Network.
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
package io.dallen.kingdoms;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.dallen.kingdoms.Tools.Creator;
import io.dallen.kingdoms.Tools.Schem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Donovan Allen
 */
public class Kingdoms extends JavaPlugin {

    @Getter
    private static Kingdoms plugin;

    @Getter
    private static ProtocolManager protocolManager;
    
    @Getter
    private static ThreadHandler threadHandler;
    
    @Override
    public void onEnable() {
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        threadHandler = new ThreadHandler();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new ThreadHandler.BuilderThread(), 5, 1);
        
        Schem schemTool = new Schem();
        this.getCommand("schem").setExecutor(schemTool.new Commands());
        Bukkit.getPluginManager().registerEvents(schemTool.new Events(), plugin);
        
        Creator createTool = new Creator();
        this.getCommand("spawnKingdom").setExecutor(createTool.new Commands());
    }
}
