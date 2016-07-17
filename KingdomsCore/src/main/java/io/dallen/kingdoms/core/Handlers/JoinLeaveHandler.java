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
package io.dallen.kingdoms.core.Handlers;

import io.dallen.kingdoms.core.PlayerData;
import io.dallen.kingdoms.core.Storage.DataLoadHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author donoa_000
 */
public class JoinLeaveHandler implements Listener {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerData.getPlayerDat().put(e.getPlayer(), DataLoadHelper.LoadPlayerData(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { //This should do some kind of fancy cache for relogs (maybe like 5 min ttl)
        DataLoadHelper.SavePlayerData(PlayerData.getData(e.getPlayer()));
        PlayerData.getPlayerDat().remove(e.getPlayer());
    }
}
