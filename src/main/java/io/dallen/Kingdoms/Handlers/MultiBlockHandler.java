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

import com.google.common.primitives.Ints;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
                    if(b.getType().equals(Material.REDSTONE_TORCH_ON)){ // TESTING: REDSTONE_TORCH_ON
                        final Location l = b.getLocation();
                        final Player p = e.getPlayer();
                        p.sendMessage("Calculating Plot...");
                        new Thread(new Runnable(){
                            @Override
                            public void run(){ // Calculate redstone torch plot
                                ArrayList<Point> corners = new ArrayList<Point>();
                                Point start = LocationUtil.asPoint(l);
                                corners.add(start);
                                //Redstone direction check needs to be first
                                boolean complete = false;
                                boolean direction = true;//true:X, false:Z
                                //Start with X direction
                                //Then check Z
                                //continue until the found point is also the start point
                                Point last; // the last confirmed point
                                Point current = start; // the current point being tested delt with
                                while(!complete){
                                    last = current; // cycle points
                                    current = null;
                                    for(int i = 1; i<64 && current == null; i++){//Still doesnt include the border // inorder to fix this, need to add lines for the border to calculate full plot
                                        Location neg = new Location(l.getWorld(), (direction ? last.getX() - i : last.getX()), l.getBlockY(), (!direction ? last.getY() - i : last.getY())); // the point in the negative direction
                                        Location pos = new Location(l.getWorld(), (direction ? last.getX() + i : last.getX()), l.getBlockY(), (!direction ? last.getY() + i : last.getY())); // the point in the positive direction
                                        if(neg.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){
                                            LogUtil.printDebug("neg triggered!");
                                            LogUtil.printDebug(LocationUtil.asPoint(neg));
                                            current = LocationUtil.asPoint(neg);
                                            if(current.equals(start)){
                                                complete = true;
                                            }
                                        }
                                        if(pos.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){
                                            LogUtil.printDebug("pos triggered!");
                                            LogUtil.printDebug(new Point(pos.getBlockX(), pos.getBlockZ()));
                                            if(current != null){
                                                p.sendMessage("Plot cannot be deturmined, please use the redstone dust method");
                                                return;
                                            }
                                            if(!complete){
                                                current = new Point(pos.getBlockX(), pos.getBlockZ());
                                                if(current.equals(start)){
                                                    complete = true;
                                                }
                                            }
                                        }
                                    }
                                    if(current == null){
                                        p.sendMessage("Could not calculate plot");
                                        return;
                                    }
                                    if(!complete){
                                        corners.add(current);
                                    }
                                    direction = !direction;
                                }
                                LogUtil.printDebug(Arrays.toString(corners.toArray()));
                                int[] Xs = new int[corners.size()];
                                int[] Zs = new int[corners.size()];
                                int i = 0;
                                for(Point p : corners){
                                    Xs[i] = (int) p.getX();
                                    Zs[i] = (int) p.getY();
                                    i++;
                                }
                                Plot NewPlot = new Plot(new Polygon(Xs, Zs, corners.size()), LocationUtil.asLocation(LocationUtil.calcCenter(corners.toArray(new Point[1])), l.getWorld(), l.getBlockY()), p, null);
                                if(NewPlot.isValid()){
                                    NewPlotMenu.setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", NewPlot, p, "Plot size: " + -1 + ", " + -1).sendMenu(p);
                                }else{
                                    p.sendMessage("Invalid Plot");
                                }
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
                    final Plot p = (Plot) e.getData();
                    Plot.getAllPlots().add(p);
                    PlayerData pd = PlayerData.getData(e.getPlayer());
                    pd.getPlots().add(p);
                    e.getPlayer().sendMessage("You have claimed this plot");
                    e.getPlayer().sendMessage("Setting base");
                    final Polygon bounds = p.getBase();
                    int Xmax = Ints.max(bounds.xpoints);
                    int Zmax = Ints.max(bounds.ypoints);
                    LogUtil.printDebug(Arrays.toString(bounds.xpoints));
                    LogUtil.printDebug("size " + Xmax + ", " + Zmax + " to " + Ints.min(bounds.xpoints) + ", " + Ints.min(bounds.ypoints));
                    for(int x = Ints.min(bounds.xpoints); x <= Xmax; x++){
                        for(int z = Ints.min(bounds.ypoints); z <= Zmax; z++){
                            LogUtil.printDebug("try " + new Point(x,z).toString());
                            if(bounds.contains(new Point(x,z)) || (bounds.contains(new Point(x-1,z)) || bounds.contains(new Point(x,z-1)) || bounds.contains(new Point(x-1,z-1)))){
                                LogUtil.printDebug("setting " + new Point(x,z).toString());
                                Location l = new Location(p.getCenter().getWorld(), x, p.getCenter().getBlockY()-1, z);
                                l.getBlock().setType(Material.DIRT);
                                l.getBlock().setData((byte) 1);
                            }
                        }
                    }
                }
            }

        }
    }
    
}
