package net.dragonultimate.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class LightRenderTypes extends RenderType {
    private LightRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                             boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
        throw new IllegalStateException("Classe nao deve ser instanciada");
    }

    private static final RenderStateShard.ShaderStateShard LIGHT_SHADER =
            new RenderStateShard.ShaderStateShard(LightShaderManager::getLightShader);

    private static final RenderStateShard.ShaderStateShard AURA_SHADER =
            new RenderStateShard.ShaderStateShard(LightShaderManager::getAuraShader);

    private static final RenderType AURA = create(
            "dbu_aura",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            1024,
            false,
            true,
            CompositeState.builder()
                    .setShaderState(AURA_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY)
                    .createCompositeState(false)
    );

    private static final RenderType AURA_ADDITIVE = create(
            "dbu_aura_additive",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            1024,
            false,
            true,
            CompositeState.builder()
                    .setShaderState(AURA_SHADER)
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY)
                    .createCompositeState(false)
    );

    private static final RenderType LIGHTNING = create(
            "dbu_lightning",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            512,
            false,
            false,
            CompositeState.builder()
                    .setShaderState(LIGHT_SHADER)
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY)
                    .createCompositeState(false)
    );

    public static RenderType aura() {
        return AURA;
    }

    public static RenderType auraAdditive() {
        return AURA_ADDITIVE;
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }
}
