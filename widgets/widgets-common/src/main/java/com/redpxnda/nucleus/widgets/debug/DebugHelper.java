package com.redpxnda.nucleus.widgets.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class DebugHelper {

    private DebugHelper() {
    }

    public static void renderDebug(
            GuiGraphics graphics,
            int width,
            int height
    ) {
        PoseStack pose = graphics.pose();

        renderGrid(graphics, width, height);
        renderAxes(graphics, width, height);
        renderDimensions(graphics, width, height);
    }

    private static void renderGrid(
            GuiGraphics graphics,
            int width,
            int height
    ) {
        int spacing = calculateGridSpacing(width, height);

        int centerX = width / 2;
        int centerY = height / 2;

        // Vertical lines
        for (int x = centerX; x <= width; x += spacing) {
            line(graphics, x, 0, x, height);
        }

        for (int x = centerX - spacing; x >= 0; x -= spacing) {
            line(graphics, x, 0, x, height);
        }

        // Horizontal lines
        for (int y = centerY; y <= height; y += spacing) {
            line(graphics, 0, y, width, y);
        }

        for (int y = centerY - spacing; y >= 0; y -= spacing) {
            line(graphics, 0, y, width, y);
        }

        // Pixel spacing label
        drawText(
                graphics,
                spacing + " px",
                centerX + 4,
                centerY + 4
        );
    }

    private static int calculateGridSpacing(int width, int height) {
        int largest = Math.max(width, height);

        // Aim for roughly 7 grid intervals.
        int raw = Math.max(1, largest / 7);

        // Snap to sensible values.
        int[] values = {
                4, 5, 8, 10, 16, 20,
                25, 32, 40, 50, 64, 80, 100,
                128, 160, 200
        };

        for (int value : values) {
            if (value >= raw) {
                return value;
            }
        }

        return raw;
    }

    private static void renderAxes(
            GuiGraphics graphics,
            int width,
            int height
    ) {
        int cx = width / 2;
        int cy = height / 2;

        // Center origin.
        renderAxis(graphics, cx, cy, width, height);

        // Top-left origin.
        renderAxis(graphics, 0, 0, width, height);
    }

    private static void renderAxis(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height
    ) {
        // X - red
        graphics.fill(
                x,
                y - 1,
                width,
                y + 1,
                Color.RED.argb()
        );

        // Y - green
        graphics.fill(
                x - 1,
                y,
                x + 1,
                height,
                Color.GREEN.argb()
        );

        // Z - blue marker.
        int size = 8;

        graphics.fill(
                x - size,
                y - size,
                x + size,
                y + size,
                Color.BLUE.argb()
        );

        //drawText(graphics, "X", width - 12, y - 12);
        //drawText(graphics, "Y", x + 4, height - 12);
        //drawText(graphics, "Z", x + 5, y + 5);
    }

    private static void renderDimensions(
            GuiGraphics graphics,
            int width,
            int height
    ) {
        drawText(
                graphics,
                width + " px",
                width - 45,
                4
        );

        drawText(
                graphics,
                height + " px",
                4,
                height - 12
        );
    }

    private static void line(
            GuiGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2
    ) {
        graphics.fill(
                x1,
                y1,
                x2 + 1,
                y2 + 1,
                Color.WHITE.withAlpha(0.25f).argb()
        );
    }

    private static void drawText(
            GuiGraphics graphics,
            String text,
            int x,
            int y
    ) {
        Font font = Minecraft.getInstance().font;

        graphics.drawString(
                font,
                Component.literal(text),
                x,
                y,
                Color.WHITE.argb(),
                true
        );
    }

    public static void renderSlot(
            GuiGraphics graphics,
            float outerX,
            float outerY,
            float innerX,
            float innerY,
            float depth,
            float rimDepth
    ) {
        PoseStack pose = graphics.pose();
        int rimColor = Color.TEXT_DARK_GRAY.abgr();
        int insideColor = Color.GRAY.abgr();

        // Recessed center.
        CubeRenderHelper.render(
                pose,
                innerX, innerY,
                +depth + rimDepth,
                -innerX, -innerY,
                depth,
                insideColor);

        // Top bezel.
        CubeRenderHelper.render(
                pose,
                -outerX, -outerY,
                -depth,
                outerX, -innerY,
                depth,
                rimColor
        );

        // Bottom bezel.
        CubeRenderHelper.render(
                pose,
                -outerX, innerY,
                -depth,
                outerX, outerY,
                depth,
                rimColor
        );

        // Left bezel.
        CubeRenderHelper.render(
                pose,
                -outerX, -innerY,
                -depth,
                -innerX, innerY,
                depth,
                rimColor
        );

        // Right bezel.
        CubeRenderHelper.render(
                pose,
                innerX,
                -innerY,
                -depth,
                outerX,
                innerY,
                depth,
                rimColor
        );
    }
}