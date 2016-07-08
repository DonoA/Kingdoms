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
package io.dallen.kingdoms.core.Handlers.SkinHandler;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class SkinTexture {
    @Getter @Setter
    private long timestamp;
    @Getter @Setter
    private String profileId;
    @Getter @Setter
    private String profileName;
    @Getter @Setter
    private boolean signatureRequired;
    @Getter @Setter
    private TextureUrlArray textures;
    
    @NoArgsConstructor
    public static class TextureUrlArray{
        @Getter @Setter
        private TextureUrl skin;
        @Getter @Setter
        private TextureUrl cape;
    }
    @NoArgsConstructor
    public static class TextureUrl{
        @Getter @Setter
        private String url;

    }
}
