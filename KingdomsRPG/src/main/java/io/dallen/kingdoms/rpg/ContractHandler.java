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
package io.dallen.kingdoms.rpg;

import io.dallen.kingdoms.core.Contract;
import io.dallen.kingdoms.core.Contract.RewardType;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Overrides.KingdomMaterial;
import io.dallen.kingdoms.core.PlayerData;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Storage;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.core.Structures.Types.BuildersHut;
import io.dallen.kingdoms.core.Structures.Vaults.BuildingVault;
import io.dallen.kingdoms.rpg.Contract.BuildingHandler.BuildTask;
import io.dallen.kingdoms.rpg.Contract.Types.BuildContract;
import io.dallen.kingdoms.rpg.Contract.Types.DemolishContract;
import io.dallen.kingdoms.utilities.Blueprint;
import io.dallen.kingdoms.utilities.Blueprint.BlueBlock;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEventHandler;
import io.dallen.kingdoms.utilities.Utils.ItemUtil;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class ContractHandler implements Listener, OptionClickEventHandler {

    private static int currentID = -1;

    private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

    @Getter
    private static HashMap<String, Contract> openInputs = new HashMap<String, Contract>();

    @Getter
    private static ContractHandler inst;

    public ContractHandler() {
        inst = this;
    }

    private ChestGUI createContract = new ChestGUI("", 9 * 1, this) {
        {
            setOption(9 * 0 + 0, KingdomMaterial.DEFAULT.getItemStack(), "Build");
            setOption(9 * 0 + 1, KingdomMaterial.DEFAULT.getItemStack(), "Demolish");
            setOption(9 * 0 + 2, KingdomMaterial.DEFAULT.getItemStack(), "Gather");
            setOption(9 * 0 + 3, KingdomMaterial.DEFAULT.getItemStack(), "Guard");
            setOption(9 * 0 + 4, KingdomMaterial.DEFAULT.getItemStack(), "Patrol");
            setOption(9 * 0 + 5, KingdomMaterial.DEFAULT.getItemStack(), "Transport");
        }
    };

    public static int geCurrentID() {
        currentID++;
        return currentID;
    }
    
    @EventHandler
    public void onNPCLeftClick(NPCLeftClickEvent e){
        ItemStack clickedItem = e.getClicker().getInventory().getItemInMainHand();
        if(KingdomMaterial.CONTRACT_FILLED.equals(clickedItem)){
            if (KingdomsCore.getAllContracts().containsKey(Integer.parseInt(clickedItem.getItemMeta().getLore().get(0)))) {
                if(KingdomsCore.getAllContracts().get(Integer.parseInt(clickedItem.getItemMeta().getLore().get(0))) instanceof BuildContract){
                    BuildContract c = (BuildContract) KingdomsCore.getAllContracts().get(Integer.parseInt(clickedItem.getItemMeta().getLore().get(0)));
                    e.getClicker().getInventory().setItemInMainHand(null);
//                    List<ItemStack> mats = new ArrayList<ItemStack>();
//                    for (Blueprint.BlueBlock[][] bbarr1 : c.getBuilding().getBlocks()) {
//                        for (Blueprint.BlueBlock[] bbarr2 : bbarr1) {
//                            for (Blueprint.BlueBlock bb : bbarr2) {
//                                ItemStack is = new ItemStack(bb.getBlock());
//                                is.getData().setData(bb.getData());
//                                boolean found = false;
//                                for (ItemStack mat : mats) {
//                                    if (mat.isSimilar(is)) {
//                                        mat.setAmount(mat.getAmount() + 1);
//                                        found = true;
//                                    }
//                                }
//                                if (!found) {
//                                    mats.add(is);
//                                }
//                            }
//                        }
//                    }
//                    List<Structure> locs = new ArrayList<Structure>();
//                    for (ItemStack is : mats) {
//                        locs = c.getPlot().getMunicipal().materialLocation(is);
//                        for (Structure st : locs) {
//                            is.setAmount(is.getAmount() - ((BuildingVault) ((Storage) st).getStorage()).getMaterial(is).getAmount());
//                        }
//                        if (is.getAmount() > 0) {
//                            e.getClicker().sendMessage("Your municipality does not have the materials to build this structure");
//                            return;
//                        }
//                    }
                    // for removeal of items:
//                    for (BlueBlock[][] bbarr1 : building.getBlocks()) {
//                    for (BlueBlock[] bbarr2 : bbarr1) {
//                        for (BlueBlock bb : bbarr2) {
//                            ItemStack is = new ItemStack(bb.getBlock());
//                            is.getData().setData(bb.getData());
//                            boolean found = false;
//                            for (ItemStack mat : mats) {
//                                if (mat.isSimilar(is)) {
//                                    mat.setAmount(mat.getAmount() + 1);
//                                    found = true;
//                                }
//                            }
//                            if (!found) {
//                                mats.add(is);
//                            }
//                        }
//                    }
//                }
//                List<Structure> locs = new ArrayList<Structure>();
//                for (ItemStack is : mats) {
//                    locs = p.getMunicipal().materialLocation(is);
//                    for (Structure s : locs) {
//                        is.setAmount(((BuildingVault) ((Storage) s).getStorage()).removeItem(is));
//                    }
//                    if (is.getAmount() > 0) {
//                        e.getPlayer().sendMessage("Your municipality does not have the materials to build this structure");
//                        return;
//                    }
//                }
                    new BuildTask(c.getBuilding(), c.getStartCorner(), 5, e.getNPC());
                }else if(KingdomsCore.getAllContracts().get(Integer.parseInt(clickedItem.getItemMeta().getLore().get(0))) instanceof DemolishContract){
                    DemolishContract c = (DemolishContract) KingdomsCore.getAllContracts().get(Integer.parseInt(clickedItem.getItemMeta().getLore().get(0)));
                    Plot p = c.getPlot();
                    ArrayList<Location> locs = new ArrayList<Location>();
                    ArrayList<BlueBlock> blocks = new ArrayList<BlueBlock>();
                    double halfWidth = p.getWidth() / 2;
                    double halfLength = p.getLength() / 2;
                    for (int x = (int) -halfWidth; x <= Math.ceil(halfWidth) + 1; x++) {
                        for (int z = (int) -halfLength; z <= Math.ceil(halfLength) + 1; z++) {
                            for (int y = 0; y < 50; y++) {
                                if (!p.getCenter().clone().add(x, y, z).getBlock().getType().equals(Material.AIR)) {
                                    locs.add(p.getCenter().clone().add(x, y, z));
                                    blocks.add(new BlueBlock((short) Material.AIR.getId(), (byte) 0));
                                }
                            }
                        }
                    }
                    Location startCorner = new Location(p.getCenter().getWorld(), p.getCenter().getX() - p.getWidth() / 2 + (p.getWidth() % 2 == 0 ? 1 : 0),
                            p.getCenter().getBlockY(), p.getCenter().getBlockZ() - p.getLength() / 2 + (p.getLength() % 2 == 0 ? 1 : 0));
                    e.getClicker().getInventory().setItemInMainHand(null);
                    new BuildTask(locs, blocks, startCorner, 5, e.getNPC());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (((!cooldown.containsKey(e.getPlayer().getName()))
                || (cooldown.containsKey(e.getPlayer().getName()) && cooldown.get(e.getPlayer().getName()) < System.currentTimeMillis() - 100))
                && e.hasItem()
                && (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            cooldown.put(e.getPlayer().getName(), System.currentTimeMillis());
            if (KingdomMaterial.CONTRACT_EMPTY.equals(e.getItem())) {
                createContract.sendMenu(e.getPlayer());
            } else if (KingdomMaterial.CONTRACT_UNFINISHED.equals(e.getItem())) {
                try {
                    LogUtil.printDebug("Unfinished Contract Called");
                    if (KingdomsCore.getAllContracts().containsKey(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)))) {
                        Contract c = KingdomsCore.getAllContracts().get(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)));
                        c.interact(e, false);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ContractHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (KingdomMaterial.CONTRACT_FILLED.equals(e.getItem())) {
                try {
                    LogUtil.printDebug("finished Contract Called");
                    if (KingdomsCore.getAllContracts().containsKey(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)))) {
                        Contract c = KingdomsCore.getAllContracts().get(Integer.parseInt(e.getItem().getItemMeta().getLore().get(0)));
                        if (c.getContractee() != null && c.isFinished()) {
                            e.getPlayer().sendMessage("Maked Contract as completed!");
                            if (c.getRewardType().equals(RewardType.ITEM)) {
                                LogUtil.printDebug("Adding items");
                                for (ItemStack is : (ItemStack[]) c.getReward()) {
                                    if (is != null) {
                                        e.getPlayer().getInventory().addItem(is);
                                    }
                                }
                            } else if (c.getRewardType().equals(RewardType.GOLD)) {
                                PlayerData.getData(e.getPlayer()).addGold((int) c.getReward());
                            } else if (c.getRewardType().equals(RewardType.PLOT)) {

                            }
                            KingdomsCore.getAllContracts().remove(c.getID());
                            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                        } else {
                            c.interact(e, true);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ContractHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void onOptionClick(ChestGUI.OptionClickEvent e) {
        if (e.getMenuName().equals(createContract.getName())) {
            try {
                Class<?> contract = Class.forName("io.dallen.kingdoms.rpg.Contract.Types." + e.getName().replace(" ", "").replace("'", "") + "Contract");
                contract.getConstructor(Player.class, ChestGUI.OptionClickEvent.class).newInstance(e.getPlayer(), e);
            } catch (ClassNotFoundException | IllegalArgumentException | SecurityException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                ex.printStackTrace();
                e.getPlayer().sendMessage("Contract name not found!");
            }
        } else if (e.getMenuName().equals("Select Reward Type")) {
            if (e.getName().equals("Gold")) {
                e.getPlayer().sendMessage("Enter amount of gold in chat");
                openInputs.put(e.getPlayer().getName(), (Contract) e.getMenuData());
            } else if (e.getName().equals("Item")) {
                final Inventory itm = Bukkit.createInventory(e.getPlayer(), 9 * 2, "Contract Reward");
                final ChestGUI.OptionClickEvent ev = e;
                Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomsRPG.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        ev.getPlayer().openInventory(itm);
                        openInputs.put(ev.getPlayer().getName(), (Contract) ev.getMenuData());
                    }
                }, 2);
            }
        }
    }

    public static void setReward(final Contract c, Player p) {
        new ChestGUI("Select Reward Type", InventoryType.HOPPER, ContractHandler.inst) {
            {
                setOption(1, KingdomMaterial.DEFAULT.getItemStack(), "Gold");
                setOption(3, KingdomMaterial.DEFAULT.getItemStack(), "Item");
                setMenuData(c);
            }
        }.sendMenu(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (openInputs.containsKey(event.getPlayer().getName())) {
            Contract contract = openInputs.get(event.getPlayer().getName());
            contract.setRewardType(RewardType.ITEM);
            contract.setReward(event.getInventory().getContents());
            LogUtil.printDebug(Arrays.toString(event.getInventory().getContents()));
            contract.setContractItem(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_FILLED.getItemStack(),
                    contract.getClass().getSimpleName(), String.valueOf(contract.getID())));
            event.getPlayer().setItemInHand(contract.getContractItem());
            openInputs.remove(event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (openInputs.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            Integer amount = -1;
            boolean failed = false;
            try {
                amount = Integer.parseInt(event.getMessage());
            } catch (NumberFormatException ex) {
                event.getPlayer().sendMessage("That is not a valid gold amount, exiting contract setup");
                openInputs.remove(event.getPlayer().getName());
                failed = true;
            }
            if (!failed) {
                Contract contract = openInputs.get(event.getPlayer().getName());
                contract.setRewardType(RewardType.GOLD);
                contract.setReward(amount);
                contract.setContractItem(ItemUtil.setItemNameAndLore(KingdomMaterial.CONTRACT_FILLED.getItemStack(),
                        contract.getClass().getSimpleName(), String.valueOf(contract.getID())));
                event.getPlayer().setItemInHand(contract.getContractItem());
                openInputs.remove(event.getPlayer().getName());
            }
        }
    }
}
