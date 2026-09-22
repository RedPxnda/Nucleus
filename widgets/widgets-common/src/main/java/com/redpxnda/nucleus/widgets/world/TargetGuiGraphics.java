package com.redpxnda.nucleus.widgets.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.redpxnda.nucleus.widgets.mixin.widgets.GuiGraphicsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.ArrayDeque;

/**
 * {@link GuiGraphics} implementations with custom scissor logic.
 * to allow {@link WidgetRenderTarget} to have standard working scissor logic.
 */
public class TargetGuiGraphics extends GuiGraphics {
    private final int targetWidth;
    private final int targetHeight;
    private final int scale;

    private final ArrayDeque<Scissor> scissors = new ArrayDeque<>();

    private record Scissor(int left, int top, int right, int bottom) {}

    public TargetGuiGraphics(
            Minecraft minecraft,
            MultiBufferSource.BufferSource bufferSource,
            int targetWidth,
            int targetHeight,
            int scale
    ) {
        super(minecraft, bufferSource);
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.scale = scale;
    }

    @Override
    public void enableScissor(int minX, int minY, int maxX, int maxY) {
        Scissor next = new Scissor(minX, minY, maxX, maxY);
        Scissor previous = scissors.peekLast();
        if (previous != null) {
            next = new Scissor(
                    Math.max(previous.left, next.left),
                    Math.max(previous.top, next.top),
                    Math.min(previous.right, next.right),
                    Math.min(previous.bottom, next.bottom)
            );
        }

        scissors.addLast(next);
        applyScissor(next);
    }

    @Override
    public void disableScissor() {
        if (scissors.isEmpty()) {
            RenderSystem.disableScissor();
            return;
        }

        scissors.removeLast();

        Scissor current = scissors.peekLast();

        if (current == null) {
            RenderSystem.disableScissor();
        } else {
            applyScissor(current);
        }
    }

    @Override
    public boolean containsPointInScissor(int x, int y) {
        Scissor scissor = scissors.peekLast();
        return scissor == null
               || (
                       x >= scissor.left
                       && y >= scissor.top
                       && x < scissor.right
                       && y < scissor.bottom
               );
    }

    private void applyScissor(Scissor scissor) {
        ((GuiGraphicsAccessor) this).nucleusFlushIfManaged();
        int x = scissor.left * scale;
        int y = targetHeight - scissor.bottom * scale;
        int width = Math.max(0, (scissor.right - scissor.left) * scale);
        int height = Math.max(0, (scissor.bottom - scissor.top) * scale);
        RenderSystem.enableScissor(
                x,
                y,
                width,
                height
        );
    }
}