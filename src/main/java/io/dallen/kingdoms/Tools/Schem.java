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
package io.dallen.kingdoms.Tools;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.SchematicLibrary;
import io.dallen.utils.BukkitUtils.ItemUtil;
import io.dallen.utils.Storage.Blueprint;
import io.dallen.utils.Storage.Blueprint.BlueBlock;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Schem {
    
    private HashMap<Player, SchemSelect> openSchems = new HashMap<>();
    
    private final StringPrompt getName = new StringPrompt(){
        @Override
        public String getPromptText(ConversationContext context) {
            if(context.getSessionData("name") == null){
                return "Name the new schem";
            }else{
                return "That name is taken, try again";
            }
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData("name", input);
            if(SchematicLibrary.exists(input)){
                return getName;
            }else{
                return getType;
            }
        }
    };
    
    private final StringPrompt getType = new StringPrompt(){
        @Override
        public String getPromptText(ConversationContext context) {
            return (context.getSessionData("help") != null ? context.getSessionData("help") + "\n\n" : "") + "Set the type for the new schematic (!help for known list)";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("!help")){
                context.setSessionData("help", "Palace, Home, Tower");
                return getType;
            }else{
                context.setSessionData("type", input);
                return getDir;
            }
        }
    };
    
    private final StringPrompt getDir = new StringPrompt(){
        @Override
        public String getPromptText(ConversationContext context) {
            return (context.getSessionData("help") != null ? context.getSessionData("help") + "\n\n" : "") + "Set the driection of schem (!help for known list)";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("!help")){
                context.setSessionData("help", "North, South, East, West");
                return getType;
            }else{
                SchemSelect ss = openSchems.get((Player) context.getForWhom());
                BlueBlock[][][] blocks = new BlueBlock[Math.abs(ss.p1.getBlockX()-ss.p2.getBlockX())]
                                                      [Math.abs(ss.p1.getBlockY()-ss.p2.getBlockY())]
                                                      [Math.abs(ss.p1.getBlockZ()-ss.p2.getBlockZ())];
                int maxX = Math.max(ss.p1.getBlockX(), ss.p2.getBlockX());
                int maxY = Math.max(ss.p1.getBlockY(), ss.p2.getBlockY());
                int maxZ = Math.max(ss.p1.getBlockZ(), ss.p2.getBlockZ());
                for(int x = Math.min(ss.p1.getBlockX(), ss.p2.getBlockX()); x <= maxX; x++){
                    for(int y = Math.min(ss.p1.getBlockY(), ss.p2.getBlockY()); y <= maxY; y++){
                        for(int z = Math.min(ss.p1.getBlockZ(), ss.p2.getBlockZ()); z <= maxZ; z++){
                            blocks[x][y][z] = new BlueBlock(ss.p1.getWorld().getBlockAt(new Location(ss.p1.getWorld(), x, y, z)));
                        }
                    }
                }
                boolean failed = false;
                try {
                    SchematicLibrary.save(new Blueprint(blocks), (String) context.getSessionData("name"), (String) context.getSessionData("type"), input);
                } catch (IOException ex) {
                    failed = true;
                    Logger.getLogger(Schem.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if(failed){
                        context.getForWhom().sendRawMessage("Failed to save " + (String) context.getSessionData("name"));
                    }else{
                        context.getForWhom().sendRawMessage("Saved " + (String) context.getSessionData("name"));
                    }
                }
                openSchems.remove((Player) context.getForWhom());
                return Prompt.END_OF_CONVERSATION;
            }
        }
    };
    
    private final ConversationFactory conversationFactory = new ConversationFactory(Kingdoms.getPlugin()){{
        withModality(true);
        withEscapeSequence("!exit");
        withPrefix(new ConversationPrefix(){
            @Override
            public String getPrefix(ConversationContext context) {
                return context.getSessionData("name") == null ? "[New Schem]" : "["+ context.getSessionData("name") + "]";
            }
        });
        withFirstPrompt(getName);
        withTimeout(60);
        thatExcludesNonPlayersWithMessage("You must be a player to send this command");
    }};
    
    public class Commands implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
            if(cmd.getName().equalsIgnoreCase("schem") && sender instanceof Player){
                ItemStack wand = ItemUtil.setItemNameAndLore(Material.WOOD_AXE, "Schem Wand", "Left - p1", "Right - p2", "Drop - save");
                ((Player) sender).getInventory().addItem(wand);
                openSchems.put(((Player) sender), new SchemSelect());
            }
            return true;
        }
    }

    public class Events implements Listener {
        
        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent e){
            if(openSchems.containsKey(e.getPlayer()) && e.hasItem() && e.getItem().hasItemMeta() && 
                    e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Schem Wand")){
                if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                    openSchems.get(e.getPlayer()).p2 = e.getClickedBlock().getLocation();
                    e.getPlayer().sendMessage("Set p2");
                    e.setCancelled(true);
                }else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                    openSchems.get(e.getPlayer()).p1 = e.getClickedBlock().getLocation();
                    e.getPlayer().sendMessage("Set p1");
                    e.setCancelled(true);
                }
            }
        }
        
        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent e){
            if(openSchems.containsKey(e.getPlayer()) && e.getItemDrop().getItemStack().hasItemMeta() && 
                    e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("Schem Wand")){
                e.getItemDrop().remove();
                conversationFactory.buildConversation((Conversable) e.getPlayer()).begin();
            }
        }
        
    }
    
    public static class SchemSelect {
        public Location p1;
        public Location p2;
    }
    
}
