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

    public static void setUniforms(float auravar, float[] colorInner, float[] colorOuter,
                                    float alp1, float alp2, float fresnelPower) {
        if (auraShader == null) return;

        float time = (Minecraft.getInstance().level != null)
            ? Minecraft.getInstance().level.getGameTime() / 20.0f : 0f;

        safeSet(auraShader, "time", time);
        safeSet(auraShader, "auravar", auravar);
        safeSet3(auraShader, "color1", colorInner[0], colorInner[1], colorInner[2]);
        safeSet3(auraShader, "color2", colorOuter[0], colorOuter[1], colorOuter[2]);
        safeSet(auraShader, "alp1", alp1);
        safeSet(auraShader, "alp2", alp2);
        safeSet(auraShader, "power", fresnelPower);
        safeSet(auraShader, "divis", 1.0f);
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
