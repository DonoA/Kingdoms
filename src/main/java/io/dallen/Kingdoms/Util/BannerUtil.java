/*
 * Copyright 2016 Donovan Allen
 *
 * This file is part of Kingdoms for the Morphics Network.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.Kingdoms.Util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
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
                b.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
                b.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT));
                b.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP));
                b.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
                b.addPattern(new Pattern(DyeColor.WHITE, PatternType.BORDER));
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
