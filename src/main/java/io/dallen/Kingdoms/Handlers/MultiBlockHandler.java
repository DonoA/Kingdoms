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
import io.dallen.Kingdoms.Kingdom.Structures.Types.Barracks;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Storeroom;
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
    
    private ChestGUI SetPlotType;
    
    private ChestGUI ViewPlotMenu;
    
    private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
    
    public MultiBlockHandler(){
        NewPlotMenu = new ChestGUI("New Plot", InventoryType.HOPPER, new MBOptions()) {{
            setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", "");
            setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Cancel Plot Claim", "");
        }};
        
        SetPlotType = new ChestGUI("Set Plot Type", 4, new MBOptions()) {{
            setOption(9*0 + 1, new ItemStack(Material.ENCHANTED_BOOK), "Storeroom", "");
            setOption(9*0 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Barracks", "");
            setOption(9*0 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Training Ground", "");
            setOption(9*0 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Armory", "");
            setOption(9*0 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Blacksmith", "");
            setOption(9*0 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Farm", "");
            setOption(9*1 + 1, new ItemStack(Material.ENCHANTED_BOOK), "Treasury", "");
            setOption(9*1 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Bank", "");
            setOption(9*1 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Stable", "");
            setOption(9*1 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Dungeon", "");
            setOption(9*1 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Marketplace", "");
            setOption(9*1 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Court", "");
            setOption(9*2 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Wall", "");
            setOption(9*2 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Wall with Door", "");
            setOption(9*2 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Corner", "");
            setOption(9*2 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Tower", "");
            setOption(9*3 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Contract", "");
            setOption(9*3 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Demolish", "");
            setOption(9*3 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Erase", "");
        }};
        
        ViewPlotMenu = new ChestGUI("Plot Info", InventoryType.HOPPER, new MBOptions()) {{
            setOption(2, new ItemStack(Material.ENCHANTED_BOOK), "No current contracts avalible", "");
        }};
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if((!cooldown.containsKey(e.getPlayer())) || 
               (cooldown.containsKey(e.getPlayer()) && cooldown.get(e.getPlayer()) < System.currentTimeMillis() - 500)){
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
                                Point current = start; // the current point being delt with
                                while(!complete){
                                    last = current; // cycle points
                                    current = null;
                                    for(int i = 1; i<64 && current == null; i++){
                                        Location neg = new Location(l.getWorld(), (direction ? last.getX() - i : last.getX()), l.getBlockY(), (!direction ? last.getY() - i : last.getY())); // the point in the negative direction
                                        Location pos = new Location(l.getWorld(), (direction ? last.getX() + i : last.getX()), l.getBlockY(), (!direction ? last.getY() + i : last.getY())); // the point in the positive direction
                                        if(neg.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){ // Test in the negative direction
                                            current = LocationUtil.asPoint(neg);
                                            if(current.equals(start)){// If the found point is the start point
                                                complete = true;
                                            }
                                        }
                                        if(pos.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){
                                            if(current != null){ // If a point has already been found
                                                p.sendMessage("Plot cannot be deturmined, please use the redstone dust method");
                                                return;
                                            }
                                            if(!complete){ // If the loop is complete
                                                current = new Point(pos.getBlockX(), pos.getBlockZ());
                                                if(current.equals(start)){
                                                    complete = true;
                                                }
                                            }
                                        }
                                    }
                                    if(current == null || corners.contains(current)){ // If the test failed to located the shape
                                        p.sendMessage("Could not calculate plot");
                                        return;
                                    }
                                    if(!complete){//dont add the start point into the list again
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
                                for(Plot plot : Plot.getAllPlots()){
                                    if(plot.getCenter().equals(NewPlot.getCenter())){
                                        if(plot.getOwner().equals(p)){
                                            p.sendMessage("You already own this plot!");
                                        }else{
                                            p.sendMessage("This plot has already been claimed!");
                                        }
                                    }
                                }
                                if(NewPlot.isValid()){
                                    NewPlotMenu.setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", NewPlot, p, "Plot size: " + -1 + ", " + -1).sendMenu(p);
                                }else{
                                    p.sendMessage("Invalid Plot");
                                }
                            }
                        }).start();

                    }
                }else if(e.getItem().hasItemMeta()){
                    if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Multi Tool")){
                        Plot p = Plot.inPlot(e.getPlayer().getLocation());
                        if(p.getOwner().equals(e.getPlayer())){
                            
                        }else{
                            e.getPlayer().sendMessage("send contracts!");
                        }
                    }
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
            }else if(e.getMenuName().equalsIgnoreCase("Set Plot Type")){
                Plot p = Plot.inPlot(e.getPlayer().getLocation());
                PlayerData pd = PlayerData.getData(e.getPlayer());
                if(e.getName().equalsIgnoreCase("Storeroom")){
                    pd.getPlots().remove(p);
                    Storeroom store = new Storeroom(p);
                    pd.getPlots().add(store);
                    e.getPlayer().sendMessage("You have assigned this plot to be a Storeroom.");
                    e.getPlayer().sendMessage("The max capaity of this plot is " + store.getMaxCapacity());
                    /* if(you dont have the resources in your builders hut){
                        e.getPlayer().sendMessage("You dont have the needed resources to build this structure fully");
                    */
                }else if(e.getName().equalsIgnoreCase("Barracks")){
                    pd.getPlots().remove(p);
                    Barracks barracks = new Barracks(p);
                    pd.getPlots().add(barracks);
                    e.getPlayer().sendMessage("You have assigned this plot to be a Barracks.");
                    /* if(you dont have the resources in your builders hut){
                        e.getPlayer().sendMessage("You dont have the needed resources to build this structure fully");
                    */
                }else if(e.getName().equalsIgnoreCase("Training Ground")){
                    
                }else if(e.getName().equalsIgnoreCase("Armory")){
                    
                }else if(e.getName().equalsIgnoreCase("Blacksmith")){
                    
                }else if(e.getName().equalsIgnoreCase("Farm")){
                    
                }else if(e.getName().equalsIgnoreCase("Treasury")){
                    
                }else if(e.getName().equalsIgnoreCase("Bank")){
                    
                }else if(e.getName().equalsIgnoreCase("Stable")){
                    
                }else if(e.getName().equalsIgnoreCase("Dungeon")){
                    
                }else if(e.getName().equalsIgnoreCase("Marketplace")){
                    
                }else if(e.getName().equalsIgnoreCase("Court")){
                    
                }else if(e.getName().equalsIgnoreCase("Wall")){
                    
                }else if(e.getName().equalsIgnoreCase("Wall with Door")){
                    
                }else if(e.getName().equalsIgnoreCase("Corner")){
                    
                }else if(e.getName().equalsIgnoreCase("Tower")){
                    
                }else if(e.getName().equalsIgnoreCase("Contract")){
                    
                }else if(e.getName().equalsIgnoreCase("Demolish")){
                    
                }else if(e.getName().equalsIgnoreCase("Erase")){
                    
                }
            }
        }
    }
    
}
