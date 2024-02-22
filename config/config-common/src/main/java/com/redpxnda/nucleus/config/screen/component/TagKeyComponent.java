package com.redpxnda.nucleus.config.screen.component;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TagKeyComponent<T> extends ClickableWidget implements ConfigComponent<TagKey<T>> {
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.tag.description");

    public final IdentifierComponent delegate;
    public final RegistryKey<? extends Registry<T>> registry;
    public ConfigComponent<?> parent;

    public TagKeyComponent(RegistryKey<? extends Registry<T>> registry, TextRenderer textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.registry = registry;
        this.delegate = new IdentifierComponent(textRenderer, x, y, width, height);
    }

    @Override
    public void onRemoved() {
        delegate.onRemoved();
    }

    @Override
    public boolean checkValidity() {
        return delegate.checkValidity();
    }

    @Override
    public TagKey<T> getValue() {
        Identifier val = delegate.getValue();
        return val == null ? null : TagKey.of(registry, val);
    }

    @Override
    public void performPositionUpdate() {
        delegate.setX(getX());
        delegate.setY(getY());
    }

    @Override
    public void setValue(TagKey<T> value) {
        delegate.setValue(value.id());
    }

    @Override
    public Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return delegate.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return delegate.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return delegate.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return delegate.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return delegate.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return delegate.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return delegate.charTyped(chr, modifiers);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
        delegate.updateValidity();
        delegate.setParent(this);
    }

    @Override
    public void setFocused(boolean focused) {
        delegate.setFocused(focused);
        if (!focused) {
            delegate.setSelectionStart(0);
            delegate.setSelectionEnd(0);
        }
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        delegate.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
