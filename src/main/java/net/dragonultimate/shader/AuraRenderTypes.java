package net.dragonultimate.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Function;

/**
 * RenderTypes customizados pra aura, usando o shader gerenciado pela AuraShaderManager.
 *
 * IMPORTANTE: usa NO_DEPTH_TEST em vez do depth-test padrão. Com depth-test normal,
 * a aura compete no z-buffer contra o próprio corpo do jogador (mesmo teste de
 * profundidade) - isso causa exatamente o sintoma de "só aparece de certos ângulos,
 * some de perto/de outros ângulos". NO_DEPTH_TEST faz a aura sempre desenhar por
 * cima, como um efeito de glow, sem competir com a geometria do corpo.
 */
public class AuraRenderTypes extends RenderType {

    private AuraRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2) {
        super(s, v, m, i, b, b2, r, r2);
        throw new IllegalStateException("Classe não deve ser instanciada");
    }

    private static final RenderStateShard.ShaderStateShard AURA_SHADER_STATE =
        new RenderStateShard.ShaderStateShard(AuraShaderManager::getShader);

    public enum AuraBlendMode {
        ADDITIVE,
        TRANSLUCENT
    }

    private static final Function<AuraBlendMode, RenderType> AURA_TYPES =
        Util.memoize(AuraRenderTypes::buildAuraType);

    public static RenderType aura(AuraBlendMode mode) {
        return AURA_TYPES.apply(mode);
    }

    private static RenderType buildAuraType(AuraBlendMode mode) {
        RenderStateShard.TransparencyStateShard transparency =
            mode == AuraBlendMode.ADDITIVE
                ? RenderType.ADDITIVE_TRANSPARENCY
                : RenderType.TRANSLUCENT_TRANSPARENCY;

        RenderType.CompositeState state = RenderType.CompositeState.builder()
            .setShaderState(AURA_SHADER_STATE)
            .setTransparencyState(transparency)
            .setCullState(RenderType.NO_CULL)
            .setDepthTestState(RenderType.NO_DEPTH_TEST)
            .setWriteMaskState(RenderType.COLOR_WRITE)
            .setLightmapState(RenderType.NO_LIGHTMAP)
            .setOverlayState(RenderType.NO_OVERLAY)
            .createCompositeState(false);

        String name = "dbi_aura_" + mode.name().toLowerCase();
        return RenderType.create(
            name,
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            256,
            false,
            false,
            state
        );
    }
}
