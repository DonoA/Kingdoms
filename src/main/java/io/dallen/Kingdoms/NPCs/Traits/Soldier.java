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
package io.dallen.Kingdoms.NPCs.Traits;

import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Soldier{
    
    public static class Archer extends Trait{

        public Archer(){
            super("Archer");
        }
        
        @Override
        public void onAttach(){
            ((LivingEntity)super.npc.getEntity()).getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setItemInOffHand(new ItemStack(Material.ARROW));
        }

    }
    
    public static class Infantry extends Trait{
        
        public Infantry(){
            super("Infantry");
        }
        
        @Override
        public void onAttach(){
            ((LivingEntity)super.npc.getEntity()).getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
        }

    }
    
    public static class Cavalry extends Trait{

        public Cavalry(){
            super("Cavalry");
        }
        
        @Override
        public void onAttach(){
            ((LivingEntity)super.npc.getEntity()).getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            Horse mount = (Horse) super.npc.getEntity().getLocation().getWorld().spawnEntity(super.npc.getEntity().getLocation(), EntityType.HORSE);
            mount.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
            mount.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            mount.setOwner((AnimalTamer) super.npc.getEntity());
            mount.setPassenger(super.npc.getEntity());
        }

    }
    
    public static class General extends Trait{

        public General(){
            super("General");
        }
        
        @Override
        public void onAttach(){
            ((LivingEntity)super.npc.getEntity()).getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
            ((LivingEntity)super.npc.getEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.STICK));
        }

    }
}
