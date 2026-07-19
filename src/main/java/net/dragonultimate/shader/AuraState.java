package net.dragonultimate.shader;

public class AuraState {

    private static boolean active = false;

    // 0..1: quanto a aura já "carregou" desde que foi ativada.
    // Sobe suave ao ligar, desce suave ao desligar — dá o efeito de
    // power-up/power-down mesmo sem ter estados de transformação ainda.
    private static float chargeLevel = 0.0f;

    private static final float CHARGE_SPEED_PER_TICK = 0.05f;   // ~4 ticks pra chegar a 1.0 (0.2s)
    private static final float DISCHARGE_SPEED_PER_TICK = 0.08f; // desliga um pouco mais rápido que carrega

    private AuraState() {}

    public static boolean isActive() {
        return active;
    }

    public static void toggle() {
        active = !active;
    }

    public static void set(boolean value) {
        active = value;
    }

    /**
     * Retorna o nível atual de carga (0..1). É esse valor que alimenta o
     * uniform "intensity" do shader e o vFlicker/coreMask no fragment.
     */
    public static float getIntensity() {
        return chargeLevel;
    }

    /**
     * Chamado uma vez por client tick (ver KeybindHandler.onClientTick ou
     * um listener de tick dedicado). Avança a rampa de carga na direção
     * certa dependendo se a aura está ativa ou não.
     */
    public static void tick() {
        if (active) {
            if (chargeLevel < 1.0f) {
                chargeLevel = Math.min(1.0f, chargeLevel + CHARGE_SPEED_PER_TICK);
            }
        } else {
            if (chargeLevel > 0.0f) {
                chargeLevel = Math.max(0.0f, chargeLevel - DISCHARGE_SPEED_PER_TICK);
            }
        }
    }
}
