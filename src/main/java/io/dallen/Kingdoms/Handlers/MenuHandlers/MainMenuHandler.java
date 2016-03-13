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
package io.dallen.Kingdoms.Handlers.MenuHandlers;

import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class MainMenuHandler implements CommandExecutor, OptionClickEventHandler{
    
    public static ChestGUI HomeMenu;
    
    public static ChestGUI MainMenu;
    public static ChestGUI EncyclopediaMenu;
    public static ChestGUI PlotMenu;
    public static ChestGUI ManagementMenu;
    public static ChestGUI PlayerMenu;
    public static ChestGUI ServerMenu;
    
    @Getter
    private static HashMap<Player, ArrayList<ChestGUI>> history = new HashMap<Player, ArrayList<ChestGUI>>();
    
    public MainMenuHandler(){
        HomeMenu = new ChestGUI("Home Menu", 3, this) {{
            setOption(1*9 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Encyclopedia", "");
            setOption(1*9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Plot Menu", "");
            setOption(1*9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "NPC Menu", "");
            setOption(1*9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Player Stats", "");
            setOption(1*9 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Server Selector", "");
            setOption(1*9 + 7, new ItemStack(Material.ENCHANTED_BOOK), "Vault", "");
            setOption(2*9, new ItemStack(Material.WORKBENCH), "Main Menu", "");
        }};
        MainMenu = new ChestGUI("Main Menu", 3, this) {{
            setOption(1*9 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Textures", "");
            setOption(1*9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Disconnect", "");
            setOption(1*9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Donate", "");
            setOption(1*9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Vote", "");
            setOption(1*9 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Website", "");
            setOption(2*9, new ItemStack(Material.ARROW), "Back", "");
            setOption(3*9-1, new ItemStack(Material.BED), "Home Menu", "");
        }};
        EncyclopediaMenu = new ChestGUI("Encyclopedia", 3, this) {{
            setOption(1*9 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Kingdoms", "");
            setOption(1*9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Roles", "");
            setOption(1*9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Levels", "");
            setOption(1*9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Resources", "");
            setOption(1*9 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Biomes", "");
            setOption(2*9, new ItemStack(Material.ARROW), "Back", "");
            setOption(3*9-1, new ItemStack(Material.BED), "Home Menu", "");
        }};
        PlotMenu = new ChestGUI("Plot Menu", 3, this) {{
            setOption(1*9 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Claim", "");
            setOption(1*9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Expand", "");
            setOption(1*9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Build", "");
            setOption(1*9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Borders", "");
            setOption(1*9 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Destroy", "");
            setOption(2*9, new ItemStack(Material.ARROW), "Back", "");
            setOption(3*9-1, new ItemStack(Material.BED), "Home Menu", "");
        }};
        ManagementMenu = new ChestGUI("Management Menu", 3, this) {{
            setOption(1*9 + 2, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 6, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(2*9, new ItemStack(Material.ARROW), "Back", "");
            setOption(3*9-1, new ItemStack(Material.BED), "Home Menu", "");
        }};
        PlayerMenu = new ChestGUI("Player Menu", 3, this) {{
            setOption(1*9 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Stats", "");
            setOption(1*9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Level", "");
            setOption(1*9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Vault", "");
            setOption(1*9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Armor", "");
            setOption(1*9 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Nickname", "");
            setOption(2*9, new ItemStack(Material.ARROW), "Back", "");
            setOption(3*9-1, new ItemStack(Material.BED), "Home Menu", "");
        }};
        ServerMenu = new ChestGUI("Server Selector", 3, this) {{
            setOption(1*9 + 2, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 3, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 4, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 5, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(1*9 + 6, new ItemStack(Material.ENCHANTED_BOOK), "", "");
            setOption(2*9, new ItemStack(Material.ARROW), "Back", "");
            setOption(3*9-1, new ItemStack(Material.BED), "Home Menu", "");
        }};
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(sender instanceof Player){
            HomeMenu.sendMenu((Player) sender);
        }
        return true;
    }

    @Override
    public void onOptionClick(ChestGUI.OptionClickEvent e) {
        if(e.getMenuName().equalsIgnoreCase("Home Menu")){
            if(e.getName().equalsIgnoreCase("Encyclopedia")){
                
                e.setNext(EncyclopediaMenu);
            }else if(e.getName().equalsIgnoreCase("Plot Menu")){
                
                e.setNext(PlotMenu);
            }else if(e.getName().equalsIgnoreCase("NPC Menu")){
                
                e.setNext(ManagementMenu);
            }else if(e.getName().equalsIgnoreCase("Player Stats")){
                
                e.setNext(PlayerMenu);
            }else if(e.getName().equalsIgnoreCase("Server Selector")){
                
                e.setNext(ServerMenu);
            }else if(e.getName().equalsIgnoreCase("Vault")){
                e.setClose(true);
    //            ServerMenu);
            }else if(e.getName().equalsIgnoreCase("Main Menu")){
                
                e.setNext(MainMenu);
            }
        }else if(e.getMenuName().equalsIgnoreCase("Encyclopedia")){
            if(e.getName().equalsIgnoreCase("Kingdoms")){
                
            }else if(e.getName().equalsIgnoreCase("Roles")){
                
            }else if(e.getName().equalsIgnoreCase("Levels")){
                
            }else if(e.getName().equalsIgnoreCase("Resources")){
                
            }else if(e.getName().equalsIgnoreCase("Biomes")){
                
            }else if(e.getName().equalsIgnoreCase("Back")){
                
                e.setNext(HomeMenu);
            }else if(e.getName().equalsIgnoreCase("Home Menu")){
                
                e.setNext(MainMenu);
            }
        }else if(e.getMenuName().equalsIgnoreCase("Plot Menu")){
            if(e.getName().equalsIgnoreCase("Claim")){
                
                e.setNext(EncyclopediaMenu);
            }else if(e.getName().equalsIgnoreCase("Expand")){
                
                e.setNext(PlotMenu);
            }else if(e.getName().equalsIgnoreCase("Build")){
                
                e.setNext(ManagementMenu);
            }else if(e.getName().equalsIgnoreCase("Borders")){
                
                e.setNext(PlayerMenu);
            }else if(e.getName().equalsIgnoreCase("Destroy")){
                
                e.setNext(ServerMenu);
            }else if(e.getName().equalsIgnoreCase("Back")){
                
                e.setNext(HomeMenu);
            }else if(e.getName().equalsIgnoreCase("Home Menu")){
                
                e.setNext(MainMenu);
            }
        }else if(e.getMenuName().equalsIgnoreCase("Management Menu")){
            if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(EncyclopediaMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(PlotMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(ManagementMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(PlayerMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(ServerMenu);
            }else if(e.getName().equalsIgnoreCase("Back")){
                
                e.setNext(HomeMenu);
            }else if(e.getName().equalsIgnoreCase("Home Menu")){
                
                e.setNext(MainMenu);
            }
        }else if(e.getMenuName().equalsIgnoreCase("Player Menu")){
            if(e.getName().equalsIgnoreCase("Stats")){
                
                e.setNext(EncyclopediaMenu);
            }else if(e.getName().equalsIgnoreCase("Level")){
                
                e.setNext(PlotMenu);
            }else if(e.getName().equalsIgnoreCase("Vault")){
                
                e.setNext(ManagementMenu);
            }else if(e.getName().equalsIgnoreCase("Armor")){
                
                e.setNext(PlayerMenu);
            }else if(e.getName().equalsIgnoreCase("Nickname")){
                
                e.setNext(ServerMenu);
            }else if(e.getName().equalsIgnoreCase("Back")){
                
                e.setNext(HomeMenu);
            }else if(e.getName().equalsIgnoreCase("Home Menu")){
                
                e.setNext(MainMenu);
            }
        }else if(e.getMenuName().equalsIgnoreCase("Server Selector")){
            if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(EncyclopediaMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(PlotMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(ManagementMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(PlayerMenu);
            }else if(e.getName().equalsIgnoreCase("")){
                
                e.setNext(ServerMenu);
            }else if(e.getName().equalsIgnoreCase("Back")){
                
                e.setNext(HomeMenu);
            }else if(e.getName().equalsIgnoreCase("Home Menu")){
                
                e.setNext(MainMenu);
            }
        }
    }
    
}
