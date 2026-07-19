package net.dragonultimate;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModConfiguredFeatures {

    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, DragonBlockUltimate.MOD_ID);

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registries.PLACED_FEATURE, DragonBlockUltimate.MOD_ID);

    public static final ResourceKey<ConfiguredFeature<?, ?>> NAMEK_TREE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "namek_tree"));

    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> NAMEK_TREE =
            CONFIGURED_FEATURES.register(NAMEK_TREE_KEY.location().getPath(), () -> new ConfiguredFeature<>(
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

    public static final DeferredHolder<PlacedFeature, PlacedFeature> NAMEK_TREE_PLACED =
            PLACED_FEATURES.register("namek_tree", () -> new PlacedFeature(
                    Holder.direct(NAMEK_TREE.get()),
                    List.<PlacementModifier>of(
                            RarityFilter.onAverageOnceEvery(6),
                            InSquarePlacement.spread(),
                            SurfaceWaterDepthFilter.forMaxDepth(0),
                            HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR),
                            BiomeFilter.biome()
                    )
            ));

    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
        PLACED_FEATURES.register(eventBus);
    }
}
