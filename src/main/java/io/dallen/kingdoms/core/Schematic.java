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
package io.dallen.kingdoms.core;

import io.dallen.utils.Storage.Blueprint;
import lombok.Getter;

/**
 *
 * @author donoa_000
 */
public class Schematic extends Blueprint{
    
    @Getter
    private String name;
    
    @Getter
    private String type;
    
    public Schematic(int l, int w, int h, short[] b, byte[] d, String name, String type){
        super(l, w, h, b, d);
        this.name = name;
        this.type = type;
    }
    
    public Schematic(Blueprint print, String name, String type){
        super(print.getLen(), print.getWid(), print.getHigh(), print.getBlocks());
        this.name = name;
        this.type = type;
    }
}
