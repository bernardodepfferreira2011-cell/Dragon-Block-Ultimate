package net.dragonultimate;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModConfiguredFeatures {

    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, DragonBlockUltimate.MOD_ID);

    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> NAMEK_TREE =
            CONFIGURED_FEATURES.register("namek_tree", () -> new ConfiguredFeature<>(
                    Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                            BlockStateProvider.simple(ModBlocks.NAMEK_LOG.get().defaultBlockState()),
                            new StraightTrunkPlacer(5, 2, 0),
                            BlockStateProvider.simple(ModBlocks.NAMEK_LEAVE.get().defaultBlockState()),
                            new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                            new TwoLayersFeatureSize(3, 0, 1)
                    )
                    .dirt(BlockStateProvider.simple(ModBlocks.NAMEK_GRASS.get().defaultBlockState()))
                    .build()
            ));

    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
    }
}
