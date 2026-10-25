package com.redpxnda.nucleus.widgets.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class SingleLineTextWidget extends NucleusWidget {
    private Component text = Component.empty();
    private float timer;
    private int scrollPosition;
    private Orientation orientation = Orientation.LEFT;

    public int textColor = -1;
    public float scrollDelay = 0.5f;
    public float scrollHoldTime = 2.0f;
    public float firstLetterExtraTime = 1.0f;
    public boolean hasTextShadow = true;

    public SingleLineTextWidget(int x, int y, int width, Component text) {
        super(x, y, width, 9, Component.empty());
        setText(text);
    }

    public void setText(Component text) {
        this.text = text == null ? Component.empty() : text;
        scrollPosition = 0;
        timer = -firstLetterExtraTime;
    }

    public Component getText() {
        return text == null ? Component.literal("Missing Text!") : text;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        var font = Minecraft.getInstance().font;
        int textWidth = font.width(getText());
        int textStart = getX();

        switch (orientation) {
            case CENTERED -> textStart += (getWidth() - textWidth) / 2;
            case RIGHT -> textStart += getWidth() - textWidth;
        }

        boolean scissor = textWidth > getWidth();

        if (scissor) {
            String string = getText().getString();
            int offset = 0;
            boolean hold = false;

            if (scrollPosition < string.length()) {
                offset = -font.width(string.substring(0, scrollPosition));
                String remaining = string.substring(
                        Math.min(string.length() - 1, scrollPosition + 1));

                hold = font.width(remaining) <= getWidth();
            }

            timer += delta / 20;

            if (timer > scrollDelay && !hold) {
                scrollPosition++;
                timer = 0;
            }

            if (timer > scrollHoldTime && hold) {
                scrollPosition = 0;
                timer = -firstLetterExtraTime;
            }

            textStart += offset;
            graphics.enableScissor(getX(), getY(),
                    getX() + getWidth(), getY() + getHeight());
        }

        graphics.drawString(font, getText(), textStart, getY(),
                textColor, hasTextShadow);

        if (scissor) graphics.disableScissor();

        super.renderWidget(graphics, mouseX, mouseY, delta);
    }

    /**
     * returns the width of the full text.
     * if less it starts scrolling
     */
    public int getDesiredWidth() {
        return Math.min(getWidth(),
                Minecraft.getInstance().font.width(getText()));
    }

    public enum Orientation {
        LEFT, CENTERED, RIGHT
    }
}