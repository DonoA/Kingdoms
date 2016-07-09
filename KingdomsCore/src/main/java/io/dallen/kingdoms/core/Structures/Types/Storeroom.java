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
package io.dallen.kingdoms.core.Structures.Types;

import io.dallen.kingdoms.core.Handlers.BuildMenuHandler;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Storage;
import io.dallen.kingdoms.core.Structures.Vaults.BuildingVault;
import io.dallen.kingdoms.utilities.Annotations.SaveData;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEvent;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEventHandler;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to store raw and refined materials
 *
 * @author donoa_000
 */
public class Storeroom extends Plot implements Storage {

    @Getter
    private int maxCapacity;
    @Getter
    @Setter
    @SaveData
    private BuildingVault Storage;
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;

    public Storeroom(Plot p) {
        super(p);
//        maxCapacity = p.getArea() * 100;
        Storage = new BuildingVault(30, 30 * 100, this);
        EditPlot = new ChestGUI("Storeroom", 2, new MenuHandler()) {
            {
                setOption(1 * 9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
                setOption(1 * 9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
                setOption(1 * 9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
            }
        };

        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()) {
            {
                setOption(1 * 9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Other");
            }
        };
    }

    @Override
    public boolean interact(PlayerInteractEvent e) {
        if (e.getClickedBlock().getType().equals(Material.CHEST)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (Storage.CanOpen(e.getPlayer())) {
                    Storage.SendToPlayer(e.getPlayer());
                    return true;
                }
            } else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (e.hasItem() && this.hasSpace()) {
                    if (Storage.addItem(e.getItem())) {
                        e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void sendEditMenu(Player p) {
        EditPlot.sendMenu(p);
    }

    public class MenuHandler implements OptionClickEventHandler {

        @Override
        public void onOptionClick(OptionClickEvent e) {
            if (e.getMenuName().equals(EditPlot.getName())) {
                BuildMenuHandler.chestBuildOptions(e, Storeroom.this);
            }
        }
    }

    @Override
    public boolean hasSpace() {
        return Storage.getFullSlots() < Storage.getUniqueSize() && Storage.getAmountFull() < Storage.getCapacity();
    }

    @Override
    public boolean supplyNPC(NPC npc) {
        return true;
    }

    private static class ResourceStats {

        @Getter
        @Setter
        private int Wealth;
        @Getter
        @Setter
        private int Grain;
        @Getter
        @Setter
        private int Sand;
        @Getter
        @Setter
        private int Wood;
        @Getter
        @Setter
        private int Ores;
        @Getter
        @Setter
        private int Stone;
        @Getter
        @Setter
        private int RefinedWood;
        @Getter
        @Setter
        private int Brick;
        @Getter
        @Setter
        private int Metal;
        @Getter
        @Setter
        private int Corps;
        @Getter
        @Setter
        private int Glass;

        @Getter
        @Setter
        private int StorageSpace;
        @Getter
        @Setter
        private int Population;

        public ResourceStats() {

        }

    }

}
