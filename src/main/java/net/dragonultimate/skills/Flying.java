package net.dragonultimate.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.joml.Math;

public class Flying {
    private boolean flying = false;

    public Flying ( ) {
    }

    public void start ( ) {

        if (Minecraft.getInstance ().player.isShiftKeyDown ()) {
            flying = true;
        }
    }

    public void stop ( ) {
        flying = false;
    }

    public boolean isFlying ( ) {
        return flying;
    }
}
