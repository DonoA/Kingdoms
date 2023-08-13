package io.dallen.kingdoms.util;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Set;

public class ToolTypes {

    @Getter
    private static final Set<Material> hoes = Set.of(
            Material.WOODEN_HOE, Material.STONE_HOE, Material.GOLDEN_HOE,
            Material.DIAMOND_HOE, Material.NETHERITE_HOE
    );

    @Getter
    private static final Set<Material> pickaxes = Set.of(
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE
    );

}
