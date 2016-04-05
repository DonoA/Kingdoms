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

package io.dallen.Kingdoms.Util.JsonClasses;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonEnchantmentMeta {
    
    @Getter @Setter
    private HashMap<String, Integer> enchants = new HashMap();

    public JsonEnchantmentMeta(ItemMeta meta) {
        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            enchants.put(entry.getKey().getName(), entry.getValue());
        }
    }
}
