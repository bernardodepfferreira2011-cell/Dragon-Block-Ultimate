package net.dragonultimate.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class LightDirtyBlock extends Block {
    public LightDirtyBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.0F, 1.0F)
                .sound(SoundType.STONE));
    }
}
