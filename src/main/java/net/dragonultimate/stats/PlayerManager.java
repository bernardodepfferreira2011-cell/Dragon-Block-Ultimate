package net.dragonultimate.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dragonultimate.DragonBlockUltimate;
import net.dragonultimate.network.NetworkHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class PlayerManager {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DragonBlockUltimate.MOD_ID);

    public static final Supplier<AttachmentType<PlayerStats>> PLAYER_STATS =
            ATTACHMENT_TYPES.register("player_stats", () ->
                    AttachmentType.builder(PlayerStats::defaults)
                            .serialize(PlayerStats.CODEC)
                            .copyOnDeath()
                            .build()
            );

    private PlayerManager() {
    }

    private static Con.Stamina staminaSystem() {
        return new Con().new Stamina();
    }

    private static Spi spiSystem() {
        return new Spi();
    }

    public static PlayerStats getStats(Player player) {
        return player.getData(PLAYER_STATS);
    }

    public static void setStats(Player player, PlayerStats stats) {
        int con = Math.max(0, stats.con());
        int dex = Math.max(0, stats.dex());
        Con.Stamina staminaSystem = staminaSystem();
        int stamina = staminaSystem.clamp(stats.stamina(), con);
        int will = Math.max(0, stats.will());
        int spi = spiSystem().clamp(stats.spi(), con);

        player.setData(PLAYER_STATS, new PlayerStats(
                Math.max(0, stats.str()), con, dex, will, stamina, spi, Math.max(0, stats.trainerPoints())
        ));
        new Dex().applyAttributes(player, dex);
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sync(serverPlayer);
        }
    }

    public static void setCon(Player player, int con) {
        PlayerStats stats = getStats(player);
        setStats(player, new PlayerStats(
                stats.str(), Math.max(0, con), stats.dex(), stats.will(), stats.stamina(), stats.spi(),
                stats.trainerPoints()
        ));
        new Con().convertCon(Math.max(0, con), player);
    }

    public static void setDex(Player player, int dex) {
        PlayerStats stats = getStats(player);
        setStats(player, new PlayerStats(
                stats.str(), stats.con(), Math.max(0, dex), stats.will(), stats.stamina(), stats.spi(),
                stats.trainerPoints()
        ));
    }

    public static int getWill(Player player) {
        return getStats(player).will();
    }

    public static void setWill(Player player, int will) {
        setStats(player, getStats(player).withWill(will));
    }

    public static int getDefense(Player player) {
        return (int) new Dex().getDefenseBonus(getStats(player).dex());
    }

    public static float getSpeed(Player player) {
        return (float) (Dex.BASE_PLAYER_SPEED
                + new Dex().getSpeedBonus(getStats(player).dex()));
    }

    public static int getMaxStamina(Player player) {
        return staminaSystem().getMaxStamina(getStats(player).con());
    }

    public static void setStamina(Player player, int stamina) {
        setStats(player, getStats(player).withStamina(stamina));
    }

    public static boolean consumeStamina(Player player, int amount) {
        if (amount < 0 || getStats(player).stamina() < amount) {
            return false;
        }

        setStamina(player, getStats(player).stamina() - amount);
        return true;
    }

    public static void restoreStamina(Player player) {
        setStamina(player, getMaxStamina(player));
    }

    public static void restoreSpi(Player player) {
        setSpi(player, getMaxSpi(player));
    }

    public static int getMaxSpi(Player player) {
        return spiSystem().getMaxSpi(getStats(player).con());
    }

    public static void setSpi(Player player, int spi) {
        setStats(player, getStats(player).withSpi(spi));
    }

    public static void addTrainerPoints(Player player, int amount) {
        if (amount <= 0) {
            return;
        }

        PlayerStats stats = getStats(player);
        setStats(player, stats.withTrainerPoints(stats.trainerPoints() + amount));
    }

    public static boolean consumeSpi(Player player, int amount) {
        if (amount < 0 || getStats(player).spi() < amount) {
            return false;
        }

        setSpi(player, getStats(player).spi() - amount);
        return true;
    }

    public static void regenerateSpi(Player player) {
        PlayerStats stats = getStats(player);
        setSpi(player, spiSystem().regenerate(stats.spi(), stats.con()));
    }

    public static void regenerateStamina(Player player) {
        PlayerStats stats = getStats(player);
        setStamina(player, staminaSystem().regenerate(stats.stamina(), stats.con()));
    }

    /**
     * Limita o ki (spi) entre 0 e o ki maximo do player.
     * O maximo vem do mesmo calculo do getMaxSpi (CON), igual ao setStats.
     */
    public static int clampSpi(Player player, int spi) {
        return Math.clamp(spi, 0, getMaxSpi(player));
    }

    /** Dano maximo de um ataque de ki do player: o dano do Will dele. */
    public static float getMaxKiDamage(Player player) {
        return new Will().getKiDamage(getStats(player).will());
    }

    /** Limita o dano de ki entre 0 e o dano maximo do player. */
    public static float clampKiDamage(Player player, float damage) {
        return Math.clamp(damage, 0.0f, getMaxKiDamage(player));
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            PlayerStats stats = getStats(player);
            new Dex().applyAttributes(player, stats.dex());
            if (player.tickCount % Con.Stamina.REGEN_INTERVAL_TICKS != 0) {
                return;
            }
            new Con().convertCon(stats.con(), player);
            regenerateStamina(player);
            regenerateSpi(player);
            new Con().regenerateHealth(player, stats.con());
        }
    }

    public record PlayerStats(int str, int con, int dex, int will, int stamina, int spi, int trainerPoints) {
        public static final Codec<PlayerStats> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("str").forGetter(PlayerStats::str),
                        Codec.INT.fieldOf("con").forGetter(PlayerStats::con),
                        Codec.INT.optionalFieldOf("dex", 10).forGetter(PlayerStats::dex),
                        Codec.INT.optionalFieldOf("will", 10).forGetter(PlayerStats::will),
                        Codec.INT.fieldOf("stamina").forGetter(PlayerStats::stamina),
                        Codec.INT.fieldOf("spi").forGetter(PlayerStats::spi),
                        Codec.INT.optionalFieldOf("trainer_points", 0).forGetter(PlayerStats::trainerPoints)
                ).apply(instance, PlayerStats::new)
        );

        public static PlayerStats defaults() {
            int defaultCon = 10;
            return new PlayerStats(10, defaultCon, 10, 10,
                    new Con().new Stamina().getMaxStamina(defaultCon),
                    new Spi().getMaxSpi(defaultCon), 0);
        }

        public PlayerStats withStamina(int stamina) {
            return new PlayerStats(this.str, this.con, this.dex, this.will, stamina, this.spi, this.trainerPoints);
        }

        public PlayerStats withSpi(int spi) {
            return new PlayerStats(this.str, this.con, this.dex, this.will, this.stamina, spi, this.trainerPoints);
        }

        public PlayerStats withTrainerPoints(int trainerPoints) {
            return new PlayerStats(this.str, this.con, this.dex, this.will, this.stamina, this.spi,
                    Math.max(0, trainerPoints));
        }

        public PlayerStats withWill(int will) {
            return new PlayerStats(this.str, this.con, this.dex, Math.max(0, will), this.stamina, this.spi,
                    this.trainerPoints);
        }
    }
}