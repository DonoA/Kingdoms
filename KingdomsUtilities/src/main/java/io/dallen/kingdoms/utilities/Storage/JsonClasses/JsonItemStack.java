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
package io.dallen.kingdoms.utilities.Storage.JsonClasses;

import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonItemStack implements SaveType.NativeType {

    @Getter
    @Setter
    private String material;
    @Getter
    @Setter
    private int amount;
    @Getter
    @Setter
    private String displayName;
    @Getter
    @Setter
    private short durability;
    @Getter
    @Setter
    private List<String> lore;
    @Getter
    @Setter
    private JsonBookMeta bookMeta;
    @Getter
    @Setter
    private JsonLeatherArmorMeta armorMeta;
    @Getter
    @Setter
    private JsonEnchantmentMeta enchantmentMeta;

    public JsonItemStack(ItemStack i) {
        if (i == null) {
            this.material = Material.AIR.name();
            this.amount = 1;
            return;
        }
        this.material = i.getType().name();
        this.amount = i.getAmount();
        this.durability = i.getDurability();
        if (i.hasItemMeta()) {
            if (i.getItemMeta().hasDisplayName()) {
                this.displayName = i.getItemMeta().getDisplayName();
            }
            if (i.getItemMeta().hasLore()) {
                this.lore = i.getItemMeta().getLore();
            }
            if (i.getItemMeta() instanceof BookMeta) {
                this.bookMeta = new JsonBookMeta((BookMeta) i.getItemMeta());
            }
            if (i.getItemMeta() instanceof LeatherArmorMeta) {
                this.armorMeta = new JsonLeatherArmorMeta((LeatherArmorMeta) i.getItemMeta());
            }
            if (i.getItemMeta().hasEnchants()) {
                this.enchantmentMeta = new JsonEnchantmentMeta(i.getItemMeta());
            }
        }
    }

    /**
     * Get a bukkit copy of this object
     *
     * @return A bukkit ItemStack object of this object
     */
    public ItemStack toJavaObject() {
        ItemStack out = new ItemStack(Material.valueOf(material));
        out.setAmount(amount);
        out.setDurability(durability);
        if (displayName != null || lore != null) {
            ItemMeta meta = out.getItemMeta();
            if (lore != null) {
                meta.setLore(lore);
            }
            if (displayName != null) {
                meta.setDisplayName(displayName);
            }
            out.setItemMeta(meta);
        }
        if (bookMeta != null) {
            BookMeta meta = (BookMeta) out.getItemMeta();
            meta.setAuthor(bookMeta.getTitle());
            meta.setPages(bookMeta.getPages());
            meta.setTitle(bookMeta.getTitle());
            if (bookMeta.getLore() != null) {
                meta.setLore(bookMeta.getLore());
            }
            out.setItemMeta(meta);
        }
        if (armorMeta != null) {
            LeatherArmorMeta meta = (LeatherArmorMeta) out.getItemMeta();
            meta.setColor(Color.fromRGB(armorMeta.getRgb()));
            if (armorMeta.getDisplayName() != null) {
                meta.setDisplayName(armorMeta.getDisplayName());
            }
            if (armorMeta.getLore() != null) {
                meta.setLore(armorMeta.getLore());
            }
            out.setItemMeta(meta);
        }
        if (enchantmentMeta != null) {
            for (Map.Entry<String, Integer> entry : enchantmentMeta.getEnchants().entrySet()) {
                out.addUnsafeEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue());
            }
        }
        return out;
    }
}
