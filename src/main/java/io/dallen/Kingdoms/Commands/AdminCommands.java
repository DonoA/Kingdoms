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
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
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
import org.bukkit.Material;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class AdminCommands implements CommandExecutor, OptionClickEventHandler{
    
    @Getter
    private int newUsers = 0;
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(sender.hasPermission(PermissionManager.getOwnerPermission())){
            if(cmd.getName().equalsIgnoreCase("strack") && args.length > 0){
                if(args[0].equalsIgnoreCase("load")){
                    Bukkit.dispatchCommand(sender, "tps");
                }else if(args[0].equalsIgnoreCase("mem")){
                    long used = (Main.getRuntime().totalMemory() - Main.getRuntime().freeMemory())/Main.getRuntime().totalMemory();
                }else if(args[0].equalsIgnoreCase("users")){
                    
                }else if(args[0].equalsIgnoreCase("dbSize")){
                    
                }else if(args[0].equalsIgnoreCase("newUsers")){
                    
                }else if(args[0].equalsIgnoreCase("kingdoms")){
                    sender.sendMessage("Current Kingdoms:");
                    sender.sendMessage("======================");
                    for(Kingdom k : Main.getKingdoms()){
                        sender.sendMessage(" - " + k.getName());
                    }
                }else if(args[0].equalsIgnoreCase("covens")){
                    
                }else if(args[0].equalsIgnoreCase("roles")){
                    
                }else if(args[0].equalsIgnoreCase("levels")){
                    
                }
            }else if(cmd.getName().equalsIgnoreCase("crash") && args.length > 0){
                if(Bukkit.getPlayer(args[0]) != null){
                    Player p = Bukkit.getPlayer(args[0]);
                    PacketContainer CrashPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.RESPAWN, false);
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(p, CrashPacket);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }else{
                    sender.sendMessage("Player not found!");
                }
            }else if(cmd.getName().equalsIgnoreCase("editschem") && args.length > 0 && sender instanceof Player){
                
                new ChestGUI("Edit Schematics", InventoryType.HOPPER, this){{
                    setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Tranfer Schem");
                    setOption(2, new ItemStack(Material.ENCHANTED_BOOK), "Move Schem");
                    setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Delete Schem");
                }}.sendMenu((Player) sender);
                return true;
            }
            return false;
        }else{
            sender.sendMessage("Not allowed!");
            return true;
        }
    }
    
    @Override
    public void onOptionClick(OptionClickEvent e){
        if(e.getMenuName().equals("Edit Schematics")){
            if(e.getName().equals("Tranfer Schem")){
                if((Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit") == null) || 
                    (!Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").isEnabled())){
                    e.getPlayer().sendMessage("World Edit not found!");
                    return;
                }
                ChestGUI schems = new ChestGUI("Select Schematic to Transfer", 0, this);
                for(File f : new File(Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder() + 
                                         DBmanager.getFileSep() + "schematics").listFiles()){
                    if(!new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() 
                            + f.getName()).exists() && schems.getSize() < 54 && f.isFile() && f.getName().contains(".schematic")){
                        schems.setOption(schems.getSize(), new ItemStack(Material.PAPER), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                schems.setOption(schems.getSize(), new ItemStack(Material.ENCHANTED_BOOK), "Move All");
                schems.setSize(schems.getSize() + 1);
                e.setNext(schems);
            }else if(e.getName().equals("Move Schem")){
                ChestGUI schems = new ChestGUI("Select Schematic to Move", 0, this);
                for(File f : new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()){
                    if(schems.getSize() < 54 && f.isDirectory()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.BOOK), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                for(File f : new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()){
                    if(schems.getSize() < 54 && f.isFile()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.PAPER), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                schems.setMenuData(new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs"));
                e.setNext(schems);
            }else if(e.getName().equals("Delete Schem")){
                ChestGUI schems = new ChestGUI("Select Schematic to Delete", 0, this);
                for(File f : new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()){
                    if(schems.getSize() < 54 && f.isDirectory()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.BOOK), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                for(File f : new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()){
                    if(schems.getSize() < 54 && f.isFile()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.PAPER), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                schems.setMenuData(new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs"));
                e.setNext(schems);
            }
        }else if(e.getMenuName().equals("Select Schematic to Transfer")){
            if(e.getName().equals("Move All")){
                for(File f : new File(Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder() + 
                                     DBmanager.getFileSep() + "schematics").listFiles()){
                    File newSchem = new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + f.getName());
                    try{
                        if(!newSchem.exists()) {
                            newSchem.createNewFile();
                        }
                        FileChannel source = null;
                        FileChannel destination = null;
                        try {
                            source = new FileInputStream(f).getChannel();
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
                    e.getPlayer().sendMessage("Files moved");
                }
            }else{
                File oldSchem = new File(Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder() + 
                                         DBmanager.getFileSep() + "schematics" + DBmanager.getFileSep() + e.getName());
                File newSchem = new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + e.getName());
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
                    e.getPlayer().sendMessage("File moved");
                }else{
                    e.getPlayer().sendMessage("Schematic not found!");
                }
            }
        }else if(e.getMenuName().equals("Select Schematic to Move")){
            File selected = new File(e.getMenuData().toString() + DBmanager.getFileSep() + e.getName());
            if(selected.isDirectory()){
                ChestGUI schems = new ChestGUI("Select Schematic to Move", 0, this);
                for(File f : selected.listFiles()){
                    if(schems.getSize() < 54 && f.isDirectory()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                for(File f : selected.listFiles()){
                    if(schems.getSize() < 54 && f.isFile()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                schems.setMenuData(selected);
                e.setNext(schems);
            }else if(selected.isFile()){
                ChestGUI schems = new ChestGUI("Select Schematic Destination", 0, this);
                for(File f : selected.listFiles()){
                    if(schems.getSize() < 54 && f.isDirectory()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                schems.setOption(schems.getSize(), new ItemStack(Material.ENCHANTED_BOOK), "Current Directory");
                schems.setSize(schems.getSize() + 1);
                schems.setMenuData(selected);
                e.setNext(schems);
            }
        }else if(e.getMenuName().equals("Select Schematic to Delete")){
            File selected = new File(e.getMenuData().toString() + DBmanager.getFileSep() + e.getName());
            if(selected.isDirectory()){
                ChestGUI schems = new ChestGUI("Select Schematic to Delete", 0, this);
                for(File f : selected.listFiles()){
                    if(schems.getSize() < 54 && f.isDirectory()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                for(File f : selected.listFiles()){
                    if(schems.getSize() < 54 && f.isFile()){
                        schems.setOption(schems.getSize(), new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        schems.setSize(schems.getSize() + 1);
                    }
                }
                schems.setMenuData(selected);
                e.setNext(schems);
            }else if(selected.isFile()){
                selected.delete();
            }
        }else if(e.getMenuName().equals("Select Schematic Destination")){
            File oldSchem = (File) e.getMenuData();
            File newSchem;
            if(e.getName().equals("Current Directory")){
                newSchem = new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs");
            }else{
                newSchem = new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + e.getName());
            }
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
                e.getPlayer().sendMessage("File moved");
            }else{
                e.getPlayer().sendMessage("Schematic not found!");
            }
        }
            
    }
    
}
