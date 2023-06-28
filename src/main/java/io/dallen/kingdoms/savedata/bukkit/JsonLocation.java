package io.dallen.kingdoms.savedata.bukkit;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@AllArgsConstructor
public class JsonLocation {
    private String world;
    private double x;
    private double y;
    private double z;

    public JsonLocation(Location location) {
        world = location.getWorld().getName();
        x = location.getX();
        y = location.getY();
        z = location.getZ();
    }

    public Location toBukkit() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}
