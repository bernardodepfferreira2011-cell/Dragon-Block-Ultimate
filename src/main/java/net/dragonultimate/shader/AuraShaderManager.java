package net.dragonultimate.shader;

import net.dragonultimate.DragonBlockUltimate;
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

    /** Estilo do fresnel (brilho de borda). Seguro chamar 1x por frame agora - não é mais por peça. */
    public static void setStyle(float power, float divis) {
        if (auraShader == null) return;
        safeSet(auraShader, "power", power);
        safeSet(auraShader, "divis", divis);
    }

    private static void safeSet(ShaderInstance s, String name, float val) {
        var u = s.getUniform(name);
        if (u != null) u.set(val);
    }
}
