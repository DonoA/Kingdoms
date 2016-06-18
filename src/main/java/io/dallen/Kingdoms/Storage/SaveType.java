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
package io.dallen.Kingdoms.Storage;

/**
 *
 * @author Donovan Allen
 */
public interface SaveType {
    
    public static interface NativeType extends SaveType{
        public <T> T toJavaObject();
            
        public static interface JsonType extends NativeType{

            public <T extends Saveable> T toJavaObject();
        }
    }
    
    public static interface Saveable extends SaveType{
        public <T extends NativeType> T toJsonObject();
    }
}
