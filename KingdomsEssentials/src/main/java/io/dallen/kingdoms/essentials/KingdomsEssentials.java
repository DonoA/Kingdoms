/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of KingdomsEssentials for the Morphics Network.
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
package io.dallen.kingdoms.essentials;

import io.dallen.kingdoms.core.Handlers.Party;
import io.dallen.kingdoms.essentials.Commands.AdminCommands;
import io.dallen.kingdoms.essentials.Commands.GeneralCommands;
import io.dallen.kingdoms.essentials.Commands.KingdomCommands;
import io.dallen.kingdoms.essentials.Commands.ModerationCommands;
import io.dallen.kingdoms.essentials.Commands.MuteCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Donovan Allen
 */
public class KingdomsEssentials extends JavaPlugin{
    
    @Getter
    private static Runtime runtime = Runtime.getRuntime();
    
    @Getter
    private static KingdomsEssentials Plugin;
    
    @Override
    public void onEnable(){
        Plugin = this;
        ModerationCommands moderation = new ModerationCommands();
        GeneralCommands general = new GeneralCommands();
        AdminCommands admin = new AdminCommands();
        this.getCommand("crash").setExecutor(admin);
        this.getCommand("editschem").setExecutor(admin);
        this.getCommand("strack").setExecutor(admin);
        this.getCommand("message").setExecutor(general);
        this.getCommand("reply").setExecutor(general);
        this.getCommand("info").setExecutor(general);
        this.getCommand("help").setExecutor(general);
        this.getCommand("list").setExecutor(general);
        this.getCommand("self").setExecutor(general);
        this.getCommand("is").setExecutor(general);
        this.getCommand("setpost").setExecutor(general);
        this.getCommand("where").setExecutor(moderation);
        this.getCommand("ban").setExecutor(moderation);
        this.getCommand("unban").setExecutor(moderation);
        this.getCommand("ipban").setExecutor(moderation);
        this.getCommand("unipban").setExecutor(moderation);
        this.getCommand("kick").setExecutor(moderation);
        this.getCommand("tmpban").setExecutor(moderation);
        this.getCommand("invspy").setExecutor(moderation);
        this.getCommand("fjail").setExecutor(moderation);
        this.getCommand("vanish").setExecutor(moderation);
        this.getCommand("uuid").setExecutor(moderation);
        this.getCommand("broadcast").setExecutor(moderation);
        this.getCommand("kingdom").setExecutor(new KingdomCommands());
        this.getCommand("chat").setExecutor(new ChatHandler());
        this.getCommand("party").setExecutor(new Party.PartyCommands());
        this.getCommand("mute").setExecutor(new MuteCommand());
    }
    
    @Override
    public void onDisable(){
    }
}
