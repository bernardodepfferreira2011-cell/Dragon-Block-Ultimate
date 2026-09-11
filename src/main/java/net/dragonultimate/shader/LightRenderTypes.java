package net.dragonultimate.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class LightRenderTypes extends RenderType {
    private LightRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                             boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
        throw new IllegalStateException("Classe não deve ser instanciada");
    }

    private static final RenderStateShard.ShaderStateShard LIGHT_SHADER =
        new RenderStateShard.ShaderStateShard(LightShaderManager::getShader);

    private static final RenderType LIGHTNING = create(
        "dbu_lightning",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.QUADS,
        256,
        false,
        false,
        CompositeState.builder()
            .setShaderState(LIGHT_SHADER)
            .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setLightmapState(RenderStateShard.NO_LIGHTMAP)
            .setOverlayState(RenderStateShard.NO_OVERLAY)
            .createCompositeState(false)
    );

    private LightRenderTypes() {
        super(null, null, null, 0, false, false, null, null);
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }
}
