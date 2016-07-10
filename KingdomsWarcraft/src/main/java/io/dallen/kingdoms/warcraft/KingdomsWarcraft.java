/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of KingdomsWarcraft for the Morphics Network.
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
package io.dallen.kingdoms.warcraft;

import io.dallen.kingdoms.core.KingdomModual;
import io.dallen.kingdoms.core.KingdomsCore;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

/**
 *
 * @author Donovan Allen
 */
public class KingdomsWarcraft extends KingdomModual {

    @Override
    public void onEnable() {
        Reflections reflections = new Reflections(getClass().getPackage().getName());
        setLstn(reflections.getSubTypesOf(Listener.class));
        KingdomsCore.getPlugin().registerModule(this);
    }

}
