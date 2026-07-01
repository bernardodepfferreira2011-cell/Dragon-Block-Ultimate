package net.dragonultimate.models;

import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class RaceSkin extends HumanoidModel<Player> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(DragonBlockUltimate.MOD_ID, "race_skin"), "main"
    );

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        DragonBlockUltimate.MOD_ID, "textures/cc/body2sayan.png"
    );

    public RaceSkin(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        // Textura 512x512 - escala UV dobrada em relação ao padrão 64x64
        PartDefinition Root = part.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(64, 32).addBox(
                -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F,
                new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

        part.addOrReplaceChild("head",
            CubeListBuilder.create().texOffs(0, 0).addBox(
                -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

        part.addOrReplaceChild("hat",
            CubeListBuilder.create().texOffs(128, 0).addBox(
                -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                new CubeDeformation(0.5F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

        part.addOrReplaceChild("left_arm",
            CubeListBuilder.create().texOffs(128, 64).addBox(
                -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F));

        part.addOrReplaceChild("right_arm",
            CubeListBuilder.create().texOffs(64, 64).addBox(
                -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F));

        part.addOrReplaceChild("left_leg",
            CubeListBuilder.create().texOffs(64, 96).addBox(
                -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F));

        part.addOrReplaceChild("right_leg",
            CubeListBuilder.create().texOffs(0, 96).addBox(
                -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 512, 512);
    }
}
