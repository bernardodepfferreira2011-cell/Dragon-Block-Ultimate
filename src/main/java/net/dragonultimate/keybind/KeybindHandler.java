package net.dragonultimate.keybind;

import net.dragonultimate.aura.AuraData;
import net.dragonultimate.aura.SaveAuraData;
import net.dragonultimate.network.ToggleAuraPayload;
import net.minecraft.client.Minecraft;
import net.dragonultimate.screen.MenuInicial;
import net.dragonultimate.screen.GuiMain;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static net.minecraft.network.chat.Component.*;

public class KeybindHandler {

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (ModKeybinds.OPEN_MENU_MAIN.consumeClick()) {
            mc.setScreen(new GuiMain (
                    literal("MenuMain") ,
                    mc.player
                        ) );
        }
        while (ModKeybinds.OPEN_MENU.consumeClick()) {
            mc.setScreen(new MenuInicial(
                    mc.player,
                    mc.player.getInventory(),
                    literal("Menu")
            ));
        }
        while (ModKeybinds.TOGGLE_AURA.consumeClick()) {
            AuraData data = SaveAuraData.getAuraData(mc.player);
            PacketDistributor.sendToServer(new ToggleAuraPayload(!data.active()));
        }

    }
}
