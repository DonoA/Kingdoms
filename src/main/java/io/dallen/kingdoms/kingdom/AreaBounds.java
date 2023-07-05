package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.util.Bounds;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

@RequiredArgsConstructor
public class AreaBounds {
    @Getter
    private final Bounds bounds;
    private final boolean onGround;

    public void placeBounds(Material boundMaterial) {

        bounds.forEachBorder((x, z) -> {
            var y = bounds.getBlockY() - 1;
            if (onGround) {
                y = groundHeight(bounds.getWorld(), x, z);
            }
            var block = new Location(bounds.getWorld(), x, y, z)
                    .getBlock();

            block.setType(boundMaterial);
        });
    }

    public void eraseBounds(Material replaceType) {
        bounds.forEachBorder((x, z, i) -> {
            var y = bounds.getBlockY() - 1;
            if (onGround) {
                y = groundHeight(bounds.getWorld(), x, z);
            }

            new Location(bounds.getWorld(), x, y, z)
                    .getBlock()
                    .setType(replaceType);
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
