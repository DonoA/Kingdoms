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
package io.dallen.Kingdoms.RPG.Contract.Types;

import io.dallen.Kingdoms.RPG.Contract.PlotContract;
import io.dallen.kingdoms.core.Overrides.KingdomMaterial;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Types.TownHall;
import io.dallen.kingdoms.rpg.ContractHandler;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ItemUtil;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class DemolishContract implements PlotContract{
    
    @Getter
    private int ID;
    
    @Getter @Setter
    private ContractTarget contractTarget;
    
    @Getter
    private Player contractor;
    
    @Getter @Setter
    private Object contractee;
    
    @Getter @Setter
    private RewardType rewardType;
    
    @Getter @Setter
    private Object reward;
    
    @Getter
    private Plot plot;
    
    @Getter @Setter
    private ItemStack contractItem;
    
    @Getter @Setter
    private boolean workerFinished;
    
    @Getter @Setter
    private boolean contractorFinished;
    
    public DemolishContract(Player contractor, ChestGUI.OptionClickEvent e){
        this.contractor = contractor;
        Plot p = Plot.inPlot(e.getPlayer().getLocation());
        if(p != null){
            this.ID = ContractHandler.geCurrentID();
            e.getPlayer().setItemInHand(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_UNFINISHED.getItemStack(), 
                    e.getName() + " - Unfinished", String.valueOf(ID)));
            this.plot = p;
            this.contractItem = e.getPlayer().getItemInHand();
            ContractHandler.getAllContracts().put(ID, this);
            e.setNext(new ChestGUI("Select Reward Type", InventoryType.HOPPER, ContractHandler.getInst()){{
                setOption(2, KingdomMaterial.DEFAULT.getItemStack(), "Gold");
                setOption(4, KingdomMaterial.DEFAULT.getItemStack(), "Item");
            }});
        }else{
            e.getPlayer().sendMessage("You must be in a plot to create this type of contract");
        }
    }
    
    @Override
    public boolean isFinished(){
        return true;
    }
    
    public void selectReward(Player p){
        ContractHandler.setReward(this, p);
    }
    
    @Override
    public void interact(PlayerInteractEvent e, boolean finished){
        LogUtil.printDebug("Interact Called, " + finished);
        if(!finished){
            selectReward(e.getPlayer());
        }else{
            e.getPlayer().sendMessage("added contract to plot");
            plot.getContracts().add(this);
            if(plot.getMunicipal() != null){
                ((TownHall) plot.getMunicipal().getCenter()).getContracts().add(this);
            }
            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        }
    }
}
