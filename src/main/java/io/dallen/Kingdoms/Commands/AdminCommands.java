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
package io.dallen.Kingdoms.Commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Util.PermissionManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class AdminCommands implements CommandExecutor{
    
    @Getter
    private int newUsers = 0;
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(sender.hasPermission(PermissionManager.getOwnerPermission())){
            if(cmd.getName().equalsIgnoreCase("strack") && args.length > 0){
                if(args[0].equalsIgnoreCase("load")){
                    
                }else if(args[0].equalsIgnoreCase("mem")){
                    long used = (Main.getRuntime().totalMemory() - Main.getRuntime().freeMemory())/Main.getRuntime().totalMemory();
                }else if(args[0].equalsIgnoreCase("users")){
                    
                }else if(args[0].equalsIgnoreCase("dbSize")){
                    
                }else if(args[0].equalsIgnoreCase("newUsers")){
                    
                }else if(args[0].equalsIgnoreCase("kingdoms")){
                    sender.sendMessage("Current Kingdoms:");
                    sender.sendMessage("======================");
                    for(String f : Kingdom.getKingdoms().keySet()){
                        sender.sendMessage(" - " + f);
                    }
                }else if(args[0].equalsIgnoreCase("covens")){
                    
                }else if(args[0].equalsIgnoreCase("roles")){
                    
                }else if(args[0].equalsIgnoreCase("levels")){
                    
                }
            }else if(cmd.getName().equalsIgnoreCase("crash") && args.length > 0){
                if(Bukkit.getPlayer(args[0]) != null){
                    Player p = Bukkit.getPlayer(args[0]);
                    PacketContainer CrashPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.RESPAWN, false);
//                    CrashPacket.getIntegers().write(0, 3);
//                    CrashPacket.getDifficulties().write(0, EnumWrappers.Difficulty.EASY);
//                    CrashPacket.getGameModes().write(0, NativeGameMode.SPECTATOR);
//                    CrashPacket.getWorldTypeModifier().write(0, WorldType.NORMAL);
                    
                    
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(p, CrashPacket);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }else{
                    sender.sendMessage("Player not found!");
                }
            }else if(cmd.getName().equalsIgnoreCase("loadshem") && args.length > 0){
                if((Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").isEnabled())){
                    sender.sendMessage("World Edit not found!");
                    return true;
                }
                File oldSchem = new File(Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder() + 
                                         DBmanager.getFileSep() + "schematics" + DBmanager.getFileSep() + args[0] + ".schematic");
                File newSchem = new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + args[0] + ".schematic");
                if(oldSchem.exists()){
                    try{
                        if(!newSchem.exists()) {
                            newSchem.createNewFile();
                        }
                        FileChannel source = null;
                        FileChannel destination = null;
                        try {
                            source = new FileInputStream(oldSchem).getChannel();
                            destination = new FileOutputStream(newSchem).getChannel();
                            destination.transferFrom(source, 0, source.size());
                        }finally{
                            if(source != null) {
                                source.close();
                            }
                            if(destination != null) {
                                destination.close();
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AdminCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    sender.sendMessage("Schematic not found!");
                }
            }
            return false;
        }else{
            sender.sendMessage("Not allowed!");
            return true;
        }
    }
}
