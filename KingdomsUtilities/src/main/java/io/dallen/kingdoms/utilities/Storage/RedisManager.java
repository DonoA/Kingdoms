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
package io.dallen.kingdoms.utilities.Storage;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

/**
 *
 * @author donoa_000
 */
public class RedisManager {

    private RedisCommands<String, String> sync;

    public RedisManager() {
        RedisClient redisClient = RedisClient.create("redis://52.11.242.231:6379/0"); // test redis server b/c redis server not on windows
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisCommands<String, String> syncCommands = connection.sync();
        }
        redisClient.shutdown();
    }

//    public PlayerData loadPlayerDat(String username){
//        if(sync.get(username).equalsIgnoreCase("")){
//            return new PlayerData();
//        }else{
//            return null;
//        }
//    }
//    
//    public void savePlayerDat(String username, PlayerData pd){
//        sync.append(username, pd.toString());
//    }
    public void saveKingdoms() {

    }

    public void loadKingdoms() {

    }

    public void saveInstanceData() {

    }

    public void loadInstanceData() {

    }
}
