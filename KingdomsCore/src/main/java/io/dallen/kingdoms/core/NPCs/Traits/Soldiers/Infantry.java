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
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine;
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine.FsmState;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.core.Structures.Types.Armory;
import io.dallen.kingdoms.core.Structures.Types.Barracks;
import io.dallen.kingdoms.utilities.Utils.LocationUtil;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.util.Arrays;
import java.util.Collection;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Infantry extends Soldier {

    private Municipality municipal;

    private NPC master;

    private Plot home;

    private final FiniteStateMachine brain;

    public Infantry(Municipality municipal) {
        super("Infantry");
        brain = new FiniteStateMachine(new patrol());
        this.municipal = municipal;
        for (Structure s : municipal.getStructures().get(Barracks.class)) {
            Barracks b = (Barracks) s;
            if (b.getHoused().size() < b.getCurrentCapacity()) {
                home = b;
                b.getHoused().add(this);
            }
        }
    }

    @Override
    public void onAttach() {
        ItemStack[] neededArmor = new ItemStack[5];
        if (((LivingEntity) npc.getEntity()).getEquipment().getHelmet() == null) {
            neededArmor[0] = new ItemStack(Material.IRON_HELMET);
        } else if (((LivingEntity) npc.getEntity()).getEquipment().getChestplate() == null) {
            neededArmor[1] = new ItemStack(Material.IRON_CHESTPLATE);
        } else if (((LivingEntity) npc.getEntity()).getEquipment().getLeggings() == null) {
            neededArmor[2] = new ItemStack(Material.IRON_LEGGINGS);
        } else if (((LivingEntity) npc.getEntity()).getEquipment().getBoots() == null) {
            neededArmor[3] = new ItemStack(Material.IRON_BOOTS);
        } else if (((LivingEntity) npc.getEntity()).getEquipment().getItemInMainHand() == null) {
            neededArmor[4] = new ItemStack(Material.IRON_SWORD);
        }
        LogUtil.printDebug(Arrays.toString(neededArmor));
        for (ItemStack is : neededArmor) {
            if (is != null) {
                Armory a = getClosest(is);
                if(a != null){
                    brain.getStateQueue().add(new getArmor(a, is));
                }
            }
        }
    }
    
    private Armory getClosest(ItemStack is) {
        Armory closest = null;
        for (Structure s : municipal.getStructures().get(Armory.class)) {
            if (((Armory) s).getStorage().getMaterial(is) != null) {
                if (closest == null) {
                    closest = (Armory) s;
                } else if (closest.getCenter().distance(npc.getEntity().getLocation())
                        > s.getCenter().distance(npc.getEntity().getLocation())) {
                    closest = (Armory) s;
                }
            }
        }
        return closest;
    }

    public class getArmor implements FsmState {

        private Armory targetArmory;

        private boolean assignedNav = false;

        private ItemStack toRetrive;
        
        public getArmor(Armory arm, ItemStack armor) {
            this.targetArmory = arm;
            this.toRetrive = armor;
        }

        @Override
        public void invoke() {
            if (!Infantry.this.npc.getNavigator().isNavigating() && !assignedNav) {
                npc.getNavigator().setTarget(targetArmory.getCenter());
                assignedNav = true;
            }
        }

        @Override
        public boolean isComplete() {
            if (assignedNav && (!npc.getNavigator().isNavigating()
                    || targetArmory.getBase().contains(LocationUtil.asPoint(npc.getEntity().getLocation())))) {
                if (toRetrive.getType().equals(Material.IRON_HELMET)) {
                    ((LivingEntity) npc.getEntity()).getEquipment().setHelmet(toRetrive);
                } else if (toRetrive.getType().equals(Material.IRON_CHESTPLATE)) {
                    ((LivingEntity) npc.getEntity()).getEquipment().setChestplate(toRetrive);
                } else if (toRetrive.getType().equals(Material.IRON_LEGGINGS)) {
                    ((LivingEntity) npc.getEntity()).getEquipment().setLeggings(toRetrive);
                } else if (toRetrive.getType().equals(Material.IRON_BOOTS)) {
                    ((LivingEntity) npc.getEntity()).getEquipment().setBoots(toRetrive);
                } else if (toRetrive.getType().equals(Material.IRON_SWORD)) {
                    ((LivingEntity) npc.getEntity()).getEquipment().setItemInMainHand(toRetrive);
                }
                targetArmory.getStorage().removeItem(toRetrive);
                return true;
            }
            return false;
        }

        

    }

    public class patrol implements FsmState {

        private Location target;

        private long ticksTilNextPatrol = 0;

        @Override
        public void invoke() {
            if (!npc.getNavigator().isNavigating() && ticksTilNextPatrol <= 0) {
                if (((LivingEntity) npc.getEntity()).getEquipment().getHelmet() == null
                        || ((LivingEntity) npc.getEntity()).getEquipment().getChestplate() == null
                        || ((LivingEntity) npc.getEntity()).getEquipment().getLeggings() == null
                        || ((LivingEntity) npc.getEntity()).getEquipment().getBoots() == null
                        || ((LivingEntity) npc.getEntity()).getEquipment().getItemInMainHand() == null) {
                    onAttach();
                } else {
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
                    target = testSpot;
                    Bukkit.getScheduler().runTask(KingdomsCore.getPlugin(), new Runnable() {
                        public void run() {
                            npc.getNavigator().setTarget(target);
                            ticksTilNextPatrol = 200 + Math.round(Math.random() * 1200);
                        }
                    });
                }
            } else {
                ticksTilNextPatrol--;
            }
        }

        @Override
        public boolean isComplete() {
            boolean complete = false;
            Collection<Zombie> zomb = npc.getEntity().getLocation().getWorld().getEntitiesByClass(Zombie.class);
            for (Zombie z : zomb) {
                if (!complete && ((LivingEntity) npc.getEntity()).hasLineOfSight(z)) {
                    brain.getStateQueue().add(new KillEnemy(z));
                    z.setTarget(((LivingEntity) npc.getEntity()));
                    complete = true;
                }
            }
            if (!npc.getNavigator().isNavigating()) {
                Collection<Skeleton> skel = npc.getEntity().getLocation().getWorld().getEntitiesByClass(Skeleton.class);
                for (Skeleton s : skel) {
                    if (!complete && ((LivingEntity) npc.getEntity()).hasLineOfSight(s)) {
                        brain.getStateQueue().add(new KillEnemy(s));
                        s.setTarget(((LivingEntity) npc.getEntity()));
                        complete = true;
                    }
                }
            }
            return false;
        }
    }

    public class KillEnemy implements FsmState {

        private LivingEntity target;

        public KillEnemy(LivingEntity m) {
            target = m;
        }

        @Override
        public void invoke() {
            if (!npc.getNavigator().isNavigating()) {
                npc.getNavigator().setTarget(target, true);
            }
        }

        @Override
        public boolean isComplete() {
            if (target.isDead()) {
                return true;
            } else if (((LivingEntity) npc.getEntity()).getHealth() <= 4) {
                brain.getStateQueue().add(new FiniteStateMachine.basicNavigation(home.getCenter(), npc));
                return true;
            } else {
                return false;
            }
        }
    }
}
