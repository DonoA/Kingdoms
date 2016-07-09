/*
 * Copyright 2016 Donovan Allen
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

import io.dallen.kingdoms.core.Structures.Structure;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author donoa_000
 */
public class GameTicks {

    public static void StartGameTicks() {
        new NPCTick().start();
        new MainTick().start();
        new DailyTick().start();
    }

    public static class NPCTick extends Thread {

        @Override
        public void run() {
            while (true) {
                //Check for players in range of building places

                //update building build amount
                //update NPC targets for tasks
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameTicks.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static class MainTick extends Thread {

        @Override
        public void run() {
            while (true) {
                for (Kingdom k : KingdomsCore.getKingdoms()) {
                    //recalculate population

                    //spawn attacking mobs
                    //add resources
                    for (Municipality m : k.getMunicipals()) {
                        //add resources

                        //recalculate chaos levels
                        for (Entry<Class, ArrayList<Structure>> buildings : m.getStructures().entrySet()) {
                            for (Structure building : buildings.getValue()) {
                                //move added resources around

                                //check overflows
                                //
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(10000);//poorly coded game tick :/
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameTicks.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static class DailyTick extends Thread {

        @Override
        public void run() {
            while (true) {
                //update kingdom taxes

                //think about mob siege
                //
                try {
                    Thread.sleep(1000 * 60 * 60 * 24);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameTicks.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
