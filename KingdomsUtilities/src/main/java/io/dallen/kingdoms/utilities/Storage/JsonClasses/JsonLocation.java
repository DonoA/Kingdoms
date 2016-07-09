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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonLocation implements SaveType.NativeType {

    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;
    @Getter
    @Setter
    private double z;
    @Getter
    @Setter
    private float yaw;
    @Getter
    @Setter
    private float pitch;
    @Getter
    @Setter
    private String world;

    public JsonLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.world = loc.getWorld().getName();
    }

    /**
     * Get a bukkit copy of this object
     *
     * @return A bukkit Location object of this object
     */
    @Override
    public Location toJavaObject() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
