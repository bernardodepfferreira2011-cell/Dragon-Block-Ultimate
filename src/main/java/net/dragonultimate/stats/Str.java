package net.dragonultimate.stats;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class Str {
    public static final int DAMAGE_PER_STR = 5;
    public static final int CONSUMESTAMINA_PER_STR = 20;
    private int str;

    public Str() {
        this.str = 10;
    }

    public int getStr() {
        return this.str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public boolean attack( int str , @NotNull Player attacker, @NotNull LivingEntity target, Dex dex) {
        float danoBase = Math.max(0, str) * DAMAGE_PER_STR;
        float danoFinal = danoBase;

        if (target instanceof Player defensor) {
            float defesa = PlayerManager.getDefense(defensor);
            float reducao = Math.min(0.75f, defesa / 100.0f);
            danoFinal = danoBase * (1.0f - reducao);
        }

        if (target.hurt(attacker.damageSources().playerAttack(attacker), danoFinal)) {
            return PlayerManager.consumeStamina(attacker, CONSUMESTAMINA_PER_STR);
        }

        return false;
    }

    public int convertStr(int strPoints, @NotNull LivingEntity target) {
        int damage = strPoints * DAMAGE_PER_STR;
        target.hurt(target.damageSources().generic(), damage);
        return damage;
    }
}
