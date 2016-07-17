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
package io.dallen.kingdoms.core.Handlers.MultiBlocks;

import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author donoa_000
 */
@NoArgsConstructor
public class MultiBlock {

    private String type;
    private Location center;
    private int len; // Z
    private int wid; // X
    private int height; // Y

    @Getter
    private static ArrayList<MultiBlock> MultiBlocks = new ArrayList<MultiBlock>();

    @Getter
    private final static Class[] MultiBlockClasses = new Class[]{Forge.class};

    public MultiBlock(Location cent, int l, int w, int h) {
        this.center = cent;
        this.len = l;
        this.wid = w;
        this.height = h;
    }

    public static void loadForm() {
        throw new UnsupportedOperationException();
    }

    public static MultiBlock getMultiBlock(Location bLoc) {
        for (MultiBlock mb : MultiBlocks) {
            if (bLoc.getBlockX() >= mb.center.getBlockX() - mb.len / 2
                    && bLoc.getBlockX() <= mb.center.getBlockX() + mb.len / 2
                    && bLoc.getBlockY() >= mb.center.getBlockY()
                    && bLoc.getBlockY() <= mb.center.getBlockY() + mb.height
                    && bLoc.getBlockZ() >= mb.center.getBlockZ() - mb.wid / 2
                    && bLoc.getBlockZ() <= mb.center.getBlockZ() + mb.wid / 2) {
                return mb;
            }
        }
        return null;
    }

    public void onInteract(PlayerInteractEvent e) {
    }

    public void destroy() {
        for (int X = center.getBlockX() - wid / 2; X < center.getBlockX() + wid / 2; X++) {
            for (int Z = center.getBlockZ() - len / 2; Z < center.getBlockZ() + len / 2; Z++) {
                for (int Y = center.getBlockY() - height / 2; Y < center.getBlockY() + height / 2; Y++) {
                    MultiBlocks.remove(this);
                }
            }
        }
    }

}
