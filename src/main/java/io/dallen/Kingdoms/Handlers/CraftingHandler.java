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
package io.dallen.Kingdoms.Handlers;

import io.dallen.Kingdoms.Util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Donovan Allen
 */
public class CraftingHandler implements Listener{
    
    public CraftingHandler(JavaPlugin p){
        ItemStack crown = ItemUtil.setItemNameAndLore(Material.GOLD_HELMET, "Crown", "The crown of a lord");
        ShapedRecipe Crown = new ShapedRecipe(crown);
        Crown.shape("GDG","GGG");
        Crown.setIngredient('G', Material.GOLD_INGOT);
        Crown.setIngredient('D', Material.DIAMOND);
        p.getServer().addRecipe(Crown);
        
        ItemStack MultiTool = ItemUtil.setItemNameAndLore(Material.WATCH, "Multi Tool", "Used for managing plots");
        ShapedRecipe mTool = new ShapedRecipe(MultiTool);
        mTool.shape("IiI"," S "," C ");
        mTool.setIngredient('I', Material.IRON_BLOCK);
        mTool.setIngredient('i', Material.IRON_INGOT);
        mTool.setIngredient('S', Material.STICK);
        mTool.setIngredient('C', Material.WATCH);
        p.getServer().addRecipe(mTool);
        
        ItemStack CommandStick = ItemUtil.setItemNameAndLore(Material.STICK, "Command Stick", "Used for managing troops");
        ShapedRecipe cStick = new ShapedRecipe(CommandStick);
        mTool.shape(" I "," S "," S ");
        mTool.setIngredient('I', Material.WATCH);
        mTool.setIngredient('S', Material.STICK);
        p.getServer().addRecipe(cStick);
        
        Bukkit.getPluginManager().registerEvents(this, p);
    }
    
    @EventHandler
    public void onEnchantItem(EnchantItemEvent e){
        if(e.getItem().getType().name().contains("_BARDING")){
            e.getEnchantsToAdd().put(Enchantment.DURABILITY, 2);
        }
    }
    
}
