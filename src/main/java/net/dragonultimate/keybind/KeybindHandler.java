package net.dragonultimate.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.dragonultimate.screen.MenuInicial;
import net.dragonultimate.shader.AuraState;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class KeybindHandler {

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        AuraState.tick();

        // === TESTE DE REFERENCIA -- particula vanilla em altura conhecida ===
        // END_ROD exatamente 2 blocos acima dos pes do jogador, em
        // coordenada de MUNDO (zero ambiguidade). So aparece enquanto a
        // aura de teste esta ativa. Serve pra comparar contra onde o
        // marcador magenta da nossa geometria aparece na mesma altura.
        if (AuraState.isActive() && mc.level != null && mc.player.tickCount % 20 == 0) {
            mc.level.addParticle(
                ParticleTypes.END_ROD,
                mc.player.getX(), mc.player.getY() + 2.0, mc.player.getZ(),
                0.0, 0.0, 0.0
            );
        }
        // === FIM TESTE ===

        while (ModKeybinds.OPEN_MENU.consumeClick()) {
            mc.setScreen(new MenuInicial(
                mc.player,
                mc.player.getInventory(),
                Component.literal("Menu")
            ));
        }

        while (ModKeybinds.TOGGLE_AURA.consumeClick()) {
            AuraState.toggle();
            mc.player.displayClientMessage(
                Component.literal(AuraState.isActive() ? "Aura: ATIVADA" : "Aura: DESATIVADA"),
                true
            );
        }
    }
}
