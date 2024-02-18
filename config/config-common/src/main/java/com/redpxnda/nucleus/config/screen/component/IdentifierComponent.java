package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IdentifierComponent extends TextFieldWidget implements ConfigComponent<Identifier> {
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.identifier.description");

    public ConfigComponent<?> parent;
    public final TextRenderer textRenderer;
    public boolean isValid = true;

    public IdentifierComponent(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        setMaxLength(200);
    }

    @Override
    public void onRemoved() {
        if (parent != null && !isValid) parent.validateChild(this);
    }

    public void updateValidity() {
        if (parent != null) {
            if (getText().isEmpty()) {
                if (isValid) {
                    parent.invalidateChild(this);
                    isValid = false;
                }
            } else {
                if (!isValid) {
                    parent.validateChild(this);
                    isValid = true;
                }
            }
        }
    }

    @Override
    public Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public boolean checkValidity() {
        return getValue() != null;
    }

    @Override
    public Identifier getValue() {
        return Identifier.tryParse(getText());
    }
    public void setValue(Identifier value) {
        setText(value.toString());
        updateValidity();
    }

    @Override
    public void write(String text) {
        String old = getText();
        super.write(text);
        if (!Identifier.isValid(getText()))
            setText(old);
        updateValidity();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) updateValidity();
        return result;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.parent = widget;
        updateValidity();
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            setSelectionStart(0);
            setSelectionEnd(0);
        }
    }
}
