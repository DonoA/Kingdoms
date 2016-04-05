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

package io.dallen.Kingdoms.Util.JsonClasses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonArmor {
    
    @Getter @Setter
    private JsonItemStack Helm;
    @Getter @Setter
    private JsonItemStack Chest;
    @Getter @Setter
    private JsonItemStack Leg;
    @Getter @Setter
    private JsonItemStack Boot;
    
    public JsonArmor(ItemStack[] is){
        Helm = new JsonItemStack(is[3]);
        Chest = new JsonItemStack(is[2]);
        Leg = new JsonItemStack(is[1]);
        Boot = new JsonItemStack(is[0]);
    }
    
    /**
     * sets Player armor to this save
     * @param p The player who should be set to the save
     */
    public void GiveTo(Player p){
        p.getInventory().setHelmet(Helm.toItemStack());
        p.getInventory().setChestplate(Chest.toItemStack());
        p.getInventory().setLeggings(Leg.toItemStack());
        p.getInventory().setBoots(Boot.toItemStack());
        p.updateInventory();
    }
    
}
