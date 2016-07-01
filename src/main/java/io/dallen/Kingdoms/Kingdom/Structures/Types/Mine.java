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
package io.dallen.Kingdoms.Kingdom.Structures.Types;

import io.dallen.Kingdoms.Handlers.BuildingHandler;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.NPCs.Traits.Miner;
import io.dallen.Kingdoms.Util.Annotations.SaveData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.LocationUtil;
import io.dallen.Kingdoms.Util.LogUtil;
import java.awt.Rectangle;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Mine extends Plot implements Storage{
    
    @Getter @Setter @SaveData
    private boolean digging;
    
    @Getter @Setter @SaveData
    private BuildingVault Storage;
    
    private Location currentBlock;
    
    private MineTask mineTask = null;
    
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;
    
    public Mine(Plot p) {
        super(p);
        digging = false;
        EditPlot = new ChestGUI("Mine", 2, new MenuHandler()){{
            setOption(0*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Start Mining");
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }};
        Storage = new BuildingVault(18,18*64, this);
    }
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.sendMenu(p);
    }
    
    @Override
    public boolean interact(PlayerInteractEvent e){
        if(e.getClickedBlock().getType().equals(Material.CHEST)){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(Storage.CanOpen(e.getPlayer())){
                    Storage.SendToPlayer(e.getPlayer());
                    return true;
                }
            }else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                if(e.hasItem() && this.hasSpace()){
                    if(Storage.addItem(e.getItem())){
                        e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean hasSpace(){
        return Storage.getFullSlots() < Storage.getUniqueSize() && Storage.getAmountFull() < Storage.getCapacity();
    }
    
    @Override
    public boolean supplyNPC(NPC npc){
        return true;
    }
    
    public class MenuHandler implements ChestGUI.OptionClickEventHandler{
        
        @Override
        public void onOptionClick(ChestGUI.OptionClickEvent e){
            if(e.getMenuName().equals(EditPlot.getName())){
                if(e.getName().equals("Start Mining")){
                    digging = !digging;
                    EditPlot.setOption(0*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Stop Mining");
                    if(mineTask == null){
                        mineTask = new MineTask(new Location(getCenter().getWorld(), getCenter().getX() - getWidth()/2  + (getWidth() % 2 == 0 ? 1 : 0),
                        getCenter().getBlockY(), getCenter().getBlockZ() - getLength()/2 + (getLength() % 2 == 0 ? 1 : 0)), 10);
                    }
                }else if(e.getName().equals("Stop Mining")){
                    digging = !digging;
                    EditPlot.setOption(0*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Start Mining");
                }else{
                    BuildingHandler.chestBuildOptions(e, BuildMenu, Mine.this);
                }
            }else if(e.getMenuName().equals(BuildMenu.getName())){
                BuildingHandler.getBuildChestHandler().onOptionClick(e);
            }
        }
    }
    
    public class MineTask implements Runnable{
        
        private int x = 0;
        
        private int y = -1; 
        
        private int z = 0;
        
        private Rectangle currentHole;
        
        private Location startCorner;
        
        private NPC Miner;
        
        private boolean finished = false;
        
        private int step = 64;
        
        public MineTask(Location start, int speed){
            LogUtil.printDebug(LocationUtil.asPoint(Mine.this.getCenter()));
            currentHole = new Rectangle(-(getWidth()/2  + (getWidth() % 2 == 0 ? 1 : 0)), -(getLength()/2 + (getLength() % 2 == 0 ? 1 : 0)), 
                    getWidth()/2  + (getWidth() % 2 == 0 ? 1 : 0), getLength()/2 + (getLength() % 2 == 0 ? 1 : 0));
            Miner = Main.getNPCs().spawnBuilder("BingRazor", Mine.this.getCenter());
            Miner.getNavigator().setTarget(start);
            this.startCorner = start;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this, speed, speed);
        }
        
        @Override
        public void run(){
            if(!Miner.getNavigator().isNavigating() && Mine.this.digging){
                if(!finished){
                    if(step == 0){
                        Miner.getTrait(Miner.class).getSupplies(startCorner);
                        step = 64;
                    }
                    if(x < Mine.this.getWidth() - (Mine.this.getWidth() % 2 == 0 ? 1 : 0)){
                        Location nLoc = startCorner.clone().add(x,y,z);
                        Collection<ItemStack> drops = nLoc.getBlock().getDrops();
                        for(ItemStack drop : drops){
                            Mine.this.getStorage().addItem(drop);
                        }
                        nLoc.getBlock().setType(Material.AIR);
                        Miner.teleport(nLoc.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        x++;
                    }else{
                        x = 0;
                        if(z < Mine.this.getLength() - 1){ // this is a bit strange, it seems to work tho
                            Location nLoc = startCorner.clone().add(x,y,z);
                            Collection<ItemStack> drops = nLoc.getBlock().getDrops();
                            for(ItemStack drop : drops){
                                Mine.this.getStorage().addItem(drop);
                            }
                            nLoc.getBlock().setType(Material.AIR);
                            Miner.teleport(nLoc.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            z++;
                        }else{
                            z = 0;
                            if(y >= -30 || startCorner.clone().getBlockY() + y > 3){
                                Location nLoc = startCorner.clone().add(x,y,z);
                                Collection<ItemStack> drops = nLoc.getBlock().getDrops();
                                for(ItemStack drop : drops){
                                    Mine.this.getStorage().addItem(drop);
                                }
                                nLoc.getBlock().setType(Material.AIR);
                                Miner.teleport(nLoc.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                y--;
                            }else{
                                Mine.this.digging = false;
                                finished = true;
                            }
                        }
                    }
                }else{
                    Miner.despawn();
                }
            }
        }
    }
}
