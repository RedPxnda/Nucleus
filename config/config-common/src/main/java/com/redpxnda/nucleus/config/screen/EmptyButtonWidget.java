package com.redpxnda.nucleus.config.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EmptyButtonWidget extends ButtonWidget {
    public final int hoveredColor;
    public final int textColor;

    protected EmptyButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, int hoveredColor, int textColor) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.hoveredColor = hoveredColor;
        this.textColor = textColor;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.drawMessage(context, minecraftClient.textRenderer, isHovered() ? hoveredColor : textColor);
    }
}
