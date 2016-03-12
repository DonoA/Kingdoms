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
package io.dallen.Kingdoms.MenuHandlers;

import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class MainMenuHandler implements CommandExecutor, OptionClickEventHandler{
    
    public static ChestGUI MainMenu;
    
    public MainMenuHandler(){
        MainMenu = new ChestGUI("MainMenu", 5, this) {{
            setOption(0, new ItemStack(Material.ACACIA_DOOR), "Item", "Lore");
        }};
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        return false;
    }

    @Override
    public void onOptionClick(ChestGUI.OptionClickEvent event) {
        
    }
    
}
