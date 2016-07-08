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
package io.dallen.kingdoms.core.Structures.Vaults;

import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Storage.JsonBuildingVault;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonItemStack;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
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
public class BuildingVault implements Vault, SaveType.Saveable{ // Will start pile from center and add blocks outward
    @Getter @Setter
    private Structure owner;
    
    @Getter
    private double uniqueSize;
    
    @Getter @Setter
    private int fullSlots;
    
    @Getter
    private int capacity;
    
    @Getter @Setter
    private int amountFull;
    
    @Getter @Setter
    private Polygon floorPlan;
    
    @Getter @Setter
    private ItemStack[] contents;
    
    @Getter
    private ResourcePile pile;
    
    @Getter
    private List<Material> list = null;
    
    @Getter
    private List<String> listPatern = null;
    
    private ListType listType = ListType.ALL;
    
    public BuildingVault(int uniqueSize, int capacity, Structure owner) {
        this.owner = owner;
        this.uniqueSize = uniqueSize;
        this.capacity = capacity;
        contents = new ItemStack[uniqueSize];
    }
    
    public BuildingVault(int uniqueSize, int capacity) {
        this.uniqueSize = uniqueSize;
        this.capacity = capacity;
        contents = new ItemStack[uniqueSize];
    }
    
    public void setFilter(ListType listType, Material...items){
        this.listType = listType;
        if(listType.equals(ListType.WHITELIST) || listType.equals(ListType.BLACKLIST)){
            listPatern = null;
            list = Arrays.asList(items);
        }
    }
    
    public void setFilter(ListType listType, String...listPatern){
        this.listType = listType;
        if(listType.equals(ListType.WHITELIST) || listType.equals(ListType.BLACKLIST)){
            list = null;
            this.listPatern = Arrays.asList(listPatern);
        }
    }
    
    public void setFilter(ListType listType){
        this.listType = listType;
    }
    
    public boolean canAdd(Material m){
        if(listType.equals(ListType.ALL)){
            return true;
        }else if(listType.equals(ListType.NONE)){
            return false;
        }else if(listType.equals(ListType.WHITELIST)){
            if(listPatern == null && list.contains(m)){
                return true;
            }else if(listPatern != null){
                for(String s : listPatern){
                    if(m.name().contains(s)){
                        return true;
                    }
                }
            }
            return false;
        }else if(listType.equals(ListType.BLACKLIST)){
            if(listPatern == null && list.contains(m)){
                return false;
            }else if(listPatern != null){
                for(String s : listPatern){
                    if(m.name().contains(s)){
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }
    
    @Override
    public boolean SendToPlayer(Player p){
        Inventory inv = Bukkit.createInventory(p, (int) uniqueSize, "Building Inventory");
        for(ItemStack m : contents){
            if(m != null){
                ItemStack toAdd = m.clone();
                toAdd.setAmount((m.getAmount() < 64 ? m.getAmount() : 64));
                List<String> lore = (toAdd.hasItemMeta() && toAdd.getItemMeta().hasLore() ? 
                        toAdd.getItemMeta().getLore() : new ArrayList<String>());
                lore.add(" ");
                lore.add(m.getAmount() + " total");
                toAdd.getItemMeta().setLore(lore);
                inv.addItem(toAdd);
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
                if(!contents[i].getType().equals(Material.AIR)){
                    ItemStack toAdd = contents[i].clone();
                    toAdd.setAmount((contents[i].getAmount() < 64 ? contents[i].getAmount() : 64));
                    List<String> lore = (toAdd.hasItemMeta() && toAdd.getItemMeta().hasLore() ? 
                            toAdd.getItemMeta().getLore() : new ArrayList<String>());
                    lore.add(" ");
                    lore.add(contents[i].getAmount() + " total");
                    toAdd.getItemMeta().setLore(lore);
                    inv.setItem(i, toAdd);
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
     * @return the amount remaining in the passed stack after removal
     */
    public int removeItem(ItemStack is){
        for(ItemStack mw : contents) {
            if(mw != null && mw.isSimilar(is)) {
                mw.setAmount(mw.getAmount() - is.getAmount());
                if(mw.getAmount() == 0){
                    mw.setType(Material.AIR);
                    fullSlots--;
                    return 0;
                }else{
                    int amnt = -mw.getAmount();
                    mw.setAmount(0);
                    return amnt;
                }
            }
        }
        return is.getAmount();
    }
    
    /**
     * 
     * @param is the stack to be added
     * @return if the item could be added
     */
    public boolean addItem(ItemStack is){
        if(fullSlots < uniqueSize && amountFull < capacity && canAdd(is.getType())){
            boolean added = false;
            for(ItemStack mw : contents) {
                if(mw != null && mw.isSimilar(is)) {
                    mw.setAmount(mw.getAmount() + is.getAmount());
                    added = true;
                }
            }
            if(!added){
                contents[fullSlots] = is.clone();
                fullSlots++;
            }
            return true;
        }
        return false;
    }
    
    public ItemStack getMaterial(ItemStack mat){
        for(ItemStack mw : contents){
            if(mw != null && mw.isSimilar(mat) && mw.getAmount() > 0){
                return mw;
            }
        }
        return null;
    }
    
    public JsonBuildingVault toJsonObject(){
        JsonBuildingVault jbv = new JsonBuildingVault();
        jbv.setAmountFull(amountFull);
        jbv.setCapacity(capacity);
        ArrayList<JsonItemStack> content = new ArrayList<JsonItemStack>();
        for(ItemStack mw : contents){
            if(mw != null){
                content.add(new JsonItemStack(mw));
            }else{
                content.add(null);
            }
        }
        jbv.setContents(content.toArray(new JsonItemStack[] {}));
//        jbv.setFloorPlan(new JsonPolygon(floorPlan));
        jbv.setFullSlots(fullSlots);
        jbv.setOwner(owner.getStructureID());
        jbv.setUniqueSize(uniqueSize);
        return jbv;
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
    
    public static class ResourcePile implements SaveType.Saveable{
        
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
                    Bukkit.getScheduler().runTask(KingdomsCore.getPlugin(), new Runnable(){
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
                    Bukkit.getScheduler().runTask(KingdomsCore.getPlugin(), new Runnable(){
                        @Override
                        public void run(){
                            bloc.getBlock().setType(Material.AIR);
                        }
                    });
                }
            }).start();
        }
    }
    
    public static enum ListType{
        WHITELIST, BLACKLIST, ALL, NONE;
    }
}
