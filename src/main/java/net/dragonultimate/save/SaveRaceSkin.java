package net.dragonultimate.save;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SaveRaceSkin {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DragonBlockUltimate.MOD_ID);

    public static final Supplier<AttachmentType<RaceData>> RACE_DATA =
        ATTACHMENT_TYPES.register("race_data", () ->
            AttachmentType.builder(() -> new RaceData("default", "textures/cc/body2sayan.png"))
                .serialize(RaceData.CODEC)
                .copyOnDeath()
                .build()
        );

    public static RaceData getRaceData(Player player) {
        return player.getData(RACE_DATA);
    }

    public static void setRaceData(Player player, String raceName, String texturePath) {
        player.setData(RACE_DATA, new RaceData(raceName, texturePath));
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) return;
        RaceData data = event.getOriginal().getData(RACE_DATA);
        event.getEntity().setData(RACE_DATA, data);
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        RaceData data = event.getEntity().getData(RACE_DATA);
        event.getEntity().setData(RACE_DATA, data);
    }

    public record RaceData(String raceName, String texturePath) {
        public static final Codec<RaceData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("race_name").forGetter(RaceData::raceName),
                Codec.STRING.fieldOf("texture_path").forGetter(RaceData::texturePath)
            ).apply(instance, RaceData::new)
        );
    }
}