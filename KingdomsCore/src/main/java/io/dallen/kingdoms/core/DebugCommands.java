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
package io.dallen.kingdoms.core;

import io.dallen.kingdoms.core.Handlers.DecayHandler.ChangeTracker;
import io.dallen.kingdoms.core.Storage.DataLoadHelper;
import io.dallen.kingdoms.utilities.KingdomsUtilities;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Donovan Allen
 */
public class DebugCommands implements CommandExecutor {

    private PluginUpdateThread update;

    public DebugCommands(File build) {
        if (KingdomsCore.getPlugin().getConfig().getBoolean("debug.autoupdate")) {
            update = new PluginUpdateThread(build);
            update.start();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setskins")) {
            if (args[0].equalsIgnoreCase("default")) {
                KingdomsCore.getSkinHandler().setRunning(false);
            } else {
                KingdomsCore.getSkinHandler().setRunning(true);
                KingdomsCore.getSkinHandler().setSkin(args[0]);
                Bukkit.getScheduler().runTaskAsynchronously(KingdomsCore.getPlugin(), KingdomsCore.getSkinHandler());
            }
        } else if (cmd.getName().equalsIgnoreCase("cleannpcs")) {
            KingdomsCore.getNPCs().getNPCReg().deregisterAll();
        } else if (cmd.getName().equalsIgnoreCase("decayall")) {
            if (!ChangeTracker.getChanges().isEmpty()) {
                for (Map.Entry<Location, ChangeTracker.SaveBlock> e : ChangeTracker.getChanges().entrySet()) {
                    ChangeTracker.getForDecay().add(e.getKey());
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("save-kingdoms")) {
            DataLoadHelper.SaveKingdomData();
        }
        return true;
    }

    public static class PluginUpdateThread extends Thread {

        private final File coreFolder;

        public PluginUpdateThread(File build) {
            this.coreFolder = build;
        }

        @Override
        public void run() {
            while (true) {
                for (KingdomModual km : KingdomsCore.getRegisteredModuals()) {
                    File buildFile = new File(coreFolder.getAbsolutePath() + DBmanager.getFileSep() + km.getModualName()
                            + DBmanager.getFileSep() + "target" + DBmanager.getFileSep() + km.getModualName() + "-"
                            + km.getDescription().getVersion() + ".jar");
                    File buildTests = new File(coreFolder.getAbsolutePath() + DBmanager.getFileSep() + km.getModualName()
                            + DBmanager.getFileSep() + "target" + DBmanager.getFileSep() + "test-classes");
                    if (buildFile.exists() && buildTests.exists() && buildTests.isDirectory()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(DebugCommands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Bukkit.shutdown();
                        break;
                    }
                }
                File buildFile = new File(coreFolder.getAbsolutePath() + DBmanager.getFileSep() + "KingdomsUtilities"
                        + DBmanager.getFileSep() + "target" + DBmanager.getFileSep() + "KingdomsUtilities" + "-"
                        + KingdomsUtilities.getPlugin().getDescription().getVersion() + ".jar");
                File buildTests = new File(coreFolder.getAbsolutePath() + DBmanager.getFileSep() + "KingdomsUtilities"
                        + DBmanager.getFileSep() + "target" + DBmanager.getFileSep() + "test-classes");
                if (buildFile.exists() && buildTests.exists() && buildTests.isDirectory()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DebugCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Bukkit.shutdown();
                    break;
                }
            }
        }

    }
}
