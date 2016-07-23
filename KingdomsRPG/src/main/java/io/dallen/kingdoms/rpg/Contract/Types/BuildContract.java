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
package io.dallen.kingdoms.rpg.Contract.Types;

import io.dallen.kingdoms.core.Contract;
import io.dallen.kingdoms.core.Contract.ContractTarget;
import io.dallen.kingdoms.core.Contract.RewardType;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.rpg.Contract.PlotContract;
import io.dallen.kingdoms.core.Overrides.KingdomMaterial;
import io.dallen.kingdoms.core.Storage.JsonContract;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Types.BuildersHut;
import io.dallen.kingdoms.core.Structures.Types.TownHall;
import io.dallen.kingdoms.rpg.Contract.BuildingHandler;
import io.dallen.kingdoms.rpg.ContractHandler;
import io.dallen.kingdoms.rpg.KingdomsRPG;
import io.dallen.kingdoms.utilities.Blueprint;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import io.dallen.kingdoms.utilities.Utils.ItemUtil;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.awt.Point;
import java.io.File;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class BuildContract implements PlotContract {

    @Getter
    private int ID;

    @Getter
    @Setter
    private ContractTarget contractTarget;

    @Getter
    private Player contractor;

    @Getter
    @Setter
    private Object contractee;

    @Getter
    @Setter
    private RewardType rewardType;

    @Getter
    @Setter
    private Object reward;

    @Getter
    private Plot plot;

    @Getter
    @Setter
    private ItemStack contractItem;

    @Getter
    @Setter
    private boolean workerFinished;

    @Getter
    @Setter
    private boolean contractorFinished;

    @Getter
    @Setter
    private Location startCorner;

    @Getter
    @Setter
    private BuildersHut supplies;

    @Getter
    @Setter
    private Blueprint building = null;

    public BuildContract(Player contractor, ChestGUI.OptionClickEvent e) {
        this.contractor = contractor;
        Plot p = Plot.inPlot(e.getPlayer().getLocation());
        if (p != null) {
            this.ID = ContractHandler.geCurrentID();
            e.getPlayer().setItemInHand(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_UNFINISHED.getItemStack(),
                    e.getName() + " - Unfinished", String.valueOf(ID)));
            this.plot = p;
            this.contractItem = e.getPlayer().getItemInHand();
            KingdomsCore.getAllContracts().put(ID, this);
            selectBuilding(contractor);
        } else {
            e.getPlayer().sendMessage("You must be in a plot to create this type of contract");
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    public void selectReward(Player p) {
        ContractHandler.setReward(this, p);
    }

    public void selectBuilding(final Player p) {
        ChestGUI buildMenu = new ChestGUI("Prefab Build Menu", 18, BuildingHandler.getBuildChestHandler());
        buildMenu.setMenuData(plot);
        int i = 0;
        LogUtil.printDebug(plot.getClass().getSimpleName());
        for (File f : new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep()
                + plot.getClass().getSimpleName() + DBmanager.getFileSep()).listFiles()) {
            if (i < 9) {
                buildMenu.setOption(0 * 9 + i, new ItemStack(Material.ENCHANTED_BOOK), f.getName(), this);
            } else {
                break;
            }
        }
        final ChestGUI bm = buildMenu;
        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomsRPG.getPlugin(), new Runnable() {
            @Override
            public void run() {
                bm.sendMenu(p);
            }
        }, 2);
        buildMenu.sendMenu(p);
    }

    public void finishSelectBuilding(Blueprint building, Location startCorner) {
        //rename item and things
        this.building = building;
        this.startCorner = startCorner;
        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomsRPG.getPlugin(), new Runnable() {
            @Override
            public void run() {
                selectReward(contractor);
            }
        }, 2);
    }

    @Override
    public void interact(PlayerInteractEvent e, boolean finished) {
        if (!finished) {
            if (building != null) {
                selectReward(e.getPlayer());
            } else {
                selectBuilding(e.getPlayer());
            }
        } else {
            e.getPlayer().sendMessage("added contract to plot");
            plot.getContracts().add(this);
            if (plot.getMunicipal() != null) {
                ((TownHall) plot.getMunicipal().getCenter()).getContracts().add(this);
            }
            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        }
    }
    
    @Override
    public JsonContract toJsonObject(){
        throw new UnsupportedOperationException();
    }

}
