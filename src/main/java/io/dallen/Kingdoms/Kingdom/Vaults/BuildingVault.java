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
package io.dallen.Kingdoms.Kingdom.Vaults;

import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonBuildingVault;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Storage.MaterialWrapper;
import io.dallen.Kingdoms.Storage.SaveTypes;
import java.awt.Polygon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class BuildingVault implements Vault, SaveTypes.Saveable{ // Will start pile from center and add blocks outward
    @Getter
    private Structure owner;
    
    @Getter
    private double uniqueSize;
    
    @Getter
    private int fullSlots;
    
    @Getter
    private int capacity;
    
    @Getter
    private int amountFull;
    
    @Getter
    private Polygon floorPlan;
    
    @Getter
    private MaterialWrapper[] contents;
    
    @Getter
    private ResourcePile pile;
    
    public BuildingVault(int uniqueSize, int capacity, Structure owner) {
        this.owner = owner;
        this.uniqueSize = uniqueSize;
        this.capacity = capacity;
        contents = new MaterialWrapper[uniqueSize];
    }
    
    @Override
    public boolean SendToPlayer(Player p){
        Inventory inv = Bukkit.createInventory(p, (int) Math.ceil(uniqueSize/9)*9, "Building Inventory");
        for(MaterialWrapper m : contents){
            if(m != null){
                inv.addItem(m.asBukkitItem());
            }
        }
//        InvenHandler.openVaults.put(p.getName(), this);
        p.openInventory(inv);
        return false;
    }
    
    @Override
    public boolean CanOpen(Player p){
//        if(owner.getMunicipal() != null && PlayerData.getData(p) != null){
//            PlayerData pd = PlayerData.getData(p);
//            if(pd.getMunicipal().equals(owner.getMunicipal())){
//                return true;
//            }
//        }else{
//            if(owner.getOwner().equals(p)){
//                return true;
//            }
//        }
//        return false;
        return true;
    }
    
    public void updateInventory(Inventory inv){
        for(int i = 0; i < inv.getSize(); i++){
            if(contents[i] != null){
                if(!contents[i].getMaterial().equals(Material.AIR)){
                    inv.setItem(i, contents[i].asBukkitItem());
                }else{
                    inv.setItem(i, new ItemStack(Material.AIR));
                    contents[i] = null;
                }
            }else{
                inv.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }
    
    /**
     * 
     * @param is the stack to be removed
     * @return if all of that material was removed
     */
    public boolean removeItem(ItemStack is){
        for(MaterialWrapper mw : contents) {
            if(mw != null && mw.getMaterial().equals(is.getType())) {
                mw.removeFromStack(is.getAmount());
                if(mw.getStackSize() <= 0){
                    mw.setMaterial(Material.AIR);
                    fullSlots--;
                    return true;
                }
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param is the stack to be added
     * @return if it was a new stack
     */
    public boolean addItem(ItemStack is){
        if(fullSlots < uniqueSize && amountFull < capacity){
            boolean added = false;
            for(MaterialWrapper mw : contents) {
                if(mw != null && mw.getMaterial().equals(is.getType())) {
                    mw.addToStack(is.getAmount());
                    added = true;
                }
            }
            if(!added){
                contents[fullSlots] = new MaterialWrapper(is);
                fullSlots++;
                return true;
            }
        }
        return false;
    }
    
    public MaterialWrapper getMaterial(Material mat){
        for(MaterialWrapper mw : contents){
            if(mw != null && mw.getMaterial().equals(mat)){
                return mw;
            }
        }
        return null;
    }
    
    public JsonBuildingVault toJsonObject(){
        throw new UnsupportedOperationException();
    }
    
//    public static class InvenHandler implements Listener{
//        
//        private static HashMap<String, BuildingVault> openVaults = new HashMap<String, BuildingVault>();
//        
//        @EventHandler
//        public void onInventoryClick(InventoryClickEvent e){
//            if(openVaults.containsKey(e.getWhoClicked().getName())){
//                BuildingVault v = openVaults.get(e.getWhoClicked().getName());
//                if(e.getCursor() != null){
//                    v.addItem(e.getCursor());
//                    e.getWhoClicked().closeInventory();
//                    v.SendToPlayer((Player) e.getWhoClicked());
//                }else{
//                    if(v.getContents()[e.getSlot()].getStackSize() > 64){
//                        e.getInventory().setItem(e.getSlot(), v.getContents()[e.getSlot()].asBukkitItem());
//                    }else{
//                        v.removeItem(e.getSlot());
//                        e.getWhoClicked().closeInventory();
//                        v.SendToPlayer((Player) e.getWhoClicked());
//                    }
//                }
//            }
//        }
//        
//        @EventHandler
//        public void onInventoryOpen(InventoryOpenEvent e){
//            if(openVaults.containsKey(e.getPlayer().getName())){
//                openVaults.remove(e.getPlayer().getName());
//            }
//        }
//    }
    
    public static class ResourcePile implements SaveTypes.Saveable{
        
        @Getter
        private Location startLoc;
        @Getter
        private double dist = 0.5;
        @Getter
        private double angleA = 0;
        @Getter
        private double angleB = 0;
        
        public ResourcePile(Location start){
            this.startLoc = start;
        }
        
        @Override
        public JsonBuildingVault toJsonObject(){
            throw new UnsupportedOperationException();
        }
        
        public void addBlock(){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    Location sugLoc = startLoc.clone().add(dist*Math.sin(Math.toRadians(angleA)), dist*Math.sin(Math.toRadians(angleB)), dist*Math.cos(Math.toRadians(angleA)));
                    while(!sugLoc.getBlock().getType().equals(Material.AIR)){
                        angleA++;
                        if(angleA >= 360){
                            angleB++;
                            angleA = 0;
                        }
                        if(angleB >= 180){
                            angleB = 0;
                            dist++;
                        }
                    }
                    final Location bloc = sugLoc.clone();
                    Bukkit.getScheduler().runTask(Main.getPlugin(), new Runnable(){
                        @Override
                        public void run(){
                            bloc.getBlock().setType(Material.LAPIS_BLOCK);
                        }
                    });
                }
            }).start();
        }
        
        public void removeBlock(){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    Location sugLoc = startLoc.clone().add(dist*Math.sin(Math.toRadians(angleA)), dist*Math.sin(Math.toRadians(angleB)), dist*Math.cos(Math.toRadians(angleA)));
                    while(!sugLoc.getBlock().getType().equals(Material.LAPIS_BLOCK)){
                        angleA--;
                        if(angleA <= 0){
                            angleB--;
                            angleA = 360;
                        }
                        if(angleB <= 0){
                            angleB = 180;
                            dist--;
                        }
                    }
                    final Location bloc = sugLoc.clone();
                    Bukkit.getScheduler().runTask(Main.getPlugin(), new Runnable(){
                        @Override
                        public void run(){
                            bloc.getBlock().setType(Material.AIR);
                        }
                    });
                }
            }).start();
        }
    }
}
