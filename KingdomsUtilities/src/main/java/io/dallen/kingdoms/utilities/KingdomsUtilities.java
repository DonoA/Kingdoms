/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of KingdomsUtilities for the Morphics Network.
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
package io.dallen.kingdoms.utilities;

import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.lang.reflect.Method;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

/**
 *
 * @author Donovan Allen
 */
public class KingdomsUtilities extends JavaPlugin {

    @Getter
    private static KingdomsUtilities Plugin;

    @Override
    public void onEnable() {
        Plugin = this;
        Reflections reflections = new Reflections(getClass().getPackage().getName());
        LogUtil.printDebug("Loading Utils");
        Set<Class<? extends Listener>> lstn = reflections.getSubTypesOf(Listener.class);
        for (Class<? extends Listener> l : lstn) {
            LogUtil.printDebug("Registered Listener " + l.getSimpleName());
            boolean hasEventHandler = false;
            for (Method m : l.getDeclaredMethods()) {
                if (m.isAnnotationPresent(EventHandler.class)) {
                    hasEventHandler = true;
                }
            }
            if (hasEventHandler) {
                try {
                    l.getConstructor();
                    Bukkit.getPluginManager().registerEvents((Listener) l.newInstance(), this);
                    LogUtil.printDebug("Registered Listener " + l.getSimpleName());
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException ex) {
                }
            }
        }
    }

    @Override
    public void onDisable() {
    }
}
