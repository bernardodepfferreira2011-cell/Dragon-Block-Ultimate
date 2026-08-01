package net.dragonultimate.shader.kiattck;

import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.minecraft.resources.ResourceLocation;
import net.dragonultimate.DragonBlockUltimate;
import javax.annotation.Nullable;
import java.io.IOException;



public class KiBlastManager {

    public static final String SHADER_NAME = "kiblast";

    private static ShaderInstance kiBlastShader;

    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        try {
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, SHADER_NAME),
                    com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR
                ),
                shader -> {
                    kiBlastShader = shader;
                    DragonBlockUltimate.LOGGER.info("[DragonBlockUltimate] Shader 'kiblast' registrado");

                }
            );
        
        } catch (Exception e) {
            DragonBlockUltimate.LOGGER.info("[DragonBlockUltimate] falha aou registrar 'kiblast'");
            throw e;
        }
    }

    
}
