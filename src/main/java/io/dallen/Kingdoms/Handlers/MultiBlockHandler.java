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
                                Point last = start;
                                for(x = (int) last.getX() - 64; x < last.getX() + 64; x++){
                                    if(new Location(l.getWorld(), x, y, last.getY()).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && x != last.getX()){
                                        currentX = new Point(x,z);
                                        corners.add(currentX);
                                        last = currentX;
                                        LogUtil.printDebug(last);
                                        for(z = (int) last.getY() - 64; z < last.getY() + 64; z++){
                                            if(new Location(l.getWorld(), last.getX(), y, z).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && z != last.getY()){
                                                currentZ = new Point(x,z);
                                                corners.add(new Point(x,z));
                                                last = currentZ;
                                                LogUtil.printDebug(last);
                                            }
                                        }
                                    }
                                }
                                if(currentX == null || currentZ == null){
                                    LogUtil.printDebug("Init not found");
                                    p.sendMessage("Plot not found!");
                                    return;
                                }
                                LogUtil.printDebug(Arrays.toString(corners.toArray()));
                                boolean finished = false;
                                finish:
                                while(!finished){
                                    currentX = null;
                                    currentZ = null;
                                    LogUtil.printDebug(last);
                                    for(x = (int) last.getX() - 64; x < last.getX() + 64; x++){
                                        if(new Location(l.getWorld(), x, y, last.getY()).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && x != last.getX()){
                                            currentX = new Point(x,(int)last.getY());
                                            if(corners.contains(currentX)){
                                                finished = true;
                                                break finish;
                                            }
                                            corners.add(currentX);
                                            last = currentX;
                                            LogUtil.printDebug(last);
                                            for(z = (int) last.getY() - 64; z < last.getY() + 64; z++){
                                                if(new Location(l.getWorld(), last.getX(), y, z).getBlock().getType().equals(Material.REDSTONE_TORCH_ON) && z != last.getY()){
                                                    currentZ = new Point((int)last.getX(),z);
                                                    if(corners.contains(currentZ)){
                                                        finished = true;
                                                        break finish;
                                                    }
                                                    corners.add(currentZ);
                                                    last = currentZ;
                                                    LogUtil.printDebug(last);
                                                    break finish;
                                                }
                                            }
                                        }
                                    }
                                    if(currentX == null || currentZ == null){
                                        LogUtil.printDebug(Arrays.toString(corners.toArray()));
                                        LogUtil.printDebug("While not found");
                                        p.sendMessage("Plot not found!");
                                        return;
                                    }
                                    LogUtil.printDebug(Arrays.toString(corners.toArray()));
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
                                    NewPlotMenu.setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", NewPlot, p, "Plot size: " + x + ", " + z).sendMenu(p);
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
                            if(bounds.contains(new Point(x,z))){
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
