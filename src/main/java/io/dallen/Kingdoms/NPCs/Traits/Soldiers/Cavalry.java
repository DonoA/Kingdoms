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
package io.dallen.Kingdoms.NPCs.Traits.Soldiers;

import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Main;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
public class Cavalry extends Trait{
    
    @Setter
    private Municipality municipal;

    private NPC mount;

    private NPC master;

    private Runnable newPatrol = new Runnable(){
        
        @Override
        public void run() {
            boolean found = false;
            Location testSpot = null;
            while(!found){
                double Radius = 15;
                double angle = Math.random()*Math.PI*2;
                double x = Math.cos(angle)*Math.random()*Radius;
                double z = Math.sin(angle)*Math.random()*Radius;
                testSpot = new Location(municipal.getCenter().getCenter().getWorld(), municipal.getCenter().getCenter().getX() + x, 
                        municipal.getCenter().getCenter().getY() + 1, municipal.getCenter().getCenter().getZ() + z);
                for(int y = -30; y <= 30 && !found; y++){
                    if(testSpot.clone().add(0, y, 0).getBlock().getType().equals(Material.AIR) && 
                            !testSpot.clone().add(0, y-1, 0).getBlock().getType().equals(Material.AIR)){
                        testSpot.add(0, y, 0);
                        found = true;
                    }
                }
            }
            final Location spot = testSpot;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){public void run(){mount.getNavigator().setTarget(spot);}});
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getPlugin(), newPatrol, 200 + Math.round(Math.random()*1200));
        }
    };

    public Cavalry(){
        super("Cavalry");
        this.npc = super.npc;
    }

    @Override
    public void onAttach(){
        ((LivingEntity)super.npc.getEntity()).getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        ((LivingEntity)super.npc.getEntity()).getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        ((LivingEntity)super.npc.getEntity()).getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        ((LivingEntity)super.npc.getEntity()).getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        ((LivingEntity)super.npc.getEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        NPC horse = Main.getNPCs().getNPCReg().createNPC(EntityType.HORSE, "");
        horse.spawn(super.npc.getStoredLocation());
        Horse mount = (Horse) horse.getEntity();
        mount.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
        mount.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        mount.setOwner((AnimalTamer) super.npc.getEntity());
        mount.setPassenger(super.npc.getEntity());
        this.mount = horse;
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getPlugin(), newPatrol, Math.round(Math.random()*1200));
    }

}
    
