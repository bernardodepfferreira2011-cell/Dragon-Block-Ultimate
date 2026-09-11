package net.dragonultimate.shader;

import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import javax.annotation.Nullable;
import java.io.IOException;

public final class LightShaderManager {
    @Nullable
    private static ShaderInstance shader;

    private LightShaderManager() {}

    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "light"),
                com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR
            ),
            registered -> shader = registered
        );
    }

    @Nullable
    public static ShaderInstance getShader() {
        return shader;
    }
}
