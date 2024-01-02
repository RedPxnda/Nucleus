package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.Comment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

@Environment(EnvType.CLIENT)
public class ConfigEntriesComponent<T> extends ScrollableWidget implements Drawable, Element, ConfigComponent<T> {
    public static final int GRADIENT_TINT_START = new Color(0, 0, 0, 200).argb();
    public static final int GRADIENT_TINT_END = new Color(0, 0, 0, 100).argb();

    public final Map<String, Pair<Field, ConfigComponent<?>>> components;
    public final List<ConfigComponent<?>> invalids = new ArrayList<>();
    protected int contentHeight;
    protected final TextRenderer textRenderer;
    protected ConfigComponent<?> focusedComponent = null;
    protected @Nullable ConfigComponent<?> parent;
    protected T value;

    public ConfigEntriesComponent(Map<String, Pair<Field, ConfigComponent<?>>> components, TextRenderer textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.components = components;
        this.textRenderer = textRenderer;

        components.forEach((k, c) -> c.getRight().setParent(this));
    }

    public void performPositionUpdate() {
        contentHeight = getY() + 20;
        components.forEach((k, c) -> {
            var comp = c.getRight();
            if (comp.getInlineMode() == InlineMode.INLINE) comp.setX(getX() + 160);
            else comp.setX(getX() + 38);
            comp.setY(contentHeight - (int) getScrollY());
            comp.performPositionUpdate();
            contentHeight += comp.getHeight() + 8;
        });
    }

    @Override
    public void requestPositionUpdate() {
        if (parent != null) parent.requestPositionUpdate();
        else performPositionUpdate();
    }

    @Override
    public boolean overflows() {
        return super.overflows();
    }

    @Override
    public void invalidateChild(ConfigComponent<?> child) {
        invalids.add(child);
    }

    @Override
    public void validateChild(ConfigComponent<?> child) {
        invalids.remove(child);
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
        super.setScrollY(scrollY);
        performPositionUpdate();
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().translate(0, getScrollY(), 0);
        for (var entry : components.entrySet()) {
            String key = entry.getKey();
            ConfigComponent<?> component = entry.getValue().getRight();
            Field field = entry.getValue().getLeft();

            int y = component.getY();
            if (y > getY()+height+getScrollY()) continue;

            int textWidth = textRenderer.getWidth(key);
            int textOffset = 0;

            int invalidWidth = textRenderer.getWidth("(!) ");
            if (invalids.contains(component)) {
                textOffset += invalidWidth;
                context.drawText(textRenderer, "(!) ", getX() + 20, y + 6, -43691, true);
                if (mouseX >= getX()+20 && mouseX <= getX()+20+invalidWidth && mouseY >= y+6 && mouseY <= y+6+textRenderer.fontHeight)
                    context.drawTooltip(textRenderer, textRenderer.wrapLines(Text.translatable("nucleus.config_screen.invalid_entry"), 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
            }

            Comment comment = field.getAnnotation(Comment.class);
            int commentWidth = textRenderer.getWidth("(?) ");
            if (comment != null) {
                context.drawText(textRenderer, "(?) ", getX() + 20 + textOffset, y + 6, -11184811, true);
                if (mouseX >= getX()+20+textOffset && mouseX <= getX()+20+textOffset+commentWidth+textWidth && mouseY >= y+6 && mouseY <= y+6+textRenderer.fontHeight)
                    context.drawTooltip(textRenderer, textRenderer.wrapLines(Text.literal(comment.value()), 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
                textOffset += commentWidth;
            }

            context.drawText(textRenderer, key, getX() + 20 + textOffset, y + 6, Color.WHITE.argb(), true);
            if (component.getInlineMode() == InlineMode.DRAW_LINE)
                context.drawVerticalLine(getX()+30, y+24, y+component.getHeight(), Color.WHITE.argb());
            component.render(context, mouseX, mouseY, delta);
            component.drawInstructionText(context, mouseX, mouseY);
        }
    }

    @Override
    protected void drawBox(DrawContext context, int x, int y, int width, int height) {
        if (parent == null) {
            context.fillGradient(x, y, x + width, y + 5, GRADIENT_TINT_START, GRADIENT_TINT_END);
            context.fill(x, y + 5, x + width, y + height - 5, GRADIENT_TINT_END);
            context.fillGradient(x, y + height - 5, x + width, y + height, GRADIENT_TINT_END, GRADIENT_TINT_START);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        // eh, who uses narrator anyways
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
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
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (focusedComponent != null && focusedComponent.mouseReleased(mouseX, mouseY, button))
                return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isMouseOver(mouseX, mouseY)) {
            if (focusedComponent != null && focusedComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseOver(mouseX, mouseY)) {
            if (focusedComponent != null && focusedComponent.mouseScrolled(mouseX, mouseY, amount))
                return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null && focusedComponent.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null && focusedComponent.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (focusedComponent != null && focusedComponent.charTyped(chr, modifiers)) return true;
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
                    Nucleus.LOGGER.error("Exception whilst setting config's value from a ConfigComponent! (Incorrect type?)", e);
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
                Nucleus.LOGGER.error("Exception whilst setting ConfigComponent's value! (Incorrect type?)", e);
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
