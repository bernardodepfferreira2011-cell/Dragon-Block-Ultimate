package net.dragonultimate.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ColorPickerScreen extends Screen {

    private final Screen parent;
    private final Consumer<float[]> onColorPicked;

    private float hue;
    private float saturation;
    private float value;

    private boolean draggingSV = false;
    private boolean draggingHue = false;

    private static final int BOX_SIZE = 120;
    private static final int HUE_BAR_WIDTH = 16;
    private static final int PADDING = 10;

    private int svX, svY;
    private int hueX, hueY;

    public ColorPickerScreen(Screen parent, float[] initialColorRgb, Consumer<float[]> onColorPicked) {
        super(Component.literal("Escolher Cor"));
        this.parent = parent;
        this.onColorPicked = onColorPicked;

        float[] hsv = rgbToHsv(initialColorRgb[0], initialColorRgb[1], initialColorRgb[2]);
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.value = hsv[2];
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void init() {
        int totalWidth = BOX_SIZE + PADDING + HUE_BAR_WIDTH;
        int startX = this.width / 2 - totalWidth / 2;
        int startY = this.height / 2 - BOX_SIZE / 2 - 25;

        svX = startX;
        svY = startY;
        hueX = startX + BOX_SIZE + PADDING;
        hueY = startY;

        int swatchY = svY + BOX_SIZE + 15;

        this.addRenderableWidget(Button.builder(Component.literal("Definir Cor"), button -> {
            float[] rgb = currentRgbFloat();
            onColorPicked.accept(rgb);
            Minecraft.getInstance().setScreen(parent);
        }).bounds(startX, swatchY + 36, 80, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Cancelar"), button ->
            Minecraft.getInstance().setScreen(parent)
        ).bounds(startX + 90, swatchY + 36, 80, 18).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.drawCenteredString(this.font, this.title, this.width / 2, svY - 14, 0xFFFFFF);

        for (int x = 0; x < BOX_SIZE; x++) {
            float sat = x / (float) BOX_SIZE;
            int top = hsvToRgbPacked(hue, sat, 1f);
            graphics.fillGradient(svX + x, svY, svX + x + 1, svY + BOX_SIZE, top, 0xFF000000);
        }
        drawBorder(graphics, svX - 1, svY - 1, BOX_SIZE + 2, BOX_SIZE + 2, 0xFFFFFFFF);

        for (int y = 0; y < BOX_SIZE; y++) {
            float hueAtY = (y / (float) BOX_SIZE) * 360f;
            int color = hsvToRgbPacked(hueAtY, 1f, 1f);
            graphics.fill(hueX, hueY + y, hueX + HUE_BAR_WIDTH, hueY + y + 1, color);
        }
        drawBorder(graphics, hueX - 1, hueY - 1, HUE_BAR_WIDTH + 2, BOX_SIZE + 2, 0xFFFFFFFF);

        int ix = svX + (int) (saturation * BOX_SIZE);
        int iy = svY + (int) ((1 - value) * BOX_SIZE);
        graphics.fill(ix - 4, iy - 4, ix + 4, iy + 4, 0xFF000000);
        graphics.fill(ix - 2, iy - 2, ix + 2, iy + 2, 0xFFFFFFFF);

        int hy = hueY + (int) ((hue / 360f) * BOX_SIZE);
        graphics.fill(hueX - 3, hy - 1, hueX + HUE_BAR_WIDTH + 3, hy + 1, 0xFF000000);
        graphics.fill(hueX - 2, hy, hueX + HUE_BAR_WIDTH + 2, hy + 1, 0xFFFFFFFF);

        int[] rgb = currentRgbInt();
        int swatchY = svY + BOX_SIZE + 15;
        int totalWidth = BOX_SIZE + PADDING + HUE_BAR_WIDTH;

        graphics.fill(svX, swatchY, svX + totalWidth, swatchY + 16, hsvToRgbPacked(hue, saturation, value));
        drawBorder(graphics, svX - 1, swatchY - 1, totalWidth + 2, 18, 0xFFFFFFFF);

        String rgbText = "RGB: " + rgb[0] + ", " + rgb[1] + ", " + rgb[2];
        String hexText = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
        graphics.drawCenteredString(this.font, rgbText, this.width / 2, swatchY + 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font, hexText, this.width / 2, swatchY + 31, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (isInsideSV(mouseX, mouseY)) {
            draggingSV = true;
            updateSV(mouseX, mouseY);
            return true;
        }
        if (isInsideHue(mouseX, mouseY)) {
            draggingHue = true;
            updateHue(mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingSV) { updateSV(mouseX, mouseY); return true; }
        if (draggingHue) { updateHue(mouseY); return true; }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingSV = false;
        draggingHue = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isInsideSV(double mouseX, double mouseY) {
        return mouseX >= svX && mouseX <= svX + BOX_SIZE && mouseY >= svY && mouseY <= svY + BOX_SIZE;
    }

    private boolean isInsideHue(double mouseX, double mouseY) {
        return mouseX >= hueX && mouseX <= hueX + HUE_BAR_WIDTH && mouseY >= hueY && mouseY <= hueY + BOX_SIZE;
    }

    private void updateSV(double mouseX, double mouseY) {
        double clampedX = Math.max(svX, Math.min(mouseX, svX + BOX_SIZE));
        double clampedY = Math.max(svY, Math.min(mouseY, svY + BOX_SIZE));
        saturation = (float) ((clampedX - svX) / BOX_SIZE);
        value = 1f - (float) ((clampedY - svY) / BOX_SIZE);
    }

    private void updateHue(double mouseY) {
        double clampedY = Math.max(hueY, Math.min(mouseY, hueY + BOX_SIZE));
        hue = (float) ((clampedY - hueY) / BOX_SIZE) * 360f;
    }

    private float[] currentRgbFloat() {
        int[] rgb = currentRgbInt();
        return new float[]{rgb[0] / 255f, rgb[1] / 255f, rgb[2] / 255f};
    }

    private int[] currentRgbInt() {
        int packed = hsvToRgbPacked(hue, saturation, value);
        return new int[]{(packed >> 16) & 0xFF, (packed >> 8) & 0xFF, packed & 0xFF};
    }

    public static int hsvToRgbPacked(float h, float s, float v) {
        float r, g, b;
        h = h % 360f;
        if (h < 0) h += 360f;

        float c = v * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = v - c;

        if (h < 60) { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }

        int ri = (int) ((r + m) * 255);
        int gi = (int) ((g + m) * 255);
        int bi = (int) ((b + m) * 255);

        return 0xFF000000 | (ri << 16) | (gi << 8) | bi;
    }

    public static float[] rgbToHsv(float r, float g, float b) {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h;
        if (delta == 0) h = 0;
        else if (max == r) h = 60 * (((g - b) / delta) % 6);
        else if (max == g) h = 60 * (((b - r) / delta) + 2);
        else h = 60 * (((r - g) / delta) + 4);
        if (h < 0) h += 360;

        float s = (max == 0) ? 0 : delta / max;
        float v = max;

        return new float[]{h, s, v};
    }
}
