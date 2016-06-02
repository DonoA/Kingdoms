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
package io.dallen.Kingdoms.Storage.JsonClasses;

import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonLocation;
import io.dallen.Kingdoms.Commands.MuteCommand.MuteClass;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Storage.SaveTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonPlayerData implements SaveTypes.JsonType{
    @Getter @Setter
    private String Role;
    
    @Getter @Setter
    private int Kingdom;
    
    @Getter @Setter
    private int Municipal;
    
    @Getter @Setter
    private JsonPlayerVault Vault;
    
    @Getter @Setter
    private int Might;
    
    @Getter @Setter
    private JsonLocation Spawn;
    
    @Getter @Setter
    private MuteClass muted;
    
    @Override
    public PlayerData toJavaObject(){//Must load Kingdoms and plots and things first
        PlayerData pd = new PlayerData();
        pd.setMight(Might);
        pd.setMuted(muted);
        pd.setSpawn(Spawn.toJavaObject());
        pd.setVault(Vault.toJavaObject());
        return pd;
    }
}
