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
package io.dallen.Kingdoms.Overrides;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Donovan Allen
 */
public enum KingdomMaterial {
    MULTI_TOOL(Material.DIAMOND_HOE, 1),
    START_BUILD(Material.DIAMOND_HOE, 2),
    ARROW_LEFT(Material.DIAMOND_HOE, 3),
    ARROW_RIGHT(Material.DIAMOND_HOE, 4),
    ARROW_DOWN(Material.DIAMOND_HOE, 5),
    ARROW_UP(Material.DIAMOND_HOE, 6),
    ARROW_CLOCKWISE(Material.DIAMOND_HOE, 7),
    ARROW_COUNTER_CLOCKWISE(Material.DIAMOND_HOE, 8),
    STRUCTURE_ARMORY(Material.DIAMOND_HOE, 9),
    STRUCTURE_BANK(Material.DIAMOND_HOE, 10),
    STRUCTURE_BARRACKS(Material.DIAMOND_HOE, 11),
    STRUCTURE_BLACKSMITH(Material.DIAMOND_HOE, 12),
    STRUCTURE_BUILDERSHUT(Material.DIAMOND_HOE, 13),
    STRUCTURE_CASTLE(Material.DIAMOND_HOE, 14),
    STRUCTURE_DUNGEON(Material.DIAMOND_HOE, 15),
    STRUCTURE_FARM(Material.DIAMOND_HOE, 16),
    STRUCTURE_MARKETPLACE(Material.DIAMOND_HOE, 17),
    STRUCTURE_STABLE(Material.DIAMOND_HOE, 18),
    STRUCTURE_STOREROOM(Material.DIAMOND_HOE, 19),
    STRUCTURE_TOWNHALL(Material.DIAMOND_HOE, 20),
    STRUCTURE_TRAININGGROUND(Material.DIAMOND_HOE, 21),
    STRUCTURE_WALL_WALL(Material.DIAMOND_HOE, 22),
    STRUCTURE_WALL_GATE(Material.DIAMOND_HOE, 23),
    STRUCTURE_WALL_TOWER(Material.DIAMOND_HOE, 24),
    STRUCTURE_WALL_CORNER(Material.DIAMOND_HOE, 25),
    STRUCTURE_POSTOFFICE(Material.DIAMOND_HOE, 29),
    STRUCTURE_MINE(Material.DIAMOND_HOE, 30),
    ERASE(Material.DIAMOND_HOE, 26),
    DEMOLISH(Material.DIAMOND_HOE, 27),
    CANCEL(Material.DIAMOND_HOE, 28);
    
    @Getter
    private Material coreItem;
    
    @Getter
    private int dmg;

    @Getter
    private ItemMeta Meta;
    
    KingdomMaterial(Material coreItem, int dmg){
        this.coreItem = coreItem;
        this.dmg = dmg;
    }
    
    public ItemStack getItemStack(){
        ItemStack is = new ItemStack(coreItem);
        is.setDurability((short) dmg);
        return is;
    }
    
    public boolean equals(ItemStack is){
        return is.getType().equals(coreItem) && is.getDurability() == (short) dmg;
//        && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(name);
    }
    
    public static KingdomMaterial fromItemStack(ItemStack is){
        for(KingdomMaterial e : KingdomMaterial.values()){
            if(e.equals(is)){
                return e;
            }
        }
        return null;
    }
}
