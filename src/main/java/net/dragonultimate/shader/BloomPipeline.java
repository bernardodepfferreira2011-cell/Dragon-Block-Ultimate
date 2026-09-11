package net.dragonultimate.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Three-level HDR-style bloom pyramid.
 *
 * <p>The scene is copied before any post-processing, then bright pixels are
 * extracted and blurred at 1/2, 1/4 and 1/8 resolution. The three blurred
 * levels are combined with the original scene in a final fullscreen pass.</p>
 */
public final class BloomPipeline {
    private static final String NAMESPACE = DragonBlockUltimate.MOD_ID + ":";
    private static final String COPY = NAMESPACE + "light_copy";
    private static final String EXTRACT = NAMESPACE + "bloom";
    private static final String BLUR = NAMESPACE + "light_gaussian";
    private static final String COMPOSITE = NAMESPACE + "light_composite";

    private static Pipeline pipeline;
    private static boolean failed;

    private BloomPipeline() {}

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL || failed) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.isPaused()) return;

        try {
            int width = minecraft.getMainRenderTarget().width;
            int height = minecraft.getMainRenderTarget().height;
            if (width <= 0 || height <= 0) return;

            if (pipeline == null || pipeline.width != width || pipeline.height != height) {
                close();
                pipeline = new Pipeline(minecraft, width, height);
            }
            pipeline.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        } catch (Exception exception) {
            failed = true;
            DragonBlockUltimate.LOGGER.error(
                "[DragonBlockUltimate] Falha ao executar a piramide de bloom OpenGL.", exception
            );
            close();
        }
    }

    public static void resize(int width, int height) {
        if (pipeline != null && (pipeline.width != width || pipeline.height != height)) {
            close();
        }
    }

    public static void close() {
        if (pipeline != null) {
            pipeline.close();
            pipeline = null;
        }
    }

    private static final class Pipeline implements AutoCloseable {
        private final Minecraft minecraft;
        private final int width;
        private final int height;
        private final RenderTarget scene;
        private final Level level0;
        private final Level level1;
        private final Level level2;
        private final List<PostPass> passes = new ArrayList<>();

        private Pipeline(Minecraft minecraft, int width, int height) throws IOException {
            this.minecraft = minecraft;
            this.width = width;
            this.height = height;
            this.scene = target(width, height);
            this.level0 = new Level(width / 2, height / 2);
            this.level1 = new Level(width / 4, height / 4);
            this.level2 = new Level(width / 8, height / 8);

            add(COPY, minecraft.getMainRenderTarget(), scene, width, height);
            add(EXTRACT, scene, level0.color, level0.width, level0.height);
            blur(level0);
            add(EXTRACT, level0.color, level1.color, level1.width, level1.height);
            blur(level1);
            add(EXTRACT, level1.color, level2.color, level2.width, level2.height);
            blur(level2);

            PostPass composite = add(COMPOSITE, scene, minecraft.getMainRenderTarget(), width, height);
            composite.addAuxAsset("Bloom0", level0.color::getColorTextureId, level0.width, level0.height);
            composite.addAuxAsset("Bloom1", level1.color::getColorTextureId, level1.width, level1.height);
            composite.addAuxAsset("Bloom2", level2.color::getColorTextureId, level2.width, level2.height);
        }

        private void blur(Level level) throws IOException {
            PostPass horizontal = add(BLUR, level.color, level.swap, level.width, level.height);
            set(horizontal, "BlurDir", 1.0f, 0.0f);
            set(horizontal, "Radius", level.radius);

            PostPass vertical = add(BLUR, level.swap, level.color, level.width, level.height);
            set(vertical, "BlurDir", 0.0f, 1.0f);
            set(vertical, "Radius", level.radius);
        }

        private PostPass add(String shader, RenderTarget input, RenderTarget output,
                             int outputWidth, int outputHeight) throws IOException {
            PostPass pass = new PostPass(
                minecraft.getResourceManager(),
                shader,
                input,
                output,
                true
            );
            pass.setOrthoMatrix(new Matrix4f().ortho(0.0f, outputWidth, outputHeight, 0.0f, 0.1f, 1000.0f));
            passes.add(pass);
            return pass;
        }

        private void process(float partialTick) {
            for (PostPass pass : passes) {
                pass.process(partialTick);
            }
        }

        private static void set(PostPass pass, String name, float... values) {
            EffectInstance effect = pass.getEffect();
            if (effect.getUniform(name) != null) {
                effect.getUniform(name).set(values);
            }
        }

        private static RenderTarget target(int width, int height) {
            TextureTarget target = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            target.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            return target;
        }

        @Override
        public void close() {
            for (PostPass pass : passes) {
                pass.close();
            }
            scene.destroyBuffers();
            level0.close();
            level1.close();
            level2.close();
        }
    }

    private static final class Level implements AutoCloseable {
        private final int width;
        private final int height;
        private final float radius;
        private final RenderTarget color;
        private final RenderTarget swap;

        private Level(int width, int height) {
            this.width = Math.max(1, width);
            this.height = Math.max(1, height);
            this.radius = width <= 2 ? 3.0f : width <= 4 ? 4.0f : 5.0f;
            this.color = Pipeline.target(this.width, this.height);
            this.swap = Pipeline.target(this.width, this.height);
        }

        @Override
        public void close() {
            color.destroyBuffers();
            swap.destroyBuffers();
        }
    }
}
