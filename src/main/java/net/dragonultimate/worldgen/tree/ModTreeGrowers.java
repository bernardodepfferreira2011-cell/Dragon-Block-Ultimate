package net.dragonultimate.worldgen.tree;

import net.dragonultimate.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower NAMEKWOOD = new TreeGrower(
            "namekwood",
            Optional.of(ModConfiguredFeatures.NAMEK_TREE.getKey()),
            Optional.empty(),
            Optional.empty()
    );
}
