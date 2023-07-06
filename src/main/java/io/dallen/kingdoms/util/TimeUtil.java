package io.dallen.kingdoms.util;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;

public class TimeUtil {

    @RequiredArgsConstructor
    public enum Time {
        MORNING(false), NOON(false),
        NIGHT(true), MIDNIGHT(true);

        private final boolean night;

        public boolean isNight() {
            return this.night;
        }
    }

    public static Time getTime(World world) {
        var worldTime = world.getTime();
        if (worldTime > 0 && worldTime <= 6_000) {
            return Time.MORNING;
        } else if (worldTime > 6_000 && worldTime <= 12_000) {
            return Time.NOON;
        } else if (worldTime > 12_000 && worldTime <= 18_000) {
            return Time.NIGHT;
        } else if (worldTime > 18_000 && worldTime <= 24_000) {
            return Time.MIDNIGHT;
        }

        return Time.MORNING;
    }

}
