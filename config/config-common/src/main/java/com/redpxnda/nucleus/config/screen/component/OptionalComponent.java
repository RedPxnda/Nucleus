package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.Nucleus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent.KEY_TEXT_WIDTH;

@Environment(EnvType.CLIENT)
public class OptionalComponent<T> extends ClickableWidget implements ConfigComponent<T> {
    public static final Text ENABLED_TEXT = Text.translatable("nucleus.config_screen.optional.description.enabled");
    public static final Text DISABLED_TEXT = Text.translatable("nucleus.config_screen.optional.description.disabled");
    public static final Identifier BUTTON_TEXTURE = new Identifier(Nucleus.MOD_ID, "textures/gui/config/optional.png");

    public final TextRenderer textRenderer;
    public ConfigComponent<?> parent;
    public boolean enabled = true;
    public final ConfigComponent<T> child;
    public final Consumer<ConfigComponent<T>> emptyValueSetter;
    public int buttonX = 0;

    public OptionalComponent(TextRenderer textRenderer, int x, int y, int width, int height, ConfigComponent<T> child, Consumer<ConfigComponent<T>> emptyValueSetter) {
        super(x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.child = child;
        child.setParent(this);
        this.emptyValueSetter = emptyValueSetter;
    }

    @Override
    public InlineMode getInlineMode() {
        return child.getInlineMode();
    }

    @Override
    public void onRemoved() {
        child.onRemoved();
    }

    @Override
    public void performPositionUpdate() {
        child.setX(getX());
        child.setY(getY());
        child.performPositionUpdate();
        width = child.getWidth();
        height = child.getHeight();
        buttonX = getInlineMode() == InlineMode.INLINE ? -28 : KEY_TEXT_WIDTH-66;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean buttonHovered = mouseX >= getX()+buttonX && mouseX < getX()+buttonX+20 && mouseY >= getY() && mouseY < getY()+20;
        context.drawTexture(BUTTON_TEXTURE, getX()+buttonX, getY(), enabled ? 0 : 20, buttonHovered ? 20 : 0, 20, 20, 64, 64);
        if (buttonHovered) context.drawTooltip(textRenderer, textRenderer.wrapLines(enabled ? ENABLED_TEXT : DISABLED_TEXT, 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);

        if (enabled) {
            child.render(context, mouseX, mouseY, delta);
            child.drawInstructionText(context, mouseX, mouseY);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || (mouseX >= getX()+buttonX && mouseX < getX()+buttonX+20 && mouseY >= getY() && mouseY < getY()+20);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= getX()+buttonX && mouseX < getX()+buttonX+20 && mouseY >= getY() && mouseY < getY()+20) {
            setEnabled(!enabled);
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            child.setFocused(false);
            return true;
        }
        if (enabled && child.mouseClicked(mouseX, mouseY, button)) {
            child.setFocused(true);
            return true;
        }
        child.setFocused(false);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return enabled && child.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return enabled && child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return enabled && child.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return enabled && child.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return enabled && child.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return enabled && child.charTyped(chr, modifiers);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean checkValidity() {
        return !enabled || child.checkValidity();
    }

    @Override
    public T getValue() {
        return enabled ? child.getValue() : null;
    }

    public void setValue(T value) {
        if (value == null) {
            setEnabled(false);
            emptyValueSetter.accept(child);
        } else
            child.setValue(value);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled && !enabled) onRemoved();
        else if (!this.enabled && enabled) {
            boolean valid = child.checkValidity();
            if (!valid) invalidateChild(child);
        }
        this.enabled = enabled;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.parent = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }
}
