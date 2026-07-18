package net.dragonultimate.block;

import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class NamekLeafBlock extends LeavesBlock {
    public NamekLeafBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.3F, 0.3F)
                .sound(SoundType.GRASS)
                .noOcclusion());
    }
}
