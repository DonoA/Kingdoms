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
package io.dallen.kingdoms.essentials.Commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.dallen.kingdoms.core.Kingdom;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.essentials.KingdomsEssentials;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEvent;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEventHandler;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import io.dallen.kingdoms.utilities.Utils.PermissionManager;
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
public class AdminCommands implements CommandExecutor, OptionClickEventHandler {

    @Getter
    private int newUsers = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (sender.hasPermission(PermissionManager.getOwnerPermission())) {
            if (cmd.getName().equalsIgnoreCase("strack") && args.length > 0) {
                if (args[0].equalsIgnoreCase("load")) {
                    Bukkit.dispatchCommand(sender, "tps");
                } else if (args[0].equalsIgnoreCase("mem")) {
//                    long used = (KingdomsEssentials.getRuntime().totalMemory() - KingdomsEssentials.getRuntime().freeMemory())/KingdomsEssentials.getRuntime().totalMemory();
                } else if (args[0].equalsIgnoreCase("users")) {

                } else if (args[0].equalsIgnoreCase("dbSize")) {

                } else if (args[0].equalsIgnoreCase("newUsers")) {

                } else if (args[0].equalsIgnoreCase("kingdoms")) {
                    sender.sendMessage("Current Kingdoms:");
                    sender.sendMessage("======================");
                    for (Kingdom k : KingdomsCore.getKingdoms()) {
                        sender.sendMessage(" - " + k.getName());
                    }
                } else if (args[0].equalsIgnoreCase("covens")) {

                } else if (args[0].equalsIgnoreCase("roles")) {

                } else if (args[0].equalsIgnoreCase("levels")) {

                }
            }
            if (cmd.getName().equalsIgnoreCase("fillplot")) {
                Player plr = (Player) sender;
                plr.sendMessage("To start a building contruction type the schematic name and build speed in chat");
                Plot p = Plot.inPlot(plr.getLocation());
//            BuildingHandler.StringInput in = new BuildingHandler.StringInput("buildConst", p, new BuildContract((Player) sender,
//                    new ChestGUI.OptionClickEvent((Player) sender, -1, null, 
//                            "Build", null, null)));
//            BuildingHandler.getOpenInputs().put(plr.getName(), in);
            } else if (cmd.getName().equalsIgnoreCase("crash") && args.length > 0) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Player p = Bukkit.getPlayer(args[0]);
                    PacketContainer CrashPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.RESPAWN, false);
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(p, CrashPacket);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage("Player not found!");
                }
            } else if (cmd.getName().equalsIgnoreCase("editschem") && sender instanceof Player) {
                new ChestGUI("Edit Schematics", InventoryType.HOPPER, this) {
                    {
                        setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Tranfer Schem");
                        setOption(2, new ItemStack(Material.ENCHANTED_BOOK), "Move Schem");
                        setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Delete Schem");
                        setMenuData(KingdomsEssentials.getPlugin().getDataFolder());
                    }
                }.sendMenu((Player) sender);
                return true;
            }
            return false;
        } else {
            sender.sendMessage("Not allowed!");
            return true;
        }
    }

    @Override
    public void onOptionClick(OptionClickEvent e) {
        if (e.getMenuName().equals("Edit Schematics")) {
            if (e.getName().equals("Tranfer Schem")) {
                if ((KingdomsEssentials.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit") == null)
                        || (!KingdomsEssentials.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").isEnabled())) {
                    e.getPlayer().sendMessage("World Edit not found!");
                    return;
                }
                ChestGUI schems = new ChestGUI("Select Schematic to Transfer", 54, this);
                int Size = 0;
                for (File f : new File(KingdomsEssentials.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder()
                        + DBmanager.getFileSep() + "schematics").listFiles()) {
                    if (!new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep()
                            + f.getName()).exists() && Size < 54 && f.isFile() && f.getName().contains(".schematic")) {
                        schems.setOption(Size, new ItemStack(Material.PAPER), f.getName());
                        Size++;;
                    }
                }
                schems.setOption(Size, new ItemStack(Material.ENCHANTED_BOOK), "Move All");
                Size++;;
                e.setNext(schems);
            } else if (e.getName().equals("Move Schem")) {
                ChestGUI schems = new ChestGUI("Select Schematic to Move", 54, this);
                int Size = 0;
                for (File f : new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()) {
                    if (Size < 54 && f.isDirectory()) {
                        schems.setOption(Size, new ItemStack(Material.BOOK), f.getName());
                        Size++;;
                    }
                }
                for (File f : new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()) {
                    if (Size < 54 && f.isFile()) {
                        schems.setOption(Size, new ItemStack(Material.PAPER), f.getName());
                        Size++;;
                    }
                }
                schems.setMenuData(new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs"));
                e.setNext(schems);
            } else if (e.getName().equals("Delete Schem")) {
                ChestGUI schems = new ChestGUI("Select Schematic to Delete", 54, this);
                int Size = 0;
                for (File f : new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()) {
                    if (Size < 54 && f.isDirectory()) {
                        schems.setOption(Size, new ItemStack(Material.BOOK), f.getName());
                        Size++;;
                    }
                }
                for (File f : new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs").listFiles()) {
                    if (Size < 54 && f.isFile()) {
                        schems.setOption(Size, new ItemStack(Material.PAPER), f.getName());
                        Size++;;
                    }
                }
                schems.setMenuData(new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs"));
                e.setNext(schems);
            }
        } else if (e.getMenuName().equals("Select Schematic to Transfer")) {
            if (e.getName().equals("Move All")) {
                for (File f : new File(KingdomsEssentials.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder()
                        + DBmanager.getFileSep() + "schematics").listFiles()) {
                    File newSchem = new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + f.getName());
                    try {
                        if (!newSchem.exists()) {
                            newSchem.createNewFile();
                        }
                        FileChannel source = null;
                        FileChannel destination = null;
                        try {
                            source = new FileInputStream(f).getChannel();
                            destination = new FileOutputStream(newSchem).getChannel();
                            destination.transferFrom(source, 0, source.size());
                        } finally {
                            if (source != null) {
                                source.close();
                            }
                            if (destination != null) {
                                destination.close();
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AdminCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    e.getPlayer().sendMessage("Files moved");
                }
            } else {
                File oldSchem = new File(KingdomsEssentials.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder()
                        + DBmanager.getFileSep() + "schematics" + DBmanager.getFileSep() + e.getName());
                File newSchem = new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + e.getName());
                if (oldSchem.exists()) {
                    try {
                        if (!newSchem.exists()) {
                            newSchem.createNewFile();
                        }
                        FileChannel source = null;
                        FileChannel destination = null;
                        try {
                            source = new FileInputStream(oldSchem).getChannel();
                            destination = new FileOutputStream(newSchem).getChannel();
                            destination.transferFrom(source, 0, source.size());
                        } finally {
                            if (source != null) {
                                source.close();
                            }
                            if (destination != null) {
                                destination.close();
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AdminCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    e.getPlayer().sendMessage("File moved");
                } else {
                    e.getPlayer().sendMessage("Schematic not found!");
                }
            }
        } else if (e.getMenuName().equals("Select Schematic to Move")) {
            File selected = new File(e.getMenuData().toString() + DBmanager.getFileSep() + e.getName());
            if (selected.isDirectory()) {
                ChestGUI schems = new ChestGUI("Select Schematic to Move", 54, this);
                int Size = 0;
                for (File f : selected.listFiles()) {
                    if (Size < 54 && f.isDirectory()) {
                        schems.setOption(Size, new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        Size++;
                    }
                }
                for (File f : selected.listFiles()) {
                    if (Size < 54 && f.isFile()) {
                        schems.setOption(Size, new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        Size++;
                    }
                }
                schems.setMenuData(selected);
                e.setNext(schems);
            } else if (selected.isFile()) {
                ChestGUI schems = new ChestGUI("Select Schematic Destination", 54, this);
                int Size = 0;
                for (File f : selected.getParentFile().listFiles()) {
                    if (Size < 54 && f.isDirectory()) {
                        schems.setOption(Size, new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        Size++;
                    }
                }
                schems.setOption(Size, new ItemStack(Material.ENCHANTED_BOOK), "Current Directory");
                schems.setMenuData(selected);
                e.setNext(schems);
            }
        } else if (e.getMenuName().equals("Select Schematic to Delete")) {
            File selected = new File(e.getMenuData().toString() + DBmanager.getFileSep() + e.getName());
            if (selected.isDirectory()) {
                ChestGUI schems = new ChestGUI("Select Schematic to Delete", 54, this);
                int Size = 0;
                for (File f : selected.listFiles()) {
                    if (Size < 54 && f.isDirectory()) {
                        schems.setOption(Size, new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        Size++;;
                    }
                }
                for (File f : selected.listFiles()) {
                    if (Size < 54 && f.isFile()) {
                        schems.setOption(Size, new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                        Size++;;
                    }
                }
                schems.setMenuData(selected);
                e.setNext(schems);
            } else if (selected.isFile()) {
                selected.delete();
            }
        } else if (e.getMenuName().equals("Select Schematic Destination")) {
            File oldSchem = (File) e.getMenuData();
            File newSchem;
            if (e.getName().equals("Current Directory")) {
                newSchem = new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + oldSchem.getName());
            } else {
                newSchem = new File(KingdomsEssentials.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + e.getName() + DBmanager.getFileSep() + oldSchem.getName());
            }
            if (oldSchem.exists()) {
                try {
                    if (!newSchem.exists()) {
                        newSchem.createNewFile();
                    }
                    FileChannel source = null;
                    FileChannel destination = null;
                    try {
                        source = new FileInputStream(oldSchem).getChannel();
                        destination = new FileOutputStream(newSchem).getChannel();
                        destination.transferFrom(source, 0, source.size());
                    } finally {
                        if (source != null) {
                            source.close();
                        }
                        if (destination != null) {
                            destination.close();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AdminCommands.class.getName()).log(Level.SEVERE, null, ex);
                }
                e.getPlayer().sendMessage("File moved");
            } else {
                e.getPlayer().sendMessage("Schematic not found!");
            }
        }

    }

}
