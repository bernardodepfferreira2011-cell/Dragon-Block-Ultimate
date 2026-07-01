package net.dragonultimate.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.dragonultimate.screen.MenuInicial;
import net.dragonultimate.shader.AuraState;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class KeybindHandler {

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

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
