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
package io.dallen.kingdoms.core.Storage;

import io.dallen.kingdoms.core.Structures.Vaults.PlayerVault;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonItemStack;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonPlayerVault implements SaveType.NativeType.JsonType {

    @Getter
    @Setter
    private UUID owner;

    @Getter
    @Setter
    private JsonItemStack[] storage;

    @Getter
    @Setter
    private int size;

    @Override
    public PlayerVault toJavaObject() {
        PlayerVault pv = new PlayerVault(Bukkit.getPlayer(owner), size);
        ArrayList<ItemStack> iss = new ArrayList<ItemStack>();
        for (JsonItemStack jis : storage) {
            if (jis != null) {
                iss.add(jis.toJavaObject());
            } else {
                iss.add(null);
            }
        }
        pv.getStorage().setContents(iss.toArray(new ItemStack[]{}));
        return pv;
    }

}
