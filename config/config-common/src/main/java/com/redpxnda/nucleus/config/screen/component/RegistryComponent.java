package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.config.screen.widget.EmptyButtonWidget;
import com.redpxnda.nucleus.config.screen.widget.SelectableOptionsWidget;
import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RegistryComponent<T> extends ClickableWidget implements ConfigComponent<T> {
    public static final Text OPEN_TEXT = Text.literal("∨");
    public static final Text CLOSED_TEXT = Text.literal(">");
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.registry.description");

    public ConfigComponent<?> parent;
    public boolean isValid = true;
    public final IdentifierComponent idComp;
    public final Registry<T> registry;
    public boolean suggestionsOpen = false;
    public final EmptyButtonWidget suggestionsOpener;
    public final SelectableOptionsWidget<T> suggestions;

    public RegistryComponent(Registry<T> reg, TextRenderer textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        idComp = new IdentifierComponent(textRenderer, x, y, width, height);
        idComp.setChangedListener(s -> {
            updateValidity();
            updateSuggestions();
        });
        registry = reg;
        suggestionsOpener = new EmptyButtonWidget(x+width-20, y, 20, 20, CLOSED_TEXT, o -> {
            toggleSuggestions();
            if (suggestionsOpen) updateSuggestions();
            o.setMessage(suggestionsOpen ? OPEN_TEXT : CLOSED_TEXT);
        }, Color.WHITE.argb(), Color.TEXT_GRAY.argb());
        suggestions = new SelectableOptionsWidget<>(textRenderer, Map.of(), (s, t) -> {
            setValue(t);
            suggestionsOpener.onPress();
        }, x, y+24, width, 54);
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
                    idComp.setEditableColor(0xFF5555);
                }
            } else {
                if (!isValid) {
                    parent.validateChild(this);
                    isValid = true;
                    idComp.setEditableColor(0xE0E0E0);
                }
            }
        }
    }

    public void updateSuggestions() {
        Map<String, T> options = new HashMap<>();

        String text = idComp.getText();
        boolean separated = text.contains(":");

        if (!text.isEmpty()) {
            for (Map.Entry<RegistryKey<T>, T> entry : registry.getEntrySet()) {
                Identifier key = entry.getKey().getValue();
                String strKey = key.toString();
                T val = entry.getValue();

                if (separated) {
                    if (strKey.contains(text))
                        options.put(strKey, val);
                } else {
                    if (key.getPath().contains(text))
                        options.put(strKey, val);
                }
                //if (options.size() >= 5) break;
            }
        }

        suggestions.setScrollY(0);
        suggestions.options = options;
    }

    public void toggleSuggestions() {
        suggestionsOpen = !suggestionsOpen;
        if (suggestionsOpen)
            height+=suggestions.getHeight();
        else
            height-=suggestions.getHeight();
        requestPositionUpdate();
    }

    @Override
    public Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public void drawInstructionText(DrawContext context, int mouseX, int mouseY) {
        if (!suggestionsOpen) ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public boolean checkValidity() {
        return getValue() != null;
    }

    @Override
    public T getValue() {
        Identifier val = idComp.getValue();
        if (val == null) return null;
        return registry.getOrEmpty(val).orElse(null);
    }

    @Override
    public void setValue(T value) {
        Identifier id = registry.getId(value);
        if (id != null) {
            idComp.setValue(id);
        }
        updateValidity();
    }

    @Override
    public void performPositionUpdate() {
        idComp.setX(getX());
        idComp.setY(getY());
        suggestions.setX(getX());
        suggestions.setY(getY()+24);
        suggestionsOpener.setX(getX()+width-20);
        suggestionsOpener.setY(getY());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (suggestionsOpener.isMouseOver(mouseX, mouseY) && suggestionsOpener.mouseClicked(mouseX, mouseY, button)) return true;
        if (suggestionsOpen && suggestions.isMouseOver(mouseX, mouseY) && suggestions.mouseClicked(mouseX, mouseY, button)) return true;
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (suggestionsOpen && suggestions.isMouseOver(mouseX, mouseY) && suggestions.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
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
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        suggestionsOpener.render(context, mouseX, mouseY, delta);
        idComp.render(context, mouseX, mouseY, delta);
        if (suggestionsOpen) suggestions.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void setFocused(boolean focused) {
        idComp.setFocused(focused);
        if (!focused) {
            idComp.setSelectionStart(0);
            idComp.setSelectionEnd(0);
        }
    }
}
