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
package io.dallen.kingdoms.core.Structures;

import java.util.ArrayList;
import lombok.Getter;

/**
 *
 * @author Donovan Allen
 */
public class MailReceiver {
    @Getter
    private String address;
    
    @Getter
    private ArrayList<Letter> currentLetters = new ArrayList<Letter>();
    
    public static class Letter{
        @Getter
        private String message;
        
        @Getter
        private String to;
        
        @Getter
        private String from;
        
        @Getter
        private AttachmentType attachmentType;
        
        @Getter
        private Object attachment;
        
        public Letter(String from, String to, String message){
            this.from = from;
            this.to = to;
            this.message = message;
        }
        
        public void setAttachment(AttachmentType type, Object attachment){
            
        }
        
        public static enum AttachmentType{
            ITEMSTACK, GOLD
        }
    }
}
