package net.dragonultimate.models.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dragonultimate.models.RaceSkin;
import net.dragonultimate.save.SaveRaceSkin;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.dragonultimate.DragonBlockUltimate;

public class RaceSkinRender extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final RaceSkin model;

    public RaceSkinRender(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, RaceSkin model) {
        super(parent);
        this.model = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        SaveRaceSkin.RaceData data = SaveRaceSkin.getRaceData(player);

        // Whitelist: só troca a textura quando a raça for exatamente Sayajin.
        // Qualquer outro valor (default, Humano, etc) mantém a skin normal do Mojang (Alex/Steve).
        if (!data.raceName().equals("Sayajin")) return;

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
            DragonBlockUltimate.MOD_ID, data.texturePath()
        );

        PlayerModel<AbstractClientPlayer> parentModel = this.getParentModel();
        this.model.head.copyFrom(parentModel.head);
        this.model.hat.copyFrom(parentModel.hat);
        this.model.body.copyFrom(parentModel.body);
        this.model.leftArm.copyFrom(parentModel.leftArm);
        this.model.rightArm.copyFrom(parentModel.rightArm);
        this.model.leftLeg.copyFrom(parentModel.leftLeg);
        this.model.rightLeg.copyFrom(parentModel.rightLeg);

        var vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
    }
}
