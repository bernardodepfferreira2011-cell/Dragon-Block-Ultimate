package net.dragonultimate.save;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dragonultimate.DragonBlockUltimate;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SaveAuraColor {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DragonBlockUltimate.MOD_ID);

    public static final Supplier<AttachmentType<AuraColorData>> AURA_COLOR =
        ATTACHMENT_TYPES.register("aura_color", () ->
            AttachmentType.builder(() -> new AuraColorData(
                1.0f, 1.0f, 1.0f,  // inner: branco (padrão)
                1.0f, 1.0f, 1.0f   // outer: branco (padrão)
            ))
            .serialize(AuraColorData.CODEC)
            .copyOnDeath()
            .build()
        );

    public static AuraColorData getAuraColor(Player player) {
        return player.getData(AURA_COLOR);
    }

    public static void setAuraColor(Player player,
            float ir, float ig, float ib,
            float or, float og, float ob) {
        player.setData(AURA_COLOR, new AuraColorData(ir, ig, ib, or, og, ob));
    }

    public record AuraColorData(
        float innerR, float innerG, float innerB,
        float outerR, float outerG, float outerB
    ) {
        public static final Codec<AuraColorData> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                Codec.FLOAT.fieldOf("inner_r").forGetter(AuraColorData::innerR),
                Codec.FLOAT.fieldOf("inner_g").forGetter(AuraColorData::innerG),
                Codec.FLOAT.fieldOf("inner_b").forGetter(AuraColorData::innerB),
                Codec.FLOAT.fieldOf("outer_r").forGetter(AuraColorData::outerR),
                Codec.FLOAT.fieldOf("outer_g").forGetter(AuraColorData::outerG),
                Codec.FLOAT.fieldOf("outer_b").forGetter(AuraColorData::outerB)
            ).apply(i, AuraColorData::new)
        );

        public float[] innerArray() { return new float[]{innerR, innerG, innerB}; }
        public float[] outerArray() { return new float[]{outerR, outerG, outerB}; }
    }
}
