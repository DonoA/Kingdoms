package io.dallen.kingdoms.util;

import java.awt.Point;
import java.awt.Polygon;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static Point asPoint(Location l) {
        return new Point(l.getBlockX(), l.getBlockZ());
    }

    public static Location asLocation(Point p, World w, int Ycord) {
        return new Location(w, p.getX(), Ycord, p.getY());
    }

    public static Point calcCenter(Point[] corners) {
        long X = 0;
        long Y = 0;
        for (int i = 0; i < corners.length; i++) {
            X += corners[i].getX();
            Y += corners[i].getY();
        }
        X /= corners.length;
        Y /= corners.length;
        return new Point((int) X, (int) Y);
    }
}
