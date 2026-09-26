package net.dragonultimate.stats;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class Will {
    public static final int KI_DAMAGE_PER_WILL = 12;
    public static final int KI_CONSUME_PER_WILL = 15;
    private int will;

    public Will() {
        this.will = 10;
    }

    public int getWill() {
        return this.will;
    }

    public void setWill(int will) {
        this.will = will;
    }

    /** Dano bruto de um ataque de ki, sem a defesa do alvo. */
    public float getKiDamage(int willPoints) {
        return Math.max(0, willPoints) * KI_DAMAGE_PER_WILL;
    }

    /** Custo de ki (Spi) de um ataque. */
    public int getKiCost() {
        return KI_CONSUME_PER_WILL;
    }

    /** Dano final depois da defesa do alvo (a defesa so vale para Player, como no Str). */
    public float calculateKiDamage(int willPoints, @NotNull LivingEntity target) {
        float danoBase = getKiDamage(willPoints);

        if (target instanceof Player defensor) {
            float defesa = PlayerManager.getDefense(defensor);
            float reducao = Math.min(0.75f, defesa / 100.0f);
            return danoBase * (1.0f - reducao);
        }

        return danoBase;
    }

    /** True se o atacante tem ki suficiente para um ataque. Nao gasta nada. */
    public boolean hasKi(@NotNull Player attacker) {
        return PlayerManager.getStats(attacker).spi() >= getKiCost();
    }

    /** Gasta o ki do ataque. False (sem gastar) se nao houver ki suficiente. */
    public boolean consumeKi(@NotNull Player attacker) {
        return PlayerManager.consumeSpi(attacker, getKiCost());
    }
}