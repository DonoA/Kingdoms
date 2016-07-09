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
package io.dallen.kingdoms.core.NPCs.Traits.Soldiers;

import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Municipality;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Archer extends Trait {

    private final static String[] names = new String[]{
        "Dallen"
    };

    @Setter
    private Municipality municipal;

    private NPC master;

    private Runnable newPatrol = new Runnable() {

        @Override
        public void run() {
            boolean found = false;
            Location testSpot = null;
            while (!found) {
                double Radius = 15;
                double angle = Math.random() * Math.PI * 2;
                double x = Math.cos(angle) * Math.random() * Radius;
                double z = Math.sin(angle) * Math.random() * Radius;
                testSpot = new Location(municipal.getCenter().getCenter().getWorld(), municipal.getCenter().getCenter().getX() + x,
                        municipal.getCenter().getCenter().getY() + 1, municipal.getCenter().getCenter().getZ() + z);
                for (int y = -30; y <= 30 && !found; y++) {
                    if (testSpot.clone().add(0, y, 0).getBlock().getType().equals(Material.AIR)
                            && !testSpot.clone().add(0, y - 1, 0).getBlock().getType().equals(Material.AIR)) {
                        testSpot.add(0, y, 0);
                        found = true;
                    }
                }
            }
            final Location spot = testSpot;
            Bukkit.getScheduler().runTask(KingdomsCore.getPlugin(), new Runnable() {
                public void run() {
                    master.getNavigator().setTarget(spot);
                }
            });
            Bukkit.getScheduler().scheduleAsyncDelayedTask(KingdomsCore.getPlugin(), newPatrol, 200 + Math.round(Math.random() * 1200));
        }
    };

    public Archer() {
        super("Archer");
    }

    @Override
    public void onAttach() {
        ((LivingEntity) super.npc.getEntity()).getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        ((LivingEntity) super.npc.getEntity()).getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        ((LivingEntity) super.npc.getEntity()).getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        ((LivingEntity) super.npc.getEntity()).getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        ((LivingEntity) super.npc.getEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        ((LivingEntity) super.npc.getEntity()).getEquipment().setItemInOffHand(new ItemStack(Material.ARROW));
        master = super.npc;
        Bukkit.getScheduler().scheduleAsyncDelayedTask(KingdomsCore.getPlugin(), newPatrol, Math.round(Math.random() * 1200));
    }

}
