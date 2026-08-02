package net.dragonultimate.models.render;

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
import org.joml.Matrix4f;

public class AuraRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final int RADIAL_SEGMENTS = 32;
    private static final int VERTICAL_RINGS = 14;

    private static final float SPIKE_CYCLE_TICKS = 55f;
    private static final int NUM_PEAKS = 7;

    private static final RenderType AURA_RENDER_TYPE =
        AuraRenderTypes.aura(AuraRenderTypes.AuraBlendMode.TRANSLUCENT);

    public AuraRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                        AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                        float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!AuraState.isActive()) return;
        if (AuraShaderManager.getShader() == null) return;

        SaveAuraColor.AuraColorData cor = SaveAuraColor.getAuraColor(player);
        VertexConsumer consumer = buffer.getBuffer(AURA_RENDER_TYPE);
        Matrix4f mat = poseStack.last().pose();

        AuraShaderManager.setStyle(2.5f, 1.0f);

        float baseCycle = (ageInTicks % SPIKE_CYCLE_TICKS) / SPIKE_CYCLE_TICKS;

        float[] out = cor.outerArray();
        addSpikyCone(consumer, mat, 0.55f, 2.2f, 0.75f, 1.1f, baseCycle,
            out[0], out[1], out[2], 0.7f);

        float[] in = cor.innerArray();
        addSpikyCone(consumer, mat, 0.32f, 2.0f, 0.75f, 0.7f, baseCycle,
            in[0], in[1], in[2], 0.85f);
    }

    private void addSpikyCone(VertexConsumer consumer, Matrix4f mat, float baseRadius, float height,
                               float taper, float peakStrength, float baseCycle,
                               float r, float g, float b, float a) {
        for (int ring = 0; ring < VERTICAL_RINGS; ring++) {
            float t0 = (float) ring / VERTICAL_RINGS;
            float t1 = (float) (ring + 1) / VERTICAL_RINGS;

            float y0 = -0.1f + t0 * (height + 0.1f);
            float y1 = -0.1f + t1 * (height + 0.1f);

            for (int seg = 0; seg < RADIAL_SEGMENTS; seg++) {
                float a0 = (float) (seg * 2.0 * Math.PI / RADIAL_SEGMENTS);
                float a1 = (float) ((seg + 1) * 2.0 * Math.PI / RADIAL_SEGMENTS);

                float finalR00 = spikyRadius(baseRadius, taper, t0, a0, peakStrength, baseCycle);
                float finalR10 = spikyRadius(baseRadius, taper, t0, a1, peakStrength, baseCycle);
                float finalR01 = spikyRadius(baseRadius, taper, t1, a0, peakStrength, baseCycle);
                float finalR11 = spikyRadius(baseRadius, taper, t1, a1, peakStrength, baseCycle);

                float x00 = (float)(Math.cos(a0) * finalR00), z00 = (float)(Math.sin(a0) * finalR00);
                float x10 = (float)(Math.cos(a1) * finalR10), z10 = (float)(Math.sin(a1) * finalR10);
                float x01 = (float)(Math.cos(a0) * finalR01), z01 = (float)(Math.sin(a0) * finalR01);
                float x11 = (float)(Math.cos(a1) * finalR11), z11 = (float)(Math.sin(a1) * finalR11);

                vert(consumer, mat, x00, y0, z00, r, g, b, a);
                vert(consumer, mat, x10, y0, z10, r, g, b, a);
                vert(consumer, mat, x01, y1, z01, r, g, b, a);

                vert(consumer, mat, x10, y0, z10, r, g, b, a);
                vert(consumer, mat, x11, y1, z11, r, g, b, a);
                vert(consumer, mat, x01, y1, z01, r, g, b, a);
            }
        }
    }

    private float spikyRadius(float baseRadius, float taper, float heightT, float angle,
                               float peakStrength, float baseCycle) {
        float r = baseRadius * (1.0f - taper * (1.0f - heightT));

        // forma da coroa: os picos ficam nos múltiplos de (2π/NUM_PEAKS), os vales na metade entre eles.
        float lobe = (float) Math.pow(Math.abs(Math.cos(angle * NUM_PEAKS / 2.0)), 3.0);

        // CORREÇÃO: desloca o corte de fase em meio-lóbulo (π/NUM_PEAKS) pra cair
        // exatamente no VALE (lobe≈0) em vez de na PONTA (lobe≈1) de cada pico.
        // Sem isso, a fase pula de valor bem no topo do pico, cortando o afunilamento
        // suave num degrau reto de 90° - o "triângulo retângulo" que você viu.
        float shiftedAngle = angle + (float) (Math.PI / NUM_PEAKS);
        float normalized = (shiftedAngle / (float) (2 * Math.PI)) * NUM_PEAKS;
        int peakBucket = ((int) Math.floor(normalized)) % NUM_PEAKS;
        if (peakBucket < 0) peakBucket += NUM_PEAKS;

        float peakPhase = peakBucket / (float) NUM_PEAKS;
        float peakProgress = (baseCycle + peakPhase) % 1.0f;
        float peakArc = (float) Math.sin(peakProgress * Math.PI);

        float peakGrowth = heightT;

        return r * (1.0f + peakStrength * lobe * peakArc * peakGrowth);
    }

    private static void vert(VertexConsumer consumer, Matrix4f mat, float x, float y, float z,
                              float r, float g, float b, float a) {
        consumer.addVertex(mat, x, y, z).setColor(r, g, b, a);
    }
}
