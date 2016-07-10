/*
 * Copyright 2016 Morphics Network.
 * 
 * This file is part of KingdomsCore for the Morphics Network.
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

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Donovan Allen
 */
public class KingdomModual extends JavaPlugin {

    @Getter
    private Class<? extends PlayerData> playerData;

    @Getter
    @Setter
    private Set<Class<? extends Listener>> lstn;

    @Getter
    private String classPath;

    @Getter
    private String storagePath;

    @Getter
    private String structurePath;

    @Getter
    @Setter
    private String modualName;

    @Getter
    private boolean hasStructures;
}
