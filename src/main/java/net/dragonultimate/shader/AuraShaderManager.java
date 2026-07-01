package net.dragonultimate.shader;

import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import javax.annotation.Nullable;
import java.io.IOException;

public class AuraShaderManager {

    @Nullable
    private static ShaderInstance auraShader;

    public static final float[][] TRANSFORM_COLORS = {
        {1.0f, 1.0f, 0.6f,  0.9f, 0.7f, 0.1f},  // Base
        {1.0f, 1.0f, 0.3f,  1.0f, 0.9f, 0.0f},  // SSJ
        {1.0f, 1.0f, 0.1f,  1.0f, 0.8f, 0.0f},  // SSJ2
        {1.0f, 0.95f,0.0f,  1.0f, 0.7f, 0.0f},  // SSJ3
        {0.8f, 0.3f, 0.0f,  0.6f, 0.1f, 0.0f},  // SSJ4
        {0.2f, 0.5f, 1.0f,  0.0f, 0.3f, 0.9f},  // SSJBlue
    };

    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "aura"),
                com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_NORMAL
            ),
            shader -> auraShader = shader
        );
    }

    @Nullable
    public static ShaderInstance getShader() {
        return auraShader;
    }

    public static void updateUniforms(
            Player player,
            float power,
            float kiCharge,
            float attackState,
            int transIndex,
            float auravar,
            int layerIndex,
            float[] customInner,
            float[] customOuter
    ) {
        if (auraShader == null) return;

        float time = (Minecraft.getInstance().level != null)
            ? Minecraft.getInstance().level.getGameTime() / 20.0f : 0f;

        int t = Math.max(0, Math.min(transIndex, TRANSFORM_COLORS.length - 1));
        float[] tColor = TRANSFORM_COLORS[t];

        // Se tiver cor customizada, usa ela
        float[] inner = (customInner != null) ? customInner : new float[]{tColor[0], tColor[1], tColor[2]};
        float[] outer = (customOuter != null) ? customOuter : new float[]{tColor[3], tColor[4], tColor[5]};

        float alp1 = layerIndex == 0 ? 0.95f : 0.55f;
        float alp2 = layerIndex == 0 ? 0.30f : 0.15f;

        safeSet(auraShader, "time", time);
        safeSet(auraShader, "power", power);
        safeSet(auraShader, "kiCharge", kiCharge);
        safeSet(auraShader, "attackState", attackState);
        safeSet(auraShader, "transformation", (float) transIndex);
        safeSet(auraShader, "auravar", auravar);
        safeSet(auraShader, "layer", (float) layerIndex);
        safeSet(auraShader, "divis", 1.0f);

        safeSet3(auraShader, "colorInner",      inner[0], inner[1], inner[2]);
        safeSet3(auraShader, "colorOuter",      outer[0], outer[1], outer[2]);
        safeSet3(auraShader, "colorCore",       1.0f, 1.0f, 1.0f);
        safeSet3(auraShader, "transformColor1", inner[0], inner[1], inner[2]);
        safeSet3(auraShader, "transformColor2", outer[0], outer[1], outer[2]);

        safeSet(auraShader, "alp1", alp1);
        safeSet(auraShader, "alp2", alp2);

        auraShader.apply();
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
