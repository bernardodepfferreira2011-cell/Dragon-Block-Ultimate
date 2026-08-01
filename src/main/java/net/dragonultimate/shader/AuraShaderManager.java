package net.dragonultimate.shader;

import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import javax.annotation.Nullable;
import java.io.IOException;

public class AuraShaderManager {

    @Nullable
    private static ShaderInstance auraShader;

    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        try {
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "aura"),
                    com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR
                ),
                shader -> {
                    auraShader = shader;
                    DragonBlockUltimate.LOGGER.info("[DragonBlockUltimate] Shader 'aura' registrado e compilado com sucesso.");
                }
            );
        } catch (Exception e) {
            DragonBlockUltimate.LOGGER.error("[DragonBlockUltimate] FALHA ao compilar o shader 'aura':", e);
            throw e;
        }
    }

    @Nullable
    public static ShaderInstance getShader() {
        return auraShader;
    }

    /** Assinatura legada (6 args) -- mantida por compatibilidade. */
    public static void setUniforms(float auravar, float[] colorInner, float[] colorOuter,
                                    float alp1, float alp2, float fresnelPower) {
        setUniforms(auravar, colorInner, colorOuter, alp1, alp2, fresnelPower,
                    AuraState.getIntensity(), 0.6f, 0.5f, 1.0f);
    }

    /** Assinatura intermediaria (8 args) -- mantida por compatibilidade. */
    public static void setUniforms(float auravar, float[] colorInner, float[] colorOuter,
                                    float alp1, float alp2, float fresnelPower,
                                    float intensity, float bloomStrength) {
        setUniforms(auravar, colorInner, colorOuter, alp1, alp2, fresnelPower,
                    intensity, bloomStrength, 0.5f, 1.0f);
    }

    /**
     * Assinatura completa (10 args). bodyDensity e tipBoostAmount sao
     * configuraveis por camada -- uma camada ja bem transparente
     * (alp1/alp2 baixos) precisa de bodyDensity mais perto de 1.0 pra nao
     * desaparecer quando multiplicado; uma camada mais opaca aguenta um
     * bodyDensity mais baixo sem sumir.
     */
    public static void setUniforms(float auravar, float[] colorInner, float[] colorOuter,
                                    float alp1, float alp2, float fresnelPower,
                                    float intensity, float bloomStrength,
                                    float bodyDensity, float tipBoostAmount) {
        if (auraShader == null) return;

        float time = (Minecraft.getInstance().level != null)
            ? Minecraft.getInstance().level.getGameTime() / 20.0f : 0f;

        float[] core = lighten(colorOuter, 0.55f);

        safeSet(auraShader, "time", time);
        safeSet3(auraShader, "color1", colorInner[0], colorInner[1], colorInner[2]);
        safeSet3(auraShader, "color2", colorOuter[0], colorOuter[1], colorOuter[2]);
        safeSet3(auraShader, "colorCore", core[0], core[1], core[2]);
        safeSet(auraShader, "alp1", alp1);
        safeSet(auraShader, "alp2", alp2);
        safeSet(auraShader, "power", fresnelPower);
        safeSet(auraShader, "divis", 1.0f);
        safeSet(auraShader, "intensity", intensity);
        safeSet(auraShader, "bloomStrength", bloomStrength);
        safeSet(auraShader, "bodyDensity", bodyDensity);
        safeSet(auraShader, "tipBoostAmount", tipBoostAmount);
    }

    private static float[] lighten(float[] rgb, float t) {
        return new float[] {
            rgb[0] + (1.0f - rgb[0]) * t,
            rgb[1] + (1.0f - rgb[1]) * t,
            rgb[2] + (1.0f - rgb[2]) * t
        };
    }

    private static void safeSet(ShaderInstance s, String name, float val) {
        var u = s.getUniform(name);
        if (u != null) u.set(val);
    }

    private static void safeSet3(ShaderInstance s, String name, float r, float g, float b) {
        var u = s.getUniform(name);
        if (u != null) u.set(r, g, b);
    }
}
