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
import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
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
                            public void run(){ // Calculate redstone torch plot
                                ArrayList<Point> corners = new ArrayList<Point>();
                                Point start = LocationUtil.asPoint(l);
                                corners.add(start);
                                int x = l.getBlockX();
                                int y = l.getBlockY();
                                int z = l.getBlockZ();
                                Point currentX = null;
                                Point currentZ = null;
                                Point lastX = null;
                                Point lastZ = null;
                                for(x = (int) start.getX() - 64; x < start.getX() + 64; x++){
                                    if(new Location(l.getWorld(), x, y, z).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && x != l.getBlockX()){
                                        currentX = new Point(x,z);
                                        corners.add(currentX);
                                        for(z = (int) start.getY() - 64; z < start.getY() + 64; z++){
                                            if(new Location(l.getWorld(), x, y, z).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && x != l.getBlockZ()){
                                                currentZ = new Point(x,z);
                                                corners.add(new Point(x,z));
                                            }
                                        }
                                    }
                                }
                                if(currentX == null || currentZ == null){
                                    p.sendMessage("Plot not found!");
                                    return;
                                }
                                while(!start.equals(currentX) || !start.equals(currentZ)){
                                    lastX = currentX;
                                    lastZ = currentZ;
                                    currentX = null;
                                    currentZ = null;
                                    for(x = (int) lastX.getX() - 64; x < lastX.getX() + 64; x++){
                                        if(new Location(l.getWorld(), x, y, z).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && x != lastX.getX()){
                                            currentX = new Point(x,z);
                                            corners.add(currentX);
                                            for(z = (int) lastZ.getY() - 64; z < lastZ.getY() + 64; z++){
                                                if(new Location(l.getWorld(), x, y, z).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && x != lastZ.getY()){
                                                    currentZ = new Point(x,z);
                                                    corners.add(new Point(x,z));
                                                }
                                            }
                                        }
                                    }
                                    if(currentX == null || currentZ == null){
                                        p.sendMessage("Plot not found!");
                                        return;
                                    }
                                }
                                int[] Xs = new int[corners.size()];
                                int[] Zs = new int[corners.size()];
                                int i = 0;
                                for(Point p : corners){
                                    Xs[0] = (int) p.getX();
                                    Zs[0] = (int) p.getY();
                                    i++;
                                }
                                Plot NewPlot = new Plot(new Polygon(Xs, Zs, corners.size()), LocationUtil.asLocation(LocationUtil.calcCenter(corners.toArray(new Point[1])), l.getWorld()), p, null);
                                NewPlotMenu.setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", NewPlot, p, "Plot size: " + x + ", " + z).sendMenu(p);
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
