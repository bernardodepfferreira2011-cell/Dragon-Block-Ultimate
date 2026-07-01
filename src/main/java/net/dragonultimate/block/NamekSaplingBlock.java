package net.dragonultimate.block;

import net.dragonultimate.ModBlocks;
import net.dragonultimate.worldgen.tree.ModTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class NamekSaplingBlock extends SaplingBlock {
    public NamekSaplingBlock() {
        super(ModTreeGrowers.NAMEKWOOD, BlockBehaviour.Properties.of()
                .strength(0.2F, 0.2F)
                .instabreak()
                .noOcclusion());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.NAMEK_GRASS.get()) || state.is(ModBlocks.LIGHT_DIRTY.get());
    }
}
