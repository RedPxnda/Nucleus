package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BooleanComponent extends ButtonWidget implements ConfigComponent<Boolean> {
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.boolean.description");
    public static final Text ON_TEXT = Text.translatable("nucleus.config_screen.boolean.on");
    public static final Text OFF_TEXT = Text.translatable("nucleus.config_screen.boolean.off");

    public ConfigComponent<?> widget;
    public boolean checked = false;

    public BooleanComponent(int x, int y, int width, int height) {
        super(x, y, width, height, OFF_TEXT, wid -> {}, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        setFocused(isHovered());
    }

    @Override
    public Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public void onPress() {
        checked = !checked;
        setMessage(checked ? ON_TEXT : OFF_TEXT);
    }

    @Override
    public boolean checkValidity() {
        return true;
    }

    @Override
    public Boolean getValue() {
        return checked;
    }

    public void setValue(Boolean value) {
        checked = value;
        setMessage(checked ? ON_TEXT : OFF_TEXT);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.widget = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return widget;
    }
}
