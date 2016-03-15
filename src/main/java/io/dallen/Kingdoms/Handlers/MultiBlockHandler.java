/*
 * This file is part of Kingdoms.
 * 
 * Kingdoms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdoms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdoms.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Handlers;

import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.PlayerData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class MultiBlockHandler implements Listener{
    
    private ChestGUI NewPlotMenu;
    
    private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
    
    public MultiBlockHandler(){
        NewPlotMenu = new ChestGUI("New Plot", InventoryType.HOPPER, new MBOptions()) {{
            setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", "");
            setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Cancel Plot Claim", "");
        }};
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if((!cooldown.containsKey(e.getPlayer())) || 
               (cooldown.containsKey(e.getPlayer()) && cooldown.get(e.getPlayer()) < System.currentTimeMillis() - 500)){//Legit fuck bukkits
                cooldown.put(e.getPlayer(), System.currentTimeMillis());
                if(!e.hasItem()){
                    Block b = e.getClickedBlock();
                    if(b.getType().equals(Material.REDSTONE_TORCH_ON)){
                        final Location l = b.getLocation();
                        final Player p = e.getPlayer();
                        p.sendMessage("Calculating Plot...");
                        new Thread(new Runnable(){
                            @Override
                            public void run(){
                                for(int x = -64; x < 64; x++){
                                    if(l.clone().add(x, 0, 0).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && x != 0){
                                        for(int z = -64; z < 64; z++){
                                            if(l.clone().add(0, 0, z).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && z != 0){
                                                Plot NewPlot = new Plot(Math.abs(x),Math.abs(z),-1,l.add(x/2, 0, z/2), p, null);
                                                NewPlotMenu.setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", NewPlot, p, "Plot size: " + x + ", " + z).sendMenu(p);
                                                return;
                                            }
                                        } 
                                    }
                                }
                                p.sendMessage("Plot not found!");
                            }
                        }).start();

                    }
                }else if(e.getItem().hasItemMeta()){

                }
            }
        }
    }
    
    
    private static class MBOptions implements OptionClickEventHandler{
    
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("New Plot")){
                if(e.getName().equalsIgnoreCase("Confirm and Claim Plot")){
                    Plot p = (Plot) e.getData();
                    Plot.getAllPlots().add(p);
                    PlayerData pd = PlayerData.getData(e.getPlayer());
//                    pd.getPlots().add(p);
                    e.getPlayer().sendMessage("You have claimed this plot");
                }
            }

        }
    }
    
}
