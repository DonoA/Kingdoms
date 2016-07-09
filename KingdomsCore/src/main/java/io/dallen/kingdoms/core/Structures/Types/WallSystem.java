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
import io.dallen.kingdoms.core.Municipality;
import io.dallen.kingdoms.core.Storage.JsonWallSystem;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEvent;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEventHandler;
import io.dallen.kingdoms.utilities.Utils.LocationUtil;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class WallSystem implements SaveType.Saveable {

    @Getter
    private HashMap<WallType, ArrayList<Wall>> Parts = new HashMap<WallType, ArrayList<Wall>>();

    @Getter
    private Municipality municipal;

    public WallSystem(Municipality m) {
        this.municipal = m;
        Parts.put(WallType.WALL, new ArrayList<Wall>());
        Parts.put(WallType.CORNER, new ArrayList<Wall>());
        Parts.put(WallType.GATE, new ArrayList<Wall>());
        Parts.put(WallType.TOWER, new ArrayList<Wall>());
    }

    public boolean recalculateBase() {//Should be called async if possible
        ArrayList<Wall> corners = new ArrayList<Wall>();
        corners.addAll(Parts.get(WallType.CORNER));
        corners.addAll(Parts.get(WallType.TOWER));
        int[] Xs = new int[corners.size()];
        int[] Zs = new int[corners.size()];
        int i = 0;
        for (i = 0; i < corners.size(); i++) {
            Xs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getX();
            Zs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getY();
        }
        Polygon newBase = new Polygon(Xs, Zs, i);
        municipal.setBase(newBase);
        return true;
    }

    public JsonWallSystem toJsonObject() {
        JsonWallSystem jws = new JsonWallSystem();
        HashMap<String, ArrayList<Integer>> pts = new HashMap<String, ArrayList<Integer>>();
        for (Entry<WallType, ArrayList<Wall>> e : Parts.entrySet()) {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            for (Wall w : e.getValue()) {
                ids.add(w.getStructureID());
            }
            pts.put(e.getKey().name(), ids);
        }
        jws.setParts(pts);
        return jws;
    }

    public static enum WallType {

        WALL, GATE, TOWER, CORNER
    }

    public static class Wall extends Plot {

        @Getter
        private static ArrayList<String> damageBars = new ArrayList<String>();

        @Getter
        private ArrayList<Player> currInteracters = new ArrayList<Player>();

        @Getter
        private BossBar damageBar;

        @Getter
        private WallType type;

        @Getter
        private ChestGUI EditPlot;

        @Getter
        private ChestGUI BuildMenu;

        public Wall(Plot p, WallType type) {
            super(p);
            this.type = type;
            EditPlot = new ChestGUI("Wall", 2, new MenuHandler()) {
                {
                    setOption(1 * 9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
                    setOption(1 * 9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
                    setOption(1 * 9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
                }
            };

            BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()) {
                {
                    setOption(1 * 9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Tall Wall");
                    setOption(1 * 9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Short Wall");
                    setOption(1 * 9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Other");
                }
            };
        }

        public void damage() {
            damageBar.setProgress(damageBar.getProgress() - 1);
        }

        public void repair() {
            damageBar.setProgress(damageBar.getProgress() + 1);
        }

        @Override
        public void sendEditMenu(Player p) {
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Train Builder", this);

            EditPlot.sendMenu(p);
        }

        public class MenuHandler implements OptionClickEventHandler {

            @Override
            public void onOptionClick(OptionClickEvent e) {
                if (e.getMenuName().equals(EditPlot.getName())) {
                    BuildMenuHandler.chestBuildOptions(e, Wall.this);
                }
            }
        }
    }

}
