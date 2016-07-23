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
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Municipality;
import io.dallen.kingdoms.core.Overrides.KingdomMaterial;
import io.dallen.kingdoms.core.Storage.JsonContract;
import io.dallen.kingdoms.core.Structures.Types.TownHall;
import io.dallen.kingdoms.rpg.ContractHandler;
import io.dallen.kingdoms.rpg.KingdomsRPG;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ItemUtil;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.util.Arrays;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class GatherContract implements Contract {

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
    private ItemStack[] requiredItems;

    @Getter
    private static HashMap<String, GatherContract> openInputs = new HashMap<String, GatherContract>();

    public GatherContract(Player contractor, ChestGUI.OptionClickEvent e) {
        this.contractor = contractor;
        this.ID = ContractHandler.geCurrentID();
        e.getPlayer().setItemInHand(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_UNFINISHED.getItemStack(),
                e.getName() + " - Unfinished", String.valueOf(ID)));
        this.contractItem = e.getPlayer().getItemInHand();
        KingdomsCore.getAllContracts().put(ID, this);
        selectRequiredItems(contractor);
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    public void selectReward(Player p) {
        ContractHandler.setReward(this, p);
    }

    public void selectRequiredItems(final Player p) {
        final Inventory itm = Bukkit.createInventory(p, 9 * 2, "Contract Requirement");
        final GatherContract gc = this;
        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomsRPG.getPlugin(), new Runnable() {
            @Override
            public void run() {
                p.openInventory(itm);
                openInputs.put(p.getName(), gc);
            }
        }, 2);
    }

    @Override
    public void interact(PlayerInteractEvent e, boolean finished) {
        LogUtil.printDebug("Interact Called, " + finished);
        if (!finished) {
            selectReward(e.getPlayer());
        } else {
            Municipality mun = Municipality.inMunicipal(e.getPlayer().getLocation());
            e.getPlayer().sendMessage("added contract to plot");
            if (mun != null) {
                ((TownHall) mun.getCenter()).getContracts().add(this);
            }
            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        }
    }

    public static class GatherContractHandler implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClose(InventoryCloseEvent event) {
            if (openInputs.containsKey(event.getPlayer().getName()) && event.getInventory().getName().equals("Contract Requirement")) {
                GatherContract contract = openInputs.get(event.getPlayer().getName());
                contract.setRequiredItems(event.getInventory().getContents());
                LogUtil.printDebug(Arrays.toString(event.getInventory().getContents()));
                contract.setContractItem(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_FILLED.getItemStack(),
                        contract.getClass().getSimpleName(), String.valueOf(contract.getID())));
                event.getPlayer().setItemInHand(contract.getContractItem());
                openInputs.remove(event.getPlayer().getName());
                contract.selectReward((Player) event.getPlayer());
            }
        }
    }
    
    @Override
    public JsonContract toJsonObject(){
        throw new UnsupportedOperationException();
    }
}
