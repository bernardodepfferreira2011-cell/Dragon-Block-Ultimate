package net.dragonultimate.stats;

import net.minecraft.world.entity.player.Player;

public class Dex {
    public static final int DEFENSE_PER_DEX = 5;
    public static final double SPEED_PER_DEX = 0.005D;
    public static final float BASE_PLAYER_SPEED = 0.1F;

    private int dex;

    public Dex() {
        this.dex = 10;
    }

    public int getDex() {
        return this.dex;
    }

    public void setDex(int dex) {
        this.dex = Math.max(0, dex);
    }

    public int convertDex(int dexPoints) {
        setDex(dexPoints);
        return this.dex;
    }


    public double getDefenseBonus(int dexPoints) {
        return Math.max(0, dexPoints) * DEFENSE_PER_DEX;
    }

    public double getSpeedBonus(int dexPoints) {
        return Math.max(0, dexPoints) * SPEED_PER_DEX;
    }

    public void applySpeed(Player player, int dexPoints) {
        player.setSpeed((float) (BASE_PLAYER_SPEED + getSpeedBonus(dexPoints)));
    }

    public void applyAttributes(Player player, int dexPoints) {
        applySpeed(player, dexPoints);
    }

    public class Speed extends Dex {
        public static final double SPEED_PER_DEX = Dex.SPEED_PER_DEX;

        public double convertSpeed(int dexPoints) {
            return getSpeedBonus(dexPoints);
        }

        public double convertDefense(int dexPoints) {
            return getDefenseBonus(dexPoints);
        }

        public void applySpeed(Player player, int dexPoints) {
            Speed.this.applySpeed(player, dexPoints);
        }
    }
}
