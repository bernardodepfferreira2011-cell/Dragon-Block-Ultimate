package net.dragonultimate.models.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.dragonultimate.save.SaveAuraColor;
import net.dragonultimate.shader.AuraRenderTypes;
import net.dragonultimate.shader.AuraShaderManager;
import net.dragonultimate.shader.AuraState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class AuraRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final float ORIGIN_OFFSET_BLOCKS = 1.407f;
    private static final float BLOCKS_PER_LOCAL_UNIT = -0.937f;

    private static final int RADIAL_SEGMENTS = 14;
    private static final int VERTICAL_RINGS = 4;

    private static long lastDebug = 0L;
    private static int vertCount = 0;

    private static final RenderType AURA_RENDER_TYPE =
        AuraRenderTypes.aura(AuraRenderTypes.AuraBlendMode.TRANSLUCENT);

    public AuraRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                        AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                        float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        boolean active = AuraState.isActive();
        boolean shaderOk = AuraShaderManager.getShader() != null;
        float intensity = AuraState.getIntensity();

        if (!active && intensity <= 0.0f) return;
        if (!shaderOk) return;

        SaveAuraColor.AuraColorData cor = SaveAuraColor.getAuraColor(player);
        Matrix4f mat = poseStack.last().pose();

        vertCount = 0;

        float baseAuraYOuter = 0.05f;
        float baseAuraYInner = 0.12f;

        AuraShaderManager.setUniforms(1.0f, cor.outerArray(), cor.outerArray(), 0.35f, 0.45f, 3.0f,
                                       intensity, 0.4f);
        VertexConsumer outerConsumer = buffer.getBuffer(AURA_RENDER_TYPE);
        addAuraCone(outerConsumer, mat, 0.35f, 0.85f, 0.03f, 2.554f, baseAuraYOuter, 0.35f,
                    ageInTicks + partialTick);
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch(AURA_RENDER_TYPE);
        }

        AuraShaderManager.setUniforms(1.0f, cor.innerArray(), cor.innerArray(), 0.10f, 0.18f, 2.0f,
                                       intensity, 0.5f);
        VertexConsumer innerConsumer = buffer.getBuffer(AURA_RENDER_TYPE);
        addAuraCone(innerConsumer, mat, 0.25f, 0.62f, 0.03f, 2.261f, baseAuraYInner, 0.28f,
                    ageInTicks + partialTick + 0.6f);
    }

    private static float realHeightToLocalY(float realHeightAboveFeet) {
        return (realHeightAboveFeet - ORIGIN_OFFSET_BLOCKS) / BLOCKS_PER_LOCAL_UNIT;
    }

    private static float radiusAtT(float t, float radiusBottom, float radiusWaist, float radiusTop) {
        if (t < 0.4f) {
            return lerp(radiusBottom, radiusWaist, t / 0.4f);
        } else {
            return lerp(radiusWaist, radiusTop, (t - 0.4f) / 0.6f);
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private void addAuraCone(VertexConsumer consumer, Matrix4f mat,
                              float radiusBottom, float radiusWaist, float radiusTop,
                              float realHeightSpan, float realBaseHeight,
                              float jitterAmount, float time) {

        int segments = RADIAL_SEGMENTS;
        int rings = VERTICAL_RINGS;

        float[] xs = new float[(rings + 1) * segments];
        float[] ys = new float[(rings + 1) * segments];
        float[] zs = new float[(rings + 1) * segments];

        for (int r = 0; r <= rings; r++) {
            float t = (float) r / rings;
            float radiusHere = radiusAtT(t, radiusBottom, radiusWaist, radiusTop);
            float realHeightHere = realBaseHeight + realHeightSpan * t;

            boolean isApexRing = (r == rings);
            boolean jitterZone = !isApexRing && t >= 0.6f;

            for (int s = 0; s < segments; s++) {
                float angle = (float) (s * 2.0 * Math.PI / segments);

                float jitterR = 0f;
                float jitterY = 0f;
                if (jitterZone) {
                    float phase = (time * 1.65f + s * 0.17f + r * 0.05f) % 1.0f;
                    if (phase < 0f) phase += 1f;
                    float pulse = phase < 0.5f ? phase / 0.5f : (1f - phase) / 0.5f;
                    jitterR = jitterAmount * (pulse - 0.4f);
                    jitterY = jitterAmount * (pulse - 0.3f) * 0.6f;
                }

                float sway = isApexRing ? 0f
                    : (float) (Math.sin(time * 0.35f + s * 0.5f) * 0.03f);

                float radius = Math.max(0.01f, radiusHere + jitterR);
                float x = (float) (Math.cos(angle) * radius + sway);
                float z = (float) (Math.sin(angle) * radius - sway);
                float y = realHeightToLocalY(realHeightHere + jitterY);

                int idx = r * segments + s;
                xs[idx] = x;
                ys[idx] = y;
                zs[idx] = z;
            }
        }

        for (int r = 0; r < rings; r++) {
            float t0 = (float) r / rings;
            float t1 = (float) (r + 1) / rings;

            for (int s = 0; s < segments; s++) {
                int sNext = (s + 1) % segments;

                int i00 = r * segments + s;
                int i01 = r * segments + sNext;
                int i10 = (r + 1) * segments + s;
                int i11 = (r + 1) * segments + sNext;

                vert(consumer, mat, xs[i00], ys[i00], zs[i00], t0);
                vert(consumer, mat, xs[i01], ys[i01], zs[i01], t0);
                vert(consumer, mat, xs[i10], ys[i10], zs[i10], t1);

                vert(consumer, mat, xs[i10], ys[i10], zs[i10], t1);
                vert(consumer, mat, xs[i01], ys[i01], zs[i01], t0);
                vert(consumer, mat, xs[i11], ys[i11], zs[i11], t1);

                vertCount += 6;
            }
        }
    }

    private static void vert(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float heightFraction) {
        consumer.addVertex(mat, x, y, z).setColor(1f, 1f, 1f, heightFraction);
    }
}
