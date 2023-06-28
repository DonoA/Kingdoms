package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.util.Bounds;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AreaBounds {
    @Getter
    private final Bounds bounds;
    private final boolean onGround;
    private final List<Material> previousBounds = new ArrayList<>();

    public void placeBounds(World world, Material boundMaterial) {
        previousBounds.clear();

        bounds.forEachBorder((x, z) -> {
            var y = bounds.getBlockY() - 1;
            if (onGround) {
                y = groundHeight(world, x, z);
            }
            var block = new Location(world, x, y, z)
                    .getBlock();

            previousBounds.add(block.getType());
            block.setType(boundMaterial);
        });
    }

    public void eraseBounds(World world) {
        bounds.forEachBorder((x, z, i) -> {
            if (i >= previousBounds.size()) {
                return;
            }

            var y = bounds.getBlockY() - 1;
            if (onGround) {
                y = groundHeight(world, x, z);
            }

            new Location(world, x, y, z)
                    .getBlock()
                    .setType(previousBounds.get(i));
        });
    }

    public int groundHeight(World world, int x, int z) {
        for (int y = bounds.getBlockY() - 1; y > world.getMinHeight(); y--) {
            var block = new Location(world, x, y, z).getBlock();
            if (block.getType().isSolid()) {
                return y;
            }
        }
        return world.getMinHeight() + 1;
    }
}
