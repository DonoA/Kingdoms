package io.dallen.kingdoms.util;

import lombok.Builder;
import lombok.Data;

import java.util.function.BiConsumer;

@Builder
@Data
public class Bounds implements Cloneable {

    private int blockX;
    private int blockY;
    private int blockZ;

    private int plusX;
    private int minusX;
    private int plusZ;
    private int minusZ;

    private int height;

    public void forEachBorder(BiConsumer<Integer, Integer> f) {
        forEachBorder((x, z, i) -> f.accept(x, z));
    }

    public void forEachBorder(ForEachIndex f) {
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

    public void forEach(ForEachIndex f) {
        int i = 0;
        for (int x = -getMinusX(); x <= getPlusX(); x++) {
            for (int z = -getMinusZ(); z <= getPlusZ(); z++) {
                f.accept(blockX + x, blockZ + z, i++);
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

    public interface ForEachIndex {
        void accept(int x, int z, int i);
    }

}