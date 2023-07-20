package io.dallen.kingdoms.savedata;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

public class ExposeExclusionStrategy {
    public static class Serialize implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes obj) {
            var annotation = obj.getAnnotation(Expose.class);
            if(annotation != null) {
                return !annotation.serialize();
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldSkipClass(Class<?> obj) {
            var annotation = obj.getAnnotation(Expose.class);
            if(annotation != null) {
                return !annotation.serialize();
            } else {
                return false;
            }
        }
    }

    public static class Deserialize implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes obj) {
            var annotation = obj.getAnnotation(Expose.class);
            if(annotation != null) {
                return !annotation.deserialize();
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldSkipClass(Class<?> obj) {
            var annotation = obj.getAnnotation(Expose.class);
            if(annotation != null) {
                return !annotation.deserialize();
            } else {
                return false;
            }
        }
    }
}
