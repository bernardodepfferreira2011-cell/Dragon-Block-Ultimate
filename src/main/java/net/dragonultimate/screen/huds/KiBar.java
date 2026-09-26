package net.dragonultimate.screen.huds;

import net.dragonultimate.alinhamento.Alinhamento;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class KiBar {
    public void render( @NotNull Minecraft mc, GuiGraphics g, Player player) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        float scale = Math.clamp(Math.min(screenWidth / 854.0f, screenHeight / 480.0f),
                0.75f, 1.5f);
        int width = Math.round(150.0f * scale);
        int height = Math.max(10, Math.round(10.0f * scale));
        int blackBorder = 1;
        int shadedBorder = 1;
        int fillBorder = blackBorder + shadedBorder;
        int x = 7;
        int y = 10;
        int radius = Math.min(Math.round(4.0f * scale), Math.min(width, height) / 2);
        Alinhamento alinhamento = Alinhamento.Neutral;
        int[] colors = kiBarColors(alinhamento);

        drawRoundedFill(g, x, y, width, height, radius, fillBorder,
                colors[0]);
        drawRoundedBorder(g, x + blackBorder, y + blackBorder,
                width - blackBorder * 2, height - blackBorder * 2,
                Math.max(0, radius - blackBorder), shadedBorder,
                colors[1], colors[2]);
        drawRoundedBorder(g, x, y, width, height, radius, blackBorder,
                0xFF000000, 0xFF000000);
    }

    private int[] kiBarColors(Alinhamento alinhamento) {
        return switch (alinhamento) {
            case Good -> new int[]{0xFF287BFF, 0xFF69A7FF, 0xFF123C99};
            case Maligno -> new int[]{0xFFE53935, 0xFFFF6B6B, 0xFF8B1111};
            case Neutral -> new int[]{0xFF8E44AD, 0xFFC078FF, 0xFF4A176B};
        };
    }

    private void drawRoundedFill(GuiGraphics graphics, int x, int y, int width, int height,
                                 int radius, int border, int color) {
        int innerX = x + border;
        int innerY = y + border;
        int innerWidth = Math.max(0, width - border * 2);
        int innerHeight = Math.max(0, height - border * 2);
        int innerRadius = Math.max(0, radius - border);

        for (int pixelY = innerY; pixelY < innerY + innerHeight; pixelY++) {
            for (int pixelX = innerX; pixelX < innerX + innerWidth; pixelX++) {
                if (insideRoundedRectangle(pixelX + 0.5f, pixelY + 0.5f,
                        innerX, innerY, innerWidth, innerHeight, innerRadius)) {
                    graphics.fill(pixelX, pixelY, pixelX + 1, pixelY + 1, color);
                }
            }
        }
    }

    private void drawRoundedBorder(GuiGraphics graphics, int x, int y, int width, int height,
                                   int radius, int border, int lightColor, int darkColor) {
        int innerX = x + border;
        int innerY = y + border;
        int innerWidth = Math.max(0, width - border * 2);
        int innerHeight = Math.max(0, height - border * 2);
        int innerRadius = Math.max(0, radius - border);

        for (int pixelY = y; pixelY < y + height; pixelY++) {
            for (int pixelX = x; pixelX < x + width; pixelX++) {
                boolean insideOuter = insideRoundedRectangle(
                        pixelX + 0.5f, pixelY + 0.5f, x, y, width, height, radius);
                boolean insideInner = insideRoundedRectangle(
                        pixelX + 0.5f, pixelY + 0.5f,
                        innerX, innerY, innerWidth, innerHeight, innerRadius);

                if (insideOuter && !insideInner) {
                    boolean lightSide = pixelY < y + border
                            || pixelX >= x + width - border;
                    int color = lightSide ? lightColor : darkColor;
                    graphics.fill(pixelX, pixelY, pixelX + 1, pixelY + 1, color);
                }
            }
        }
    }

    private boolean insideRoundedRectangle(float pointX, float pointY, int x, int y,
                                           int width, int height, int radius) {
        if (width <= 0 || height <= 0) {
            return false;
        }

        float closestX = Math.clamp(pointX, x + radius, x + width - radius);
        float closestY = Math.clamp(pointY, y + radius, y + height - radius);
        float deltaX = pointX - closestX;
        float deltaY = pointY - closestY;
        return deltaX * deltaX + deltaY * deltaY <= radius * radius;
    }
}
