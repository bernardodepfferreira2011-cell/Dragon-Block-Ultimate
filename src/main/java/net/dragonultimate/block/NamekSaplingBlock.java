package net.dragonultimate.block;

import net.dragonultimate.ModBlocks;
import net.dragonultimate.ModConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class NamekSaplingBlock extends SaplingBlock {

    public NamekSaplingBlock() {
        super(ModConfiguredFeatures.NAMEK_TREE_GROWER, BlockBehaviour.Properties.of()
                .strength(0.2F, 0.2F)
                .instabreak());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.NAMEK_GRASS.get()) || state.is(ModBlocks.LIGHT_DIRTY.get());
    }
}
