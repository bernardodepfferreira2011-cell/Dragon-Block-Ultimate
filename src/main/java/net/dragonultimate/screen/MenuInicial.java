package net.dragonultimate.screen;

import net.dragonultimate.DragonBlockUltimate;
import net.dragonultimate.save.SaveRaceSkin;
import net.dragonultimate.screen.buttons.NextButton;
import net.dragonultimate.screen.buttons.PreviousButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Optional;

public class MenuInicial extends Screen {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        DragonBlockUltimate.MOD_ID, "textures/gui/gui.png");
    private static final int GUI_WIDTH = 255;
    private static final int GUI_HEIGHT = 159;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    @Override
    public Optional < GuiEventListener > getChildAt ( double mouseX , double mouseY ) {
        return super.getChildAt ( mouseX , mouseY );
    }

    public enum Raca {
        HUMANO("Humano", "textures/entity/player/slim/alex.png"),
        SAYAJIN("Sayajin", "textures/cc/sayan1.png");

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

    private final Player player;
    private final Inventory playerInventory;
    private Raca racaAtual = Raca.HUMANO;

    public MenuInicial(Player player, Inventory playerInventory, Component title) {
        super(title);
        this.player = player;
        this.playerInventory = playerInventory;
    }

    @Override
    protected void init() {
        int guiX = this.width / 2 - GUI_WIDTH / 2;
        int guiY = this.height / 2 - GUI_HEIGHT / 2;

        this.addRenderableWidget(new PreviousButton(
            guiX + 80, guiY + 120,
            button -> { racaAtual = racaAtual.anterior(); salvarRaca(); }
        ));

        this.addRenderableWidget(new NextButton(
            guiX + 165, guiY + 120,
            button -> { racaAtual = racaAtual.proxima(); salvarRaca(); }
        ));

    }

    private void salvarRaca() {
        SaveRaceSkin.setRaceData(this.player, racaAtual.getNome(), racaAtual.getTextura());

        // DEBUG - remover depois
        SaveRaceSkin.RaceData check = SaveRaceSkin.getRaceData(this.player);
        this.player.displayClientMessage(Component.literal(
            "[DEBUG salvarRaca] escreveu=" + racaAtual.getNome()
            + " | releu=" + check.raceName() + " / " + check.texturePath()
        ), false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(
            GUI_TEXTURE,
            this.width / 2 - GUI_WIDTH / 2,
            this.height / 2 - GUI_HEIGHT / 2,
            0, 0, GUI_WIDTH, GUI_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        int playerX = this.width / 2 - GUI_WIDTH / 2 + 35;
        int playerY = this.height / 2 + 60;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
            graphics,
            playerX - 30, playerY - 110,
            playerX + 30, playerY,
            50, 0.0625F,
            mouseX, mouseY,
            this.player
        );

        int guiX = this.width / 2 - GUI_WIDTH / 2;
        int guiY = this.height / 2 - GUI_HEIGHT / 2;
        graphics.drawCenteredString(this.font, racaAtual.getNome(),
            guiX + 127, guiY + 122, 0xFFFFFF);


    }

}
