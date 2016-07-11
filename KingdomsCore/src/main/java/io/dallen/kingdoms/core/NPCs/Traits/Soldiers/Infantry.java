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
import io.dallen.kingdoms.core.Structures.Types.Armory;
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine;
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine.FsmState;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.utilities.Utils.LocationUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
public class Infantry extends Trait {
//    @Setter

    private Municipality municipal;

    private NPC master;

    private final FiniteStateMachine brain;

    public Infantry(Municipality municipal) {
        super("Infantry");
        brain = new FiniteStateMachine(new patrol());
        this.municipal = municipal;
    }

    @Override
    public void onAttach() {
        brain.getStateQueue().add(new getArmor());

//        master = super.npc;
//        Bukkit.getScheduler().scheduleAsyncDelayedTask(KingdomsCore.getPlugin(), newPatrol, Math.round(Math.random()*1200));
    }

    public class getArmor implements FsmState {

        private Armory targetArmory;

        private boolean assignedNav = false;

        private List<ItemStack> toRetrive = new ArrayList<ItemStack>();

        public getArmor() {
            targetArmory = null;
        }

        public getArmor(Armory arm, ItemStack... armor) {
            this.targetArmory = arm;
            this.toRetrive = Arrays.asList(armor);
        }

        @Override
        public void invoke() {
            if (!Infantry.this.npc.getNavigator().isNavigating() && !assignedNav) {
                if (targetArmory != null) {
                    npc.getNavigator().setTarget(targetArmory.getCenter());
                    assignedNav = true;
                } else {
                    ItemStack[] neededArmor = new ItemStack[]{new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_LEGGINGS),
                        new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_BOOTS)};
                    HashMap<Armory, ArrayList<ItemStack>> otherTasks = new HashMap<Armory, ArrayList<ItemStack>>();
                    for (ItemStack is : neededArmor) {
                        if (targetArmory == null) {
                            targetArmory = getClosest(is);
                            toRetrive.add(is);
                        } else {
                            Armory a = getClosest(is);
                            if (a != targetArmory) {
                                if (otherTasks.containsKey(a)) {
                                    otherTasks.get(a).add(is);
                                } else {
                                    otherTasks.put(a, new ArrayList<ItemStack>(Arrays.asList(new ItemStack[]{is})));
                                }
                            } else {
                                toRetrive.add(is);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean isComplete() {
            if (assignedNav && (!npc.getNavigator().isNavigating()
                    || targetArmory.getBase().contains(LocationUtil.asPoint(npc.getEntity().getLocation())))) {
                for (ItemStack is : toRetrive) {
                    ((LivingEntity) npc.getEntity()).getEquipment().setHelmet(is);
                    targetArmory.getStorage().removeItem(is);
                }
                return true;
            }
            return false;
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
                    npc.getNavigator().setTarget(closest.getCenter());
                    assignedNav = true;
                }
            }
            return closest;
        }
    }

    public class patrol implements FsmState {

        private Location target;

        private long ticksTilNextPatrol = 0;

        @Override
        public void invoke() {
            if (!npc.getNavigator().isNavigating() && ticksTilNextPatrol <= 0) {
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
                        master.getNavigator().setTarget(target);
                        ticksTilNextPatrol = 200 + Math.round(Math.random() * 1200);
                    }
                });
            } else {
                ticksTilNextPatrol--;
            }
            //check for enemies nearby
        }

        @Override
        public boolean isComplete() {
            return false;
        }
    }
}
