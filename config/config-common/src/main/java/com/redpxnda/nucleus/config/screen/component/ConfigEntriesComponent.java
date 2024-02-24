package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.screen.ConfigScreen;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.Comment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

@Environment(EnvType.CLIENT)
public class ConfigEntriesComponent<T> extends ScrollableWidget implements Drawable, Element, ConfigComponent<T> {
    private static final Logger LOGGER = Nucleus.getLogger();
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.entries.description");
    public static final int KEY_TEXT_WIDTH = 200;
    public static final int GRADIENT_TINT_START = new Color(0, 0, 0, 200).argb();
    public static final int GRADIENT_TINT_END = new Color(0, 0, 0, 100).argb();

    public final Map<String, Pair<Field, ConfigComponent<?>>> components;
    public final List<ConfigComponent<?>> invalids = new ArrayList<>();
    protected int contentHeight;
    protected final TextRenderer textRenderer;
    protected ConfigComponent<?> focusedComponent = null;
    protected @Nullable ConfigComponent<?> parent;
    protected T value;
    public boolean minimized = true;
    public final ButtonWidget minimizer;
    public boolean renderInstructions = true;

    public ConfigEntriesComponent(Map<String, Pair<Field, ConfigComponent<?>>> components, TextRenderer textRenderer, int x, int y, int width, int height) { // todo horiz scroll
        super(x, y, width, height, Text.empty());
        this.components = components;
        this.textRenderer = textRenderer;

        components.forEach((k, c) -> c.getRight().setParent(this));

        Text minimizedText = Text.literal(">");
        Text maximizedText = Text.literal("∨");
        minimizer = ButtonWidget.builder(minimizedText, wid -> {
            if (Screen.hasControlDown()) {
                Screen oldScreen = MinecraftClient.getInstance().currentScreen;
                if (oldScreen instanceof ConfigScreen<?> cs) cs.skipNextInit = true;
                ConfigScreen<T> newScreen = new ConfigScreen<>(oldScreen, this);
                MinecraftClient.getInstance().setScreen(newScreen);
            } else {
                minimized = !minimized;
                if (minimized) focusedComponent = null;
                wid.setMessage(minimized ? minimizedText : maximizedText);
                requestPositionUpdate();
            }
        }).dimensions(0, 0, 20, 20).build();
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
    public boolean overflows() {
        return parent == null && super.overflows();
    }

    @Override
    public InlineMode getInlineMode() {
        return isMinimized() ? InlineMode.NONE : InlineMode.DRAW_LINE;
    }

    public void performPositionUpdate() {
        minimizer.setPosition(getX()+KEY_TEXT_WIDTH-38, getY());
        if (parent != null) width = KEY_TEXT_WIDTH-18;
        contentHeight = getY() + 20;
        if (!isMinimized()) {
            components.forEach((k, c) -> {
                var comp = c.getRight();
                if (comp.getInlineMode() == InlineMode.INLINE) comp.setX(getX() + KEY_TEXT_WIDTH);
                else comp.setX(getX() + 38);
                comp.setY(contentHeight - (int) getScrollY());
                comp.performPositionUpdate();
                int newWidth;
                if (parent != null && (newWidth = comp.getX() + comp.getWidth()) > width) width = newWidth;
                contentHeight += comp.getHeight() + 8;
            });
            if (!components.isEmpty()) contentHeight -= 8; // last element should not increase height
        }
        if (parent != null)
            height = contentHeight-getY();
        if (getScrollY() > getMaxScrollY()) setScrollY(getMaxScrollY());
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
    public void drawInstructionText(DrawContext context, int mouseX, int mouseY) {
        if (minimizer != null && minimizer.isMouseOver(mouseX, mouseY))
            ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public @Nullable Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    protected int getContentsHeight() {
        return contentHeight;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return Screen.hasShiftDown() ? 8 : 16;
    }

    @Override
    protected void setScrollY(double scrollY) {
        if (parent == null) {
            super.setScrollY(scrollY);
            performPositionUpdate();
        } else super.setScrollY(0);
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().translate(0, getScrollY(), 0);
        if (!isMinimized()) {
            for (var entry : components.entrySet()) {
                String key = entry.getKey();
                ConfigComponent<?> component = entry.getValue().getRight();
                Field field = entry.getValue().getLeft();

                int y = component.getY();
                if (y > getY() + height + getScrollY()) continue;

                int textWidth = textRenderer.getWidth(key);
                int textOffset = 0;

                int invalidWidth = textRenderer.getWidth("(!) ");
                if (invalids.contains(component)) {
                    textOffset += invalidWidth;
                    context.drawText(textRenderer, "(!) ", getX() + 20, y + 6, -43691, true);
                    if (mouseX >= getX() + 20 && mouseX <= getX() + 20 + invalidWidth && mouseY >= y + 6 && mouseY <= y + 6 + textRenderer.fontHeight)
                        context.drawTooltip(textRenderer, textRenderer.wrapLines(Text.translatable("nucleus.config_screen.invalid_entry"), 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
                }

                Comment comment = field.getAnnotation(Comment.class);
                int commentWidth = textRenderer.getWidth("(?) ");
                if (comment != null) {
                    context.drawText(textRenderer, "(?) ", getX() + 20 + textOffset, y + 6, -11184811, true);
                    if (mouseX >= getX() + 20 + textOffset && mouseX <= getX() + 20 + textOffset + commentWidth + textWidth && mouseY >= y + 6 && mouseY <= y + 6 + textRenderer.fontHeight)
                        context.drawTooltip(textRenderer, textRenderer.wrapLines(Text.literal(comment.value()), 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
                    textOffset += commentWidth;
                }

                context.drawText(textRenderer, key, getX() + 20 + textOffset, y + 6, Color.WHITE.argb(), true); // todo key translations
                if (component.getInlineMode() == InlineMode.DRAW_LINE)
                    context.drawVerticalLine(getX() + 30, y + 24, y + component.getHeight(), Color.WHITE.argb());
                component.render(context, mouseX, mouseY, delta);
                if (parent instanceof ConfigEntriesComponent<?> c) renderInstructions = c.renderInstructions;
                if (renderInstructions) component.drawInstructionText(context, mouseX, mouseY);
            }
        }
    }

    public int getContentsHeightWithPadding() {
        return this.getContentsHeight() + 4;
    }

    public int getScrollbarThumbHeight() {
        return MathHelper.clamp((height * height) / getContentsHeightWithPadding(), 32, height);
    }

    public void drawScrollbar(DrawContext context) {
        int thumbHeight = getScrollbarThumbHeight();
        int left = getX() + width;
        int right = getX() + width + 6;
        int top = Math.max(getY(), (int)getScrollY() * (height - thumbHeight) / getMaxScrollY() + getY());
        int bottom = top + thumbHeight;
        context.fill(left, top, right, bottom, -8355712);
        context.fill(left, top, right - 1, bottom - 1, -4144960);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (parent == null) {
            super.renderButton(context, mouseX, mouseY, delta);
        } else {
            renderContents(context, mouseX, mouseY, delta);
            minimizer.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void renderOverlay(DrawContext context) {
        if (overflows()) {
            drawScrollbar(context);
        }
    }

    @Override
    protected void drawBox(DrawContext context, int x, int y, int width, int height) {
        context.fillGradient(x, y, x + width, y + 5, GRADIENT_TINT_START, GRADIENT_TINT_END);
        context.fill(x, y + 5, x + width, y + height - 5, GRADIENT_TINT_END);
        context.fillGradient(x, y + height - 5, x + width, y + height, GRADIENT_TINT_END, GRADIENT_TINT_START);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        // eh, who uses narrator anyways
    }

    public boolean isMinimized() {
        return parent != null && minimized;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (minimizer.isMouseOver(mouseX, mouseY)) return minimizer.mouseClicked(mouseX, mouseY, button);
            if (!isMinimized()) {
                for (var c : components.values()) {
                    ConfigComponent<?> component = c.getRight();
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
        }
        return parent == null && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (!isMinimized() && focusedComponent != null && focusedComponent.mouseReleased(mouseX, mouseY, button))
                return true;
        }
        return parent == null && super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isMouseOver(mouseX, mouseY)) {
            if (!isMinimized() && focusedComponent != null && focusedComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                return true;
        }
        return parent == null && super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseOver(mouseX, mouseY)) {
            if (!isMinimized() && focusedComponent != null && focusedComponent.mouseScrolled(mouseX, mouseY, amount))
                return true;
        }
        return parent == null && super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!isMinimized() && focusedComponent != null && focusedComponent.keyReleased(keyCode, scanCode, modifiers)) return true;
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
        for (Pair<Field, ConfigComponent<?>> pair : components.values()) {
            if (!pair.getRight().checkValidity()) return false;
        }
        return true;
    }

    @Override
    public T getValue() {
        if (value != null)
            components.forEach((key, pair) -> {
                try {
                    ConfigComponent comp = pair.getRight();
                    pair.getLeft().set(value, comp.getValue());
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    LOGGER.error("Exception whilst setting config's value from a ConfigComponent! (Incorrect type?)", e);
                }
            });
        return value;
    }

    @Override
    public void setValue(T val) {
        components.forEach((key, pair) -> {
            try {
                ConfigComponent comp = pair.getRight();
                comp.setValue(pair.getLeft().get(val));
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
