package net.dragonultimate.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class ModKeybinds {

    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.dragonblockultimate.open_menu",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V,
            "key.categories.dragonblockultimate"
    );

    public static final KeyMapping TOGGLE_AURA = new KeyMapping(
            "key.dragonblockultimate.toggle_aura",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            "key.categories.dragonblockultimate"
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
        event.register(TOGGLE_AURA);
    }

}
