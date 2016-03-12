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
package io.dallen.Kingdoms.Util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 *
 * @author donoa_000
 */
public class BannerUtil{
    
    public static ItemStack setBannerLetter(char l, ItemStack item){
        if(!item.getType().equals(Material.BANNER)){
            LogUtil.printErr("Cannot set letter of non banner item");
            return null;
        }
        BannerMeta b = (BannerMeta) item.getItemMeta();
        b.setBaseColor(DyeColor.WHITE);
        switch(l){ //https://imgur.com/gallery/D9KhL
            case 'a':
                break;
            case 'b':
                break;
            case 'c':
                break;
            case 'd':
                break;
            case 'e':
                break;
                
        }
        item.setItemMeta(b);
        return item;
    }
    
}
