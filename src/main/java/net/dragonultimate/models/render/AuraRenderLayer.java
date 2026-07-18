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

    private static final int RADIAL_SEGMENTS = 32;
    private static final int VERTICAL_RINGS = 12;
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

        if (!active) return;
        if (!shaderOk) return;

        SaveAuraColor.AuraColorData cor = SaveAuraColor.getAuraColor(player);
        VertexConsumer consumer = buffer.getBuffer(AURA_RENDER_TYPE);
        Matrix4f mat = poseStack.last().pose();

        vertCount = 0;

        AuraShaderManager.setUniforms(1.0f, cor.outerArray(), cor.outerArray(), 0.55f, 0.15f, 3.0f);
        addFlowingAuraSpikes(consumer, mat, 1.0f, 2.35f, 0.55f, ageInTicks + partialTick);

        AuraShaderManager.setUniforms(1.0f, cor.innerArray(), cor.innerArray(), 0.9f, 0.3f, 2.0f);
        addFlowingAuraSpikes(consumer, mat, 0.72f, 2.15f, 0.42f, ageInTicks + partialTick + 0.6f);
    }

    /**
     * Gera uma casca espiralada e irregular em torno do corpo do jogador,
     * parecida com uma aura de Dragon Ball, em vez de um único cone.
     */
    private void addFlowingAuraSpikes(VertexConsumer consumer, Matrix4f mat, float baseRadius, float height, float verticalOffset, float time) {
        int spikeCount = 18;
        float speed = 1.65f;

        for (int i = 0; i < spikeCount; i++) {
            float phase = (time * speed + i * 0.11f) % 1.0f;
            if (phase < 0.0f) phase += 1.0f;

            float pulse = 0.0f;
            if (phase < 0.5f) {
                pulse = phase / 0.5f;
            } else {
                pulse = (1.0f - phase) / 0.5f;
            }

            float angle = (float) (i * 2.0 * Math.PI / spikeCount);
            float nextAngle = (float) ((i + 1) * 2.0 * Math.PI / spikeCount);
            float midAngle = (angle + nextAngle) * 0.5f;

            float orbitRadius = baseRadius * (0.45f + 0.15f * pulse);
            float tipRadius = baseRadius * (0.7f + 0.25f * pulse);
            float lift = verticalOffset + height * 0.92f * phase;
            float tipHeight = lift + height * 0.16f * pulse;
            float baseHeight = lift - height * 0.03f * (1.0f + pulse);

            float sway = (float) (Math.sin(time * 0.35f + i * 0.8f) * 0.06f + Math.cos(midAngle * 3.0f + time * 0.2f) * 0.03f);
            float x0 = (float) (Math.cos(angle) * orbitRadius + sway);
            float z0 = (float) (Math.sin(angle) * orbitRadius - sway);
            float x1 = (float) (Math.cos(nextAngle) * orbitRadius - sway);
            float z1 = (float) (Math.sin(nextAngle) * orbitRadius + sway);
            float xt = (float) (Math.cos(midAngle) * tipRadius);
            float zt = (float) (Math.sin(midAngle) * tipRadius);

            vert(consumer, mat, x0, baseHeight, z0);
            vert(consumer, mat, x1, baseHeight, z1);
            vert(consumer, mat, xt, tipHeight, zt);

            vertCount += 3;
        }
    }

    private static void vert(VertexConsumer consumer, Matrix4f mat, float x, float y, float z) {
        consumer.addVertex(mat, x, y, z).setColor(1f, 1f, 1f, 1f);
    }
}
