package io.dallen.kingdoms.util;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Locale;
import java.util.function.BiConsumer;

@Builder
@Data
public class Bounds implements Cloneable {

    private World world;

    private int blockX;
    private int blockY;
    private int blockZ;

    private int plusX;
    private int minusX;
    private int plusZ;
    private int minusZ;

    private int height;
    private int depth;

    public void forEachBorder(BiConsumer<Integer, Integer> f) {
        forEachBorder((x, z, i) -> f.accept(x, z));
    }

    public Location center() {
        return new Location(world, blockX, blockY, blockZ);
    }

    public void forEachBorder(ForEachIndex2D f) {
        int i = 0;
        for (int x = -getMinusX(); x <= getPlusX(); x++) {
            f.accept(blockX + x, blockZ - getMinusZ(), i++);
            f.accept(blockX + x, blockZ + getPlusZ(), i++);
        }

        for (int z = -getMinusZ() + 1; z < getPlusZ(); z++) {
            f.accept(blockX - getMinusX(), blockZ + z, i++);
            f.accept(blockX + getPlusX(), blockZ + z, i++);
        }
    }

    public void forEachBase(ForEachIndex2D f) {
        int i = 0;
        for (int x = -getMinusX(); x <= getPlusX(); x++) {
            for (int z = -getMinusZ(); z <= getPlusZ(); z++) {
                f.accept(blockX + x, blockZ + z, i++);
            }
        }
    }

    public void forEach(ForEachIndex3D f) {
        int i = 0;
        for (int x = -getMinusX(); x <= getPlusX(); x++) {
            for (int y = getDepth(); y < getHeight(); y++) {
                for (int z = -getMinusZ(); z <= getPlusZ(); z++) {
                    f.accept(blockX + x, blockY + y, blockZ + z, i++);
                }
            }
        }
    }

    public Bounds add(int plusX, int minusX, int plusZ, int minusZ) {
        this.plusX += plusX;
        this.minusX += minusX;
        this.plusZ += plusZ;
        this.minusZ += minusZ;
        return this;
    }

    public boolean contains(int x, int z) {
        return (x > blockX - minusX && x < blockX + plusX) &&
            (z > blockZ - minusZ && z < blockZ + plusZ);
    }

    public boolean contains(int x, int y, int z) {
        return (x >= blockX - minusX && x <= blockX + plusX) &&
                (z >= blockZ - minusZ && z <= blockZ + plusZ) &&
                (y >= blockY - depth && y < blockY + height);
    }

    @Override
    public Bounds clone() {
        try {
            Bounds clone = (Bounds) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public int getSizeX() {
        return minusX + plusX + 1;
    }

    public int getSizeZ() {
        return minusZ + plusZ + 1;
    }
    public int getTotalHeight() {
        return depth + height;
    }

    public interface ForEachIndex2D {
        void accept(int x, int z, int i);
    }

    public interface ForEachIndex3D {
        void accept(int x, int y, int z, int i);
    }

}
