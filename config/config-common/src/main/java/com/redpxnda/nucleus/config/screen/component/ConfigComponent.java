package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface ConfigComponent<T> extends Drawable, Element, Widget {
    default void drawInstructionText(DrawContext context, int mouseX, int mouseY) {
        Text text = getInstructionText();
        if (isMouseOver(mouseX, mouseY) && text != null)
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, MinecraftClient.getInstance().textRenderer.wrapLines(text, 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
    }

    default @Nullable Text getInstructionText() {
        return null;
    }

    boolean checkValidity();

    T getValue();

    void setValue(T value);

    default void onRemoved() {}

    void setParent(ConfigComponent<?> widget);

    ConfigComponent<?> getParent();

    default void invalidateChild(ConfigComponent<?> child) {
        ConfigComponent<?> parent = getParent();
        if (parent != null) {
            parent.invalidateChild(child);
            parent.invalidateChild(this);
        }
    }
    default void validateChild(ConfigComponent<?> child) {
        ConfigComponent<?> parent = getParent();
        if (parent != null) {
            parent.validateChild(child);
            parent.validateChild(this);
        }
    }

    default void performPositionUpdate() {}

    default void requestPositionUpdate() {
        ConfigComponent<?> parent = getParent();
        if (parent != null) parent.requestPositionUpdate();
    }

    boolean isHovered();

    default InlineMode getInlineMode() {
        return InlineMode.INLINE;
    }

    enum InlineMode {
        NONE,
        DRAW_LINE,
        INLINE
    }

    @Override
    default ScreenRect getNavigationFocus() {
        return Element.super.getNavigationFocus();
    }
}
