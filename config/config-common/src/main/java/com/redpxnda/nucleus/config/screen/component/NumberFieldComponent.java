package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NumberFieldComponent<N extends Number> extends TextFieldWidget implements ConfigComponent<N> {
    public ConfigComponent<?> widget;
    public final NumberParser<N> parser;
    public final boolean allowDecimals;
    public final TextRenderer textRenderer;
    public final Text infoText;
    public final String hashtag;
    public final int hashtagWidth;
    public boolean isValid = true;

    public NumberFieldComponent(TextRenderer textRenderer, int x, int y, int width, int height, NumberParser<N> parser, boolean allowDecimals) {
        super(textRenderer, x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.parser = parser;
        this.allowDecimals = allowDecimals;
        this.infoText = Text.translatable(allowDecimals ? "nucleus.config_screen.double" : "nucleus.config_screen.integer");
        this.hashtag = allowDecimals ? "#.# " : "# ";
        this.hashtagWidth = textRenderer.getWidth(hashtag);

        setDrawsBackground(false);
    }

    @Override
    public void onRemoved() {
        if (widget != null && !isValid) widget.validateChild(this);
    }

    public void updateValidity() {
        if (widget != null) {
            if (getText().isEmpty()) {
                if (isValid) {
                    widget.invalidateChild(this);
                    isValid = false;
                }
            } else {
                if (!isValid) {
                    widget.validateChild(this);
                    isValid = true;
                }
            }
        }
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(textRenderer, hashtag, getX(), getY()+6, isValid ? -11184811 : -43691, true);
        context.getMatrices().push();
        context.getMatrices().translate(hashtagWidth, 6, 0);
        super.renderButton(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }

    @Override
    public Text getInstructionText() {
        return infoText;
    }

    @Override
    public boolean checkValidity() {
        return getValue() != null;
    }

    @Override
    public N getValue() {
        try {
            return parser.parse(getText());
        } catch (Exception e) {
            return null;
        }
    }
    public void setValue(N value) {
        setText(value.toString());
        updateValidity();
    }

    @Override
    public void write(String text) {
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (!((chr >= '0' && chr <= '9') || (allowDecimals && chr == '.' && !getText().contains(".")))) return;
        }
        super.write(text);
        updateValidity();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) updateValidity();
        return result;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        N val = getValue();
        if (val != null) {
            int increment = 0;
            if (Screen.hasShiftDown()) increment+=5;
            if (Screen.hasControlDown()) increment+=10;
            if (increment == 0) increment = 1;

            if (allowDecimals) {
                double num = val.doubleValue() + (amount < 0 ? -increment : increment);
                setText(String.valueOf(num));
            } else {
                int num = val.intValue() + (amount < 0 ? -increment : increment);
                setText(String.valueOf(num));
            }
        }
        return true;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.widget = widget;
        updateValidity();
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

    public interface NumberParser<N extends Number> {
        N parse(String str) throws NumberFormatException;
    }
}
