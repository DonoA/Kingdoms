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
package io.dallen.Kingdoms.Commands;

import io.dallen.Kingdoms.Handlers.MultiBlockHandler;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.DBmanager;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan Allen
 */
public class DebugCommands implements CommandExecutor{
    
    private PluginUpdateThread update;
    
    public DebugCommands(File build){
        if(Main.getPlugin().getConfig().getBoolean("debug.autorestart")){
            update = new PluginUpdateThread(build);
            update.start();
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("fillplot")){
            Player plr = (Player) sender;
            plr.sendMessage("To start a building contruction type the schematic name and build speed in chat");
            Plot p = Plot.inPlot(plr.getLocation());
            MultiBlockHandler.StringInput in = new MultiBlockHandler.StringInput("buildConst", p);
            MultiBlockHandler.getOpenInputs().put(plr.getName(), in);
        }else if(cmd.getName().equalsIgnoreCase("setskins")){
            Main.getSkinHandler().setSkin(args[0]);
            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), Main.getSkinHandler());
        }
        return true;
    }
    
    public static class PluginUpdateThread extends Thread{
        
        private final File buildFolder;
        
        private final File buildTests;
        
        private final File buildFile;
             
        public PluginUpdateThread(File build){
            this.buildFolder = build;
            this.buildFile = new File(build.getAbsolutePath() + DBmanager.getFileSep() + "Kingdoms-1.0-SNAPSHOT.jar");
            this.buildTests = new File(build.getAbsolutePath() + DBmanager.getFileSep() + "test-classes");
        }
        
        @Override
        public void run(){
            while(true){
               if(buildFile.exists() && buildTests.exists() && buildTests.isDirectory()){
                   Bukkit.shutdown();
               }
            }
        }
        
    }
}
