package net.dragonultimate.screen;

import net.dragonultimate.DragonBlockUltimate;
import net.dragonultimate.save.SaveRaceSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.GuiGraphics;

public class MenuInicial extends Screen {

    public enum Raca {
        HUMANO("Humano", "textures/entity/player/slim/alex.png"),
        SAYAJIN("Sayajin", "textures/cc/body2sayan.png");

        private final String nome;
        private final String textura;

        Raca(String nome, String textura) {
            this.nome = nome;
            this.textura = textura;
        }

        public String getNome() { return nome; }
        public String getTextura() { return textura; }

        public Raca anterior() {
            Raca[] v = values();
            int i = this.ordinal() - 1;
            return v[i < 0 ? v.length - 1 : i];
        }

        public Raca proxima() {
            Raca[] v = values();
            int i = this.ordinal() + 1;
            return v[i >= v.length ? 0 : i];
        }
    }

    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(
        DragonBlockUltimate.MOD_ID, "textures/gui/gui.png");

    private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(
        DragonBlockUltimate.MOD_ID, "textures/gui/icons.png");

    private static final int GuiW = 255;
    private static final int GuiH = 159;
    private static final int TextuW = 256;
    private static final int TextuH = 256;

    private final Player player;
    private final Inventory playerInventory;
    private Raca racaAtual = Raca.HUMANO;

    public MenuInicial(Player player, Inventory playerInventory, Component title) {
        super(title);
        this.player = player;
        this.playerInventory = playerInventory;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void init() {
        int guiX = this.width / 2 - GuiW / 2;
        int guiY = this.height / 2 - GuiH / 2;

        this.addRenderableWidget(new SpriteButton(
            guiX + 80, guiY + 120, 10, 10,
            0, 0, 0, 10, ICONS,
            button -> { racaAtual = racaAtual.anterior(); salvarRaca(); }
        ));

        this.addRenderableWidget(new SpriteButton(
            guiX + 165, guiY + 120, 10, 10,
            10, 0, 10, 10, ICONS,
            button -> { racaAtual = racaAtual.proxima(); salvarRaca(); }
        ));

        this.addRenderableWidget(Button.builder(Component.literal("Customizar"), button ->
            Minecraft.getInstance().setScreen(new CustomizacaoScreen(this.player, this.playerInventory, this))
        ).bounds(guiX + 190, guiY + 15, 55, 18).build());
    }

    private void salvarRaca() {
        SaveRaceSkin.setRaceData(this.player, racaAtual.getNome(), racaAtual.getTextura());
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

        int guiX = this.width / 2 - GuiW / 2;
        int guiY = this.height / 2 - GuiH / 2;
        graphics.drawCenteredString(this.font, racaAtual.getNome(),
            guiX + 127, guiY + 122, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private static class SpriteButton extends Button {
        private final int uNormal, vNormal, uPressed, vPressed;
        private final ResourceLocation texture;
        private boolean pressed = false;

        public SpriteButton(int x, int y, int width, int height,
                            int uNormal, int vNormal, int uPressed, int vPressed,
                            ResourceLocation texture, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.uNormal = uNormal;
            this.vNormal = vNormal;
            this.uPressed = uPressed;
            this.vPressed = vPressed;
            this.texture = texture;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                pressed = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            pressed = false;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int u = pressed ? uPressed : uNormal;
            int v = pressed ? vPressed : vNormal;
            graphics.blit(texture, this.getX(), this.getY(), u, v, this.width, this.height, 256, 256);
        }
    }
}
