package com.redpxnda.nucleus.config.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.joml.Vector4i;

@Environment(EnvType.CLIENT)
public class TextFieldComponent extends TextFieldWidget implements ConfigComponent<String> {
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.text.description");

    public ConfigComponent<?> widget;

    public TextFieldComponent(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Text.empty());
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
    }

    @Override
    public Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public boolean checkValidity() {
        return true;
    }

    @Override
    public String getValue() {
        return getText();
    }
    public void setValue(String value) {
        setText(value);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.widget = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return widget;
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
