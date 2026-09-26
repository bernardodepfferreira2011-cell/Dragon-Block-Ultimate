package net.dragonultimate.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import javax.annotation.Nullable;
import java.io.IOException;

public final class LightShaderManager {
    @Nullable
    private static ShaderInstance lightShader;

    @Nullable
    private static ShaderInstance auraShader;

    private LightShaderManager() {}

    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "light"),
                        DefaultVertexFormat.POSITION_COLOR
                ),
                registered -> lightShader = registered
        );

        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "aura"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                registered -> auraShader = registered
        );
    }

    @Nullable
    public static ShaderInstance getLightShader() {
        return lightShader;
    }

    @Nullable
    public static ShaderInstance getAuraShader() {
        return auraShader;
    }

    public static void setAuraUniforms(float time, float intensity, float[] innerColor, float[] outerColor,
                                       float velX, float velY, float velZ, float seed, float charge) {
        if (auraShader != null) {
            if (auraShader.getUniform("Time") != null) {
                auraShader.getUniform("Time").set(time);
            }
            if (auraShader.getUniform("Intensity") != null) {
                auraShader.getUniform("Intensity").set(intensity);
            }
            if (auraShader.getUniform("InnerColor") != null) {
                auraShader.getUniform("InnerColor").set(innerColor[0], innerColor[1], innerColor[2], innerColor[3]);
            }
            if (auraShader.getUniform("OuterColor") != null) {
                auraShader.getUniform("OuterColor").set(outerColor[0], outerColor[1], outerColor[2], outerColor[3]);
            }
            if (auraShader.getUniform("PlayerVelocity") != null) {
                auraShader.getUniform("PlayerVelocity").set(velX, velY, velZ);
            }
            if (auraShader.getUniform("Seed") != null) {
                auraShader.getUniform("Seed").set(seed);
            }
            if (auraShader.getUniform("Charge") != null) {
                auraShader.getUniform("Charge").set(charge);
            }
        }
    }
}
