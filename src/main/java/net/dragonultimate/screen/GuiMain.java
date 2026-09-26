package net.dragonultimate.screen;

import net.dragonultimate.DragonBlockUltimate;
import net.dragonultimate.screen.buttons.ButtonManager;
import net.minecraft.client.gui.GuiGraphics;
import net.dragonultimate.stats.PlayerManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class GuiMain extends Screen {
    protected static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        DragonBlockUltimate.MOD_ID, "textures/gui/gui.png");
    protected static final int GUI_WIDTH = 255;
    protected static final int GUI_HEIGHT = 159;
    protected static final int TEXTURE_WIDTH = 256;
    protected static final int TEXTURE_HEIGHT = 256;
    private final Player player;


    public GuiMain(Component title, Player player) {
        super (title);
        this.player = player;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(
            GUI_TEXTURE,
            this.width / 2 - GUI_WIDTH / 2,
            this.height / 2 - GUI_HEIGHT / 2,
            0, 0, GUI_WIDTH, GUI_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT
    );
    }

    protected final <T extends ButtonManager> T addButton(T button) {
        return this.addRenderableWidget(button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        float escala = 0.75F;

        graphics.pose().pushPose();
        graphics.pose().scale(escala, escala, 1.0F);

        PlayerManager.PlayerStats stats = PlayerManager.getStats(this.player);
        graphics.drawString(this.font, "str: " + stats.str(), 107, 120, 0xFFFFFF, false);
        graphics.drawString(this.font, "TP: " + stats.trainerPoints(), 95, 90, 0xFFFFFF, false);
    }
}
