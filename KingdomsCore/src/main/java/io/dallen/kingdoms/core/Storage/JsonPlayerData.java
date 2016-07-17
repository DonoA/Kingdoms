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

import io.dallen.kingdoms.core.PlayerData;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonLocation;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonPlayerData implements SaveType.NativeType.JsonType {

    @Getter
    @Setter
    private String Role;

    @Getter
    @Setter
    private int Kingdom;

    @Getter
    @Setter
    private int Municipal;

    @Getter
    @Setter
    private JsonPlayerVault Vault;

    @Getter
    @Setter
    private int Might;

    @Getter
    @Setter
    private JsonLocation Spawn;

    @Getter
    @Setter
    private HashMap<String, HashMap<String, Object>> modualData = new HashMap<String, HashMap<String, Object>>();

    @Override
    public PlayerData toJavaObject() {//Must load Kingdoms and plots and things first
        PlayerData pd = new PlayerData();
        pd.setMight(Might);
//        pd.setMuted(muted);
        pd.setSpawn(Spawn.toJavaObject());
        pd.setVault(Vault.toJavaObject());
        return pd;
    }
}
