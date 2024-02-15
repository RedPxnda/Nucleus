package com.redpxnda.nucleus.config.screen.widget;

import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class IntegerFieldWidget extends TextFieldWidget {
    public @Nullable String prefix;
    public int value;
    public @Nullable Integer maxValue = null;
    public @Nullable Integer minValue = null;
    protected final Consumer<Integer> onValueUpdate;
    protected final TextRenderer textRenderer;

    public IntegerFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text message, Consumer<Integer> onValueUpdate) {
        super(textRenderer, x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.onValueUpdate = onValueUpdate;
        setText(String.valueOf(value));
        setDrawsBackground(false);
    }
    public IntegerFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, int minValue, int maxValue, Text message, Consumer<Integer> onValueUpdate, String prefix) {
        this(textRenderer, x, y, width, height, message, onValueUpdate);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.prefix = prefix;
    }

    public void setValue(int v) {
        if (maxValue != null && v > maxValue) v = maxValue;
        else if (minValue != null && v < minValue) v = minValue;
        value = v;
        setText(String.valueOf(v));
    }

    public void tryUpdateValue() {
        try {
            value = Integer.parseInt(getText());
        } catch (Exception e) {
            value = 0;
        }

        if (maxValue != null && value > maxValue) {
            value = maxValue;
            setText(String.valueOf(value));
        } else if (minValue != null && value < minValue) {
            value = minValue;
            setText(String.valueOf(value));
        }

        onValueUpdate.accept(value);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (prefix != null)
            context.drawText(textRenderer, prefix, getX()-textRenderer.getWidth(prefix)-4, getY()-1, Color.WHITE.argb(), true);
        super.renderButton(context, mouseX, mouseY, delta);
    }

    @Override
    public void write(String text) {
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (!(chr >= '0' && chr <= '9')) return;
        }
        super.write(text);
        tryUpdateValue();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) tryUpdateValue();
        return result;
    }
}
