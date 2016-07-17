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
import io.dallen.kingdoms.core.NPCs.Traits.Soldiers.Soldier;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEvent;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEventHandler;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to house its NPCs
 *
 * @author donoa_000
 */
public class Barracks extends Plot {

    @Getter
    private int maxCapacity;
    @Getter
    private int currentCapacity;

    @Getter
    private int readySpeed;

    @Getter
    private PopulationStats people;

    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;

    @Getter
    private ArrayList<Soldier> housed = new ArrayList<Soldier>();

    public Barracks(Plot p) {
        super(p);
        maxCapacity = p.getArea() / 4;
        currentCapacity = 0;
        people = new PopulationStats();
        readySpeed = 10;
        EditPlot = new ChestGUI("Barracks", 2, new MenuHandler()) {
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
    public void sendEditMenu(Player p) {
        EditPlot.sendMenu(p);
    }

    public class MenuHandler implements OptionClickEventHandler {

        @Override
        public void onOptionClick(OptionClickEvent e) {
            if (e.getMenuName().equals(EditPlot.getName())) {
                BuildMenuHandler.chestBuildOptions(e, Barracks.this);
            }
        }
    }

    public static class BedHandler implements Listener {

        public BedHandler() {
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent e) {
            if (e.getBlock().getType().equals(Material.BED)) {
                Plot p = Plot.inPlot(e.getBlock().getLocation());
                if (p != null && p instanceof Barracks) {
                    Barracks b = (Barracks) p;
                    b.currentCapacity++;
                }
            }
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent e) {
            if (e.getBlock().getType().equals(Material.BED)) {
                Plot p = Plot.inPlot(e.getBlock().getLocation());
                if (p != null && p instanceof Barracks) {
                    Barracks b = (Barracks) p;
                    b.currentCapacity--;
                }
            }
        }

    }

    @NoArgsConstructor
    public static class PopulationStats {

        @Getter
        private int infantry;

        @Getter
        private int calvalry;

        @Getter
        private int archers;

        @Getter
        private int other;
    }
}
