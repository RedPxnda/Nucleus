package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.config.screen.widget.EmptyButtonWidget;
import com.redpxnda.nucleus.config.screen.widget.SelectableOptionsWidget;
import com.redpxnda.nucleus.util.Color;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryComponent<T> extends AbstractWidget implements ConfigComponent<T> {
    public static final Component OPEN_TEXT = Component.literal("∨");
    public static final Component CLOSED_TEXT = Component.literal(">");
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.registry.description");

    public ConfigComponent<?> parent;
    public boolean isValid = true;
    public final IdentifierComponent idComp;
    public final Registry<T> registry;
    public boolean suggestionsOpen = false;
    public final EmptyButtonWidget suggestionsOpener;
    public final SelectableOptionsWidget<T> suggestions;

    public RegistryComponent(Registry<T> reg, Font textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        idComp = new IdentifierComponent(textRenderer, x, y, width, height);
        idComp.setResponder(s -> {
            updateValidity();
            updateSuggestions();
        });
        registry = reg;
        suggestionsOpener = new EmptyButtonWidget(x + width - 20, y, 20, 20, CLOSED_TEXT, o -> {
            toggleSuggestions();
            if (suggestionsOpen) updateSuggestions();
            o.setMessage(suggestionsOpen ? OPEN_TEXT : CLOSED_TEXT);
        }, Color.WHITE.argb(), Color.TEXT_GRAY.argb());
        suggestions = new SelectableOptionsWidget<>(textRenderer, Map.of(), (s, t) -> {
            setConfigValue(t);
            suggestionsOpener.onPress();
        }, x, y + 24, width, 54);
    }

    @Override
    public void onRemoved() {
        if (parent != null && !isValid) parent.validateChild(this);
    }

    public void updateValidity() {
        if (parent != null) {
            if (!checkValidity()) {
                if (isValid) {
                    parent.invalidateChild(this);
                    isValid = false;
                    idComp.setTextColor(0xFF5555);
                }
            } else {
                if (!isValid) {
                    parent.validateChild(this);
                    isValid = true;
                    idComp.setTextColor(0xE0E0E0);
                }
            }
        }
    }

    public void updateSuggestions() {
        Map<String, T> options = new HashMap<>();

        String text = idComp.getValue();
        boolean separated = text.contains(":");

        if (!text.isEmpty()) {
            for (Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
                ResourceLocation key = entry.getKey().location();
                String strKey = key.toString();
                T val = entry.getValue();

                if (separated) {
                    String[] parts = strKey.split(":", 2);
                    if (key.getPath().contains(parts[1])) {
                        options.put(strKey, val);
                    }
                    if (key.getPath().contains(parts[0])) {
                        options.put(strKey, val);
                    }
                } else {
                    if (key.getPath().contains(text)) {
                        options.put(strKey, val);
                    }
                    if (key.getNamespace().contains(text)) {
                        options.put(strKey, val);
                    }
                }
                //if (options.size() >= 5) break;
            }
        }

        suggestions.setScrollAmount(0);
        suggestions.options = options;
    }

    public void toggleSuggestions() {
        suggestionsOpen = !suggestionsOpen;
        if (suggestionsOpen)
            height += suggestions.getHeight();
        else
            height -= suggestions.getHeight();
        requestPositionUpdate();
    }

    @Override
    public Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public void drawInstructionText(GuiGraphics context, int mouseX, int mouseY) {
        if (!suggestionsOpen) ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public boolean checkValidity() {
        return getConfigValue() != null;
    }

    @Override
    public T getConfigValue() {
        ResourceLocation val = idComp.getConfigValue();
        if (val == null) return null;
        return registry.getOptional(val).orElse(null);
    }

    @Override
    public void setConfigValue(T value) {
        ResourceLocation id = registry.getKey(value);
        if (id != null) {
            idComp.setConfigValue(id);
        }
        updateValidity();
    }

    @Override
    public void performPositionUpdate() {
        idComp.setX(getX());
        idComp.setY(getY());
        suggestions.setX(getX());
        suggestions.setY(getY() + 24);
        suggestionsOpener.setX(getX() + width - 20);
        suggestionsOpener.setY(getY());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (suggestionsOpener.isMouseOver(mouseX, mouseY) && suggestionsOpener.mouseClicked(mouseX, mouseY, button))
            return true;
        if (suggestionsOpen && suggestions.isMouseOver(mouseX, mouseY) && suggestions.mouseClicked(mouseX, mouseY, button))
            return true;
        return idComp.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return idComp.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return idComp.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double xAmount) {
        if (suggestionsOpen && suggestions.isMouseOver(mouseX, mouseY) && suggestions.mouseScrolled(mouseX, mouseY, amount, xAmount))
            return true;
        return super.mouseScrolled(mouseX, mouseY, amount, xAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return idComp.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return idComp.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return idComp.charTyped(chr, modifiers);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
        //idComp.setParent(this);
        updateValidity();
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        suggestionsOpener.render(context, mouseX, mouseY, delta);
        idComp.render(context, mouseX, mouseY, delta);
        if (suggestionsOpen) suggestions.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    public void setFocused(boolean focused) {
        idComp.setFocused(focused);
        if (!focused) {
            idComp.setCursorPosition(0);
            idComp.setHighlightPos(0);
        }
    }
}
