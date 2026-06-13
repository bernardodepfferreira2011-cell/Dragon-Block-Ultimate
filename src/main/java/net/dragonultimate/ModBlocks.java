package net.dragonultimate;

import net.dragonultimate.block.LightDirtyBlock;
import net.dragonultimate.block.NamekGrassBlock;
import net.dragonultimate.block.NamekLeafBlock;
import net.dragonultimate.block.NamekLogBlock;
import net.dragonultimate.block.NamekSaplingBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.minecraft.core.registries.Registries.BLOCK;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BLOCK, DragonBlockUltimate.MOD_ID);

    public static final DeferredHolder<Block, Block> NAMEK_GRASS = BLOCKS.register("namek_grass", NamekGrassBlock::new);
    public static final DeferredHolder<Block, Block> NAMEK_LEAVE = BLOCKS.register("namek_leave", NamekLeafBlock::new);
    public static final DeferredHolder<Block, Block> NAMEK_LOG = BLOCKS.register("namek_log", NamekLogBlock::new);
    public static final DeferredHolder<Block, Block> NAMEK_SAPLING = BLOCKS.register("namek_sapling", NamekSaplingBlock::new);
    public static final DeferredHolder<Block, Block> LIGHT_DIRTY = BLOCKS.register("light_dirty", LightDirtyBlock::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
