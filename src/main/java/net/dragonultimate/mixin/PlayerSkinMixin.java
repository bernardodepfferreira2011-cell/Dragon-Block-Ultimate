package net.dragonultimate.mixin;

import net.dragonultimate.DragonBlockUltimate;
import net.dragonultimate.save.SaveRaceSkin;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class PlayerSkinMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void dragonblockultimate$overrideSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;

        SaveRaceSkin.RaceData data = SaveRaceSkin.getRaceData(self);
        if (!data.raceName().equals("Sayajin")) return;

        PlayerSkin original = cir.getReturnValue();

        ResourceLocation customTexture = ResourceLocation.fromNamespaceAndPath(
            DragonBlockUltimate.MOD_ID, data.texturePath()
        );

        PlayerSkin overridden = new PlayerSkin(
            customTexture,
            original.textureUrl(),
            original.capeTexture(),
            original.elytraTexture(),
            original.model(),
            original.secure()
        );

        cir.setReturnValue(overridden);
    }
}
