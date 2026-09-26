package net.dragonultimate.shader;

import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;

/**
 * Executa o bloom seletivo usando a cadeia de pós-processamento nativa do Minecraft.
 * A configuração dos passes fica em assets/dragonblockultimate/shaders/post/bloom.json.
 */
public final class BloomPipeline {
    private static final ResourceLocation BLOOM_ID =
            ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "bloom");

    private static PostChain pipeline;
    private static int width;
    private static int height;
    private static boolean failed;

    private BloomPipeline() {}

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL || failed) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.isPaused() || minecraft.getMainRenderTarget() == null) {
            return;
        }

        int targetWidth = minecraft.getMainRenderTarget().width;
        int targetHeight = minecraft.getMainRenderTarget().height;
        if (targetWidth <= 0 || targetHeight <= 0) {
            return;
        }

        try {
            if (pipeline == null || width != targetWidth || height != targetHeight) {
                close();
                DragonBlockUltimate.LOGGER.info(
                        "[DragonBlockUltimate] Inicializando PostChain de bloom {}x{}.",
                        targetWidth,
                        targetHeight
                );
                pipeline = new PostChain(
                        minecraft.getTextureManager(),
                        minecraft.getResourceManager(),
                        minecraft.getMainRenderTarget(),
                        BLOOM_ID
                );
                width = targetWidth;
                height = targetHeight;
                pipeline.resize(targetWidth, targetHeight);
                DragonBlockUltimate.LOGGER.info(
                        "[DragonBlockUltimate] PostChain de bloom inicializado com sucesso."
                );
            }

            pipeline.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        } catch (IOException | RuntimeException exception) {
            failed = true;
            DragonBlockUltimate.LOGGER.error(
                    "[DragonBlockUltimate] Falha ao inicializar/executar o PostChain de bloom.",
                    exception
            );
            close();
        }
    }

    public static void resize(int targetWidth, int targetHeight) {
        if (pipeline != null && targetWidth > 0 && targetHeight > 0) {
            pipeline.resize(targetWidth, targetHeight);
            width = targetWidth;
            height = targetHeight;
        }
    }

    public static void reset() {
        failed = false;
        close();
    }

    public static void close() {
        if (pipeline != null) {
            pipeline.close();
            pipeline = null;
        }
        width = 0;
        height = 0;
    }
}
