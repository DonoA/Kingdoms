/*
 * Copyright 2017 donoa_000.
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
package io.dallen.kingdoms;

import java.util.LinkedList;
import java.util.Queue;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author donoa_000
 */
public class ThreadHandler {
    
    public static class BuilderThread implements Runnable {
        
        @Getter
        private static Queue<QueuedBlock> placeQueue = new LinkedList<>();
        
        @Override
        public void run() {
            if(placeQueue.isEmpty())
                return;
            for(int i = 0; i <= 3000; i++){
                QueuedBlock qb = placeQueue.poll();
                if(qb == null)
                    return;
                qb.loc.getBlock().setType(qb.newMat);
            }
        }
        
        public static void add(Location loc, Material mat){
            placeQueue.add(new QueuedBlock(loc, mat));
        }
        
        public static class QueuedBlock {
            @Getter
            private Location loc;
            
            @Getter
            private Material newMat;
            
            public QueuedBlock(Location loc, Material mat){
                this.loc = loc;
                this.newMat = mat;
            }
        }
    }
}
