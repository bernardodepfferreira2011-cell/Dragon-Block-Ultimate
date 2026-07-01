package net.dragonultimate.shader;

public class AuraState {

    private static boolean active = false;

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
}
