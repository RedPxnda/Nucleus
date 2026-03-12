package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.screen.AbstractConfigScreen;
import com.redpxnda.nucleus.config.screen.ConfigScreen;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.Comment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

@Environment(EnvType.CLIENT)
public class ConfigEntriesComponent<T> extends AbstractScrollWidget implements Renderable, GuiEventListener, ConfigComponent<T> {
    private static final Logger LOGGER = Nucleus.getLogger();
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.entries.description");
    public static final int KEY_TEXT_WIDTH = 175;
    public static final int CHILD_OFFSET = 12;
    public static final int GRADIENT_TINT_START = new Color(0, 0, 0, 200).argb();
    public static final int GRADIENT_TINT_END = new Color(0, 0, 0, 100).argb();

    public final Map<String, Tuple<Field, ConfigComponent<?>>> components;
    public final List<ConfigComponent<?>> invalids = new ArrayList<>();
    protected int contentHeight;
    protected final Font textRenderer;
    protected ConfigComponent<?> focusedComponent = null;
    protected @Nullable ConfigComponent<?> parent;
    protected T value;
    public boolean minimized = true;
    public final Button minimizer;
    public boolean renderInstructions = true;

    public ConfigEntriesComponent(Map<String, Tuple<Field, ConfigComponent<?>>> components, Font textRenderer, int x, int y, int width, int height) { // todo horiz scroll
        super(x, y, width, height, Component.empty());
        this.components = components;
        this.textRenderer = textRenderer;

        components.forEach((k, c) -> c.getB().setParent(this));

        Component minimizedText = Component.literal(">");
        Component maximizedText = Component.literal("∨");
        minimizer = Button.builder(minimizedText, wid -> {
            if (Screen.hasControlDown()) {
                Screen oldScreen = Minecraft.getInstance().screen;
                if (oldScreen instanceof AbstractConfigScreen<?> cs) cs.skipNextInit = true;
                ConfigScreen<T> newScreen = new ConfigScreen<>(oldScreen, this);
                Minecraft.getInstance().setScreen(newScreen);
            } else {
                minimized = !minimized;
                if (minimized) focusedComponent = null;
                wid.setMessage(minimized ? minimizedText : maximizedText);
                requestPositionUpdate();
            }
        }).bounds(0, 0, 20, 20).build();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void onRemoved() {
        if (!minimized)
            minimizer.onPress();
    }

    @Override
    public boolean scrollbarVisible() {
        return parent == null && super.scrollbarVisible();
    }

    @Override
    public InlineMode getInlineMode() {
        return isMinimized() ? InlineMode.NONE : InlineMode.DRAW_LINE;
    }

    public void performPositionUpdate() {
        minimizer.setPosition(getX() + KEY_TEXT_WIDTH - 38, getY());
        if (parent != null) width = KEY_TEXT_WIDTH - 18;
        contentHeight = getY() + 20;
        if (!isMinimized()) {
            components.forEach((k, c) -> {
                var comp = c.getB();
                if (comp.getInlineMode() == InlineMode.INLINE) comp.setX(getX() + KEY_TEXT_WIDTH);
                else comp.setX(getX() + CHILD_OFFSET);
                comp.setY(contentHeight - (int) scrollAmount());
                comp.performPositionUpdate();
                int newWidth;
                if (parent != null && (newWidth = comp.getX() + comp.getWidth()) > width) width = newWidth;
                contentHeight += comp.getHeight() + 8;
            });
            if (!components.isEmpty()) contentHeight -= 8; // last element should not increase height
        }
        if (parent != null)
            height = contentHeight - getY();
        if (scrollAmount() > getMaxScrollAmount()) setScrollAmount(getMaxScrollAmount());
    }

    @Override
    public void requestPositionUpdate() {
        if (parent != null) parent.requestPositionUpdate();
        else performPositionUpdate();
    }

    @Override
    public void invalidateChild(ConfigComponent<?> child) {
        invalids.add(child);
        if (parent != null) ConfigComponent.super.invalidateChild(child);
    }

    @Override
    public void validateChild(ConfigComponent<?> child) {
        invalids.remove(child);
        if (parent != null) ConfigComponent.super.validateChild(child);
    }

    @Override
    public void drawInstructionText(GuiGraphics context, int mouseX, int mouseY) {
        if (minimizer != null && minimizer.isMouseOver(mouseX, mouseY))
            ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public @Nullable Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    protected int getInnerHeight() {
        return contentHeight;
    }

    @Override
    protected double scrollRate() {
        return Screen.hasShiftDown() ? 8 : 16;
    }

    @Override
    protected void setScrollAmount(double scrollY) {
        if (parent == null) {
            super.setScrollAmount(scrollY);
            performPositionUpdate();
        } else super.setScrollAmount(0);
    }

    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.pose().translate(0, scrollAmount(), 0);
        if (!isMinimized()) {
            for (var entry : components.entrySet()) {
                String key = entry.getKey();
                ConfigComponent<?> component = entry.getValue().getB();
                Field field = entry.getValue().getA();

                int y = component.getY();
                if (y > getY() + height + scrollAmount()) continue;

                int textWidth = textRenderer.width(key);
                int textOffset = 0;

                int invalidWidth = textRenderer.width("(!) ");
                if (invalids.contains(component)) {
                    textOffset += invalidWidth;
                    context.drawString(textRenderer, "(!) ", getX() + CHILD_OFFSET - 4, y + 6, -43691, true);
                    if (mouseX >= getX() + CHILD_OFFSET - 4 && mouseX <= getX() + CHILD_OFFSET - 4 + invalidWidth && mouseY >= y + 6 && mouseY <= y + 6 + textRenderer.lineHeight)
                        context.renderTooltip(textRenderer, textRenderer.split(Component.translatable("nucleus.config_screen.invalid_entry"), 150), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
                }

                Comment comment = field.getAnnotation(Comment.class);
                int commentWidth = textRenderer.width("(?) ");
                if (comment != null) {
                    context.drawString(textRenderer, "(?) ", getX() + CHILD_OFFSET - 4 + textOffset, y + 6, -11184811, true);
                    if (mouseX >= getX() + CHILD_OFFSET - 4 + textOffset && mouseX <= getX() + CHILD_OFFSET - 4 + textOffset + commentWidth + textWidth && mouseY >= y + 6 && mouseY <= y + 6 + textRenderer.lineHeight)
                        context.renderTooltip(textRenderer, textRenderer.split(Component.literal(comment.value()), 150), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
                    textOffset += commentWidth;
                }

                context.drawString(textRenderer, key, getX() + CHILD_OFFSET - 4 + textOffset, y + 6, Color.WHITE.argb(), true); // todo key translations
                if (component.getInlineMode() == InlineMode.DRAW_LINE)
                    context.vLine(getX() + CHILD_OFFSET, y + 24, y + component.getHeight(), Color.WHITE.argb());
                component.render(context, mouseX, mouseY, delta);
                if (parent instanceof ConfigEntriesComponent<?> c) renderInstructions = c.renderInstructions;
                if (renderInstructions) component.drawInstructionText(context, mouseX, mouseY);
            }
        }
    }

    public int getContentHeight() {
        return this.getInnerHeight() + 4;
    }

    public int getScrollBarHeight() {
        return Mth.clamp((height * height) / getContentHeight(), 32, height);
    }

    public void renderScrollBar(GuiGraphics context) {
        int thumbHeight = getScrollBarHeight();
        int left = getX() + width;
        int right = getX() + width + 6;
        int top = Math.max(getY(), (int) scrollAmount() * (height - thumbHeight) / getMaxScrollAmount() + getY());
        int bottom = top + thumbHeight;
        context.fill(left, top, right, bottom, -8355712);
        context.fill(left, top, right - 1, bottom - 1, -4144960);
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (parent == null) {
            super.renderWidget(context, mouseX, mouseY, delta);
        } else {
            renderContents(context, mouseX, mouseY, delta);
            minimizer.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void renderDecorations(GuiGraphics context) {
        if (scrollbarVisible()) {
            renderScrollBar(context);
        }
    }

    @Override
    protected void renderBorder(GuiGraphics context, int x, int y, int width, int height) {
        context.fillGradient(x, y, x + width, y + 5, GRADIENT_TINT_START, GRADIENT_TINT_END);
        context.fill(x, y + 5, x + width, y + height - 5, GRADIENT_TINT_END);
        context.fillGradient(x, y + height - 5, x + width, y + height, GRADIENT_TINT_END, GRADIENT_TINT_START);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        // eh, who uses narrator anyways
    }

    public boolean isMinimized() {
        return parent != null && minimized;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (minimizer.isMouseOver(mouseX, mouseY)) return minimizer.mouseClicked(mouseX, mouseY, button);
        if (!isMinimized()) {
            for (var c : components.values()) {
                ConfigComponent<?> component = c.getB();
                if (focusedComponent != null && focusedComponent.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                } else if (component.isMouseOver(mouseX, mouseY)) {
                    if (button == 0) {
                        if (focusedComponent != null) focusedComponent.setFocused(false);
                        component.setFocused(true);
                        focusedComponent = component;
                    }
                    component.mouseClicked(mouseX, mouseY, button);
                    return true;
                }
            }
        }
        return parent == null && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.mouseReleased(mouseX, mouseY, button))
            return true;
        return parent == null && super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            return true;
        return parent == null && super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double xAmount) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.mouseScrolled(mouseX, mouseY, amount, xAmount))
            return true;
        return parent == null && super.mouseScrolled(mouseX, mouseY, amount, xAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.keyPressed(keyCode, scanCode, modifiers))
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.keyReleased(keyCode, scanCode, modifiers))
            return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // dont send to children
    }

    @Override
    public boolean checkValidity() {
        if (!invalids.isEmpty()) return false;
        for (Tuple<Field, ConfigComponent<?>> pair : components.values()) {
            if (!pair.getB().checkValidity()) return false;
        }
        return true;
    }

    @Override
    public T getConfigValue() {
        if (value != null)
            components.forEach((key, pair) -> {
                try {
                    ConfigComponent comp = pair.getB();
                    pair.getA().set(value, comp.getConfigValue());
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    LOGGER.error("Exception whilst setting config's value from a ConfigComponent! (Incorrect type?)", e);
                }
            });
        return value;
    }

    @Override
    public void setConfigValue(T val) {
        components.forEach((key, pair) -> {
            try {
                ConfigComponent comp = pair.getB();
                comp.setConfigValue(pair.getA().get(val));
            } catch (IllegalAccessException | IllegalArgumentException e) {
                LOGGER.error("Exception whilst setting ConfigComponent's value! (Incorrect type?)", e);
            }
        });
        value = val;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }
}
