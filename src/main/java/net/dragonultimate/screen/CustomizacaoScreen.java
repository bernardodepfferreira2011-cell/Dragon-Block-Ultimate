package net.dragonultimate.screen;

import net.dragonultimate.DragonBlockUltimate;
import net.dragonultimate.save.SaveAuraColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class CustomizacaoScreen extends Screen {

    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(
        DragonBlockUltimate.MOD_ID, "textures/gui/gui.png");

    private static final int GuiW = 255;
    private static final int GuiH = 159;
    private static final int TextuW = 256;
    private static final int TextuH = 256;

    private final Player player;
    private final Inventory playerInventory;
    private final Screen parent;

    public CustomizacaoScreen(Player player, Inventory playerInventory, Screen parent) {
        super(Component.literal("Customização"));
        this.player = player;
        this.playerInventory = playerInventory;
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void init() {
        int guiX = this.width / 2 - GuiW / 2;
        int guiY = this.height / 2 - GuiH / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Aura Interna"), button -> abrirPicker(true))
            .bounds(guiX + 130, guiY + 20, 100, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Aura Externa"), button -> abrirPicker(false))
            .bounds(guiX + 130, guiY + 44, 100, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Voltar"), button ->
            Minecraft.getInstance().setScreen(parent)
        ).bounds(guiX + 130, guiY + 130, 100, 18).build());
    }

    private void abrirPicker(boolean interna) {
        SaveAuraColor.AuraColorData data = SaveAuraColor.getAuraColor(player);
        float[] corAtual = interna ? data.innerArray() : data.outerArray();

        Minecraft.getInstance().setScreen(new ColorPickerScreen(this, corAtual, novaCor -> {
            if (interna) {
                SaveAuraColor.setAuraColor(player,
                    novaCor[0], novaCor[1], novaCor[2],
                    data.outerR(), data.outerG(), data.outerB());
            } else {
                SaveAuraColor.setAuraColor(player,
                    data.innerR(), data.innerG(), data.innerB(),
                    novaCor[0], novaCor[1], novaCor[2]);
            }
        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(BG, this.width / 2 - GuiW / 2, this.height / 2 - GuiH / 2, 0, 0, GuiW, GuiH, TextuW, TextuH);

        int playerX = this.width / 2 - GuiW / 2 + 35;
        int playerY = this.height / 2 + 60;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
            graphics,
            playerX - 30, playerY - 110,
            playerX + 30, playerY,
            50, 0.0625F,
            mouseX, mouseY,
            this.player
        );

        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
