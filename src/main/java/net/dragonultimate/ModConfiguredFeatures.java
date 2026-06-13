package net.dragonultimate;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.TreeGrower;
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

import java.util.Optional;

public class ModConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, DragonBlockUltimate.MOD_ID);

    public static final ResourceKey<ConfiguredFeature<?, ?>> NAMEK_TREE_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            new ResourceLocation(DragonBlockUltimate.MOD_ID, "namek_tree")
    );

    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> NAMEK_TREE =
            CONFIGURED_FEATURES.register("namek_tree", () -> new ConfiguredFeature<>(Feature.TREE, createTreeConfiguration()));

    public static final TreeGrower NAMEK_TREE_GROWER = new TreeGrower(
            DragonBlockUltimate.MOD_ID + ":namek",
            Optional.empty(),
            Optional.of(NAMEK_TREE_KEY),
            Optional.empty()
    );

    private static TreeConfiguration createTreeConfiguration() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.NAMEK_LOG.get().defaultBlockState()),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(ModBlocks.NAMEK_LEAVE.get().defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(3, 0, 1)
        )
                .dirt(BlockStateProvider.simple(ModBlocks.NAMEK_GRASS.get().defaultBlockState()))
                .build();
    }

    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
    }
}
