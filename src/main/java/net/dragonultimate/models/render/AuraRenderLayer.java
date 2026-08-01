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

        // Volume voltando pro nível que já funcionava (radiusWaist/crownRadius
        // iguais ao original) -- só a base (radiusBottom) fica um pouco mais
        // fina, sem encolher o cone inteiro como na tentativa anterior.
        float baseAuraYOuter = -0.05f;
        float baseAuraYInner = -0.02f;

        long seed = player.getUUID().getLeastSignificantBits();

        AuraShaderManager.setUniforms(1.0f, cor.outerArray(), cor.outerArray(), 0.35f, 0.45f, 3.0f,
                                       intensity, 0.4f, 0.4f, 1.0f);
        VertexConsumer outerConsumer = buffer.getBuffer(AURA_RENDER_TYPE);
        addAuraCone(outerConsumer, mat, 0.24f, 0.85f, 0.22f, 2.554f, baseAuraYOuter,
                    12, 0.55f, 0.20f, 0f, ageInTicks + partialTick, seed);
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch(AURA_RENDER_TYPE);
        }

        AuraShaderManager.setUniforms(1.0f, cor.innerArray(), cor.innerArray(), 0.10f, 0.18f, 2.0f,
                                       intensity, 0.5f, 0.85f, 1.0f);
        VertexConsumer innerConsumer = buffer.getBuffer(AURA_RENDER_TYPE);
        addAuraCone(innerConsumer, mat, 0.18f, 0.62f, 0.15f, 2.261f, baseAuraYInner,
                    10, 0.40f, 0.15f, (float) (Math.PI / 6.0), ageInTicks + partialTick + 0.6f, seed + 991);
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

    private static float hash01(long seed, int salt) {
        long x = seed + salt * 0x9E3779B97F4A7C15L;
        x = (x ^ (x >>> 30)) * 0xBF58476D1CE4E5B9L;
        x = (x ^ (x >>> 27)) * 0x94D049BB133111EBL;
        x = x ^ (x >>> 31);
        return ((x >>> 40) & 0xFFFFFF) / (float) 0xFFFFFF;
    }

    private void addAuraCone(VertexConsumer consumer, Matrix4f mat,
                              float radiusBottom, float radiusWaist, float crownRadius,
                              float realHeightSpan, float realBaseHeight,
                              int spikeCount, float spikeHeightSpan, float spikeJitter,
                              float angleOffset, float time, long seed) {

        int segments = RADIAL_SEGMENTS;
        int rings = VERTICAL_RINGS;

        float[] xs = new float[(rings + 1) * segments];
        float[] ys = new float[(rings + 1) * segments];
        float[] zs = new float[(rings + 1) * segments];

        for (int r = 0; r <= rings; r++) {
            float t = (float) r / rings;
            float radiusHere = radiusAtT(t, radiusBottom, radiusWaist, crownRadius);
            float realHeightHere = realBaseHeight + realHeightSpan * t;

            for (int s = 0; s < segments; s++) {
                float angle = (float) (s * 2.0 * Math.PI / segments);

                float x = (float) (Math.cos(angle) * radiusHere);
                float z = (float) (Math.sin(angle) * radiusHere);
                float y = realHeightToLocalY(realHeightHere);

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

        float crownRealHeight = realBaseHeight + realHeightSpan;
        float crownLocalY = realHeightToLocalY(crownRealHeight);

        float centerLocalY = crownLocalY;
        for (int s = 0; s < segments; s++) {
            int sNext = (s + 1) % segments;
            float angleA = (float) (s * 2.0 * Math.PI / segments);
            float angleB = (float) (sNext * 2.0 * Math.PI / segments);

            float xa = (float) (Math.cos(angleA) * crownRadius);
            float za = (float) (Math.sin(angleA) * crownRadius);
            float xb = (float) (Math.cos(angleB) * crownRadius);
            float zb = (float) (Math.sin(angleB) * crownRadius);

            vert(consumer, mat, 0f, centerLocalY, 0f, 1.0f);
            vert(consumer, mat, xa, centerLocalY, za, 1.0f);
            vert(consumer, mat, xb, centerLocalY, zb, 1.0f);

            vertCount += 3;
        }

        for (int i = 0; i < spikeCount; i++) {
            float baseAngle = angleOffset + (float) (i * 2.0 * Math.PI / spikeCount);

            float angleJitter = (hash01(seed, i * 7 + 1) - 0.5f) * (float) (2.0 * Math.PI / spikeCount) * 0.9f;
            float centerAngle = baseAngle + angleJitter;

            float widthFactor = 0.55f + hash01(seed, i * 7 + 2) * 0.9f;
            float baseHalfWidth = (float) (2.0 * Math.PI / spikeCount) * 0.32f * widthFactor;

            float heightFactor = 0.45f + hash01(seed, i * 7 + 3) * 1.35f;
            float ownSpan = spikeHeightSpan * heightFactor;

            float radialPullback = crownRadius * (hash01(seed, i * 7 + 4) * 0.15f);
            float spikeBaseRadius = Math.max(crownRadius - radialPullback, crownRadius * 0.8f);

            float phaseOffset = hash01(seed, i * 7 + 5);
            float freqFactor = 0.8f + hash01(seed, i * 7 + 6) * 0.9f;
            float spikePhase = (time * 1.3f * freqFactor + phaseOffset) % 1.0f;
            if (spikePhase < 0f) spikePhase += 1f;
            float spikePulse = spikePhase < 0.5f ? spikePhase / 0.5f : (1f - spikePhase) / 0.5f;

            float tipRealHeight = crownRealHeight
                + ownSpan * (0.5f + 0.5f * spikePulse)
                + spikeJitter * (spikePulse - 0.5f);

            float leftAngle = centerAngle - baseHalfWidth;
            float rightAngle = centerAngle + baseHalfWidth;

            float baseLx = (float) (Math.cos(leftAngle) * spikeBaseRadius);
            float baseLz = (float) (Math.sin(leftAngle) * spikeBaseRadius);
            float baseRx = (float) (Math.cos(rightAngle) * spikeBaseRadius);
            float baseRz = (float) (Math.sin(rightAngle) * spikeBaseRadius);

            float tipDrift = crownRadius * 0.3f * (hash01(seed, i * 7 + 7) - 0.5f);
            float tipX = (float) (Math.cos(centerAngle) * tipDrift);
            float tipZ = (float) (Math.sin(centerAngle) * tipDrift);
            float tipLocalY = realHeightToLocalY(tipRealHeight);

            vert(consumer, mat, baseLx, crownLocalY, baseLz, 1.0f);
            vert(consumer, mat, baseRx, crownLocalY, baseRz, 1.0f);
            vert(consumer, mat, tipX, tipLocalY, tipZ, 1.0f);

            vertCount += 3;
        }
    }

    private static void vert(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float heightFraction) {
        consumer.addVertex(mat, x, y, z).setColor(1f, 1f, 1f, heightFraction);
    }
}
