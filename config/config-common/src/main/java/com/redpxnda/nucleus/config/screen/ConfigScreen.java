package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.config.ConfigObject;
import com.redpxnda.nucleus.config.screen.component.ConfigComponent;
import com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConfigScreen<T> extends Screen {
    protected final Map<String, Pair<Field, ConfigComponent<?>>> components;
    protected ConfigEntriesComponent<T> widget;
    protected ButtonWidget discardButton;
    protected ButtonWidget saveButton;
    protected ButtonWidget instructionsButton;
    protected final @Nullable ConfigObject<T> config;
    protected final Screen parent;
    public boolean renderInstructions = true;
    public boolean skipNextInit = false;
    protected @Nullable ConfigComponent<?> oldWidgetParent;

    public ConfigScreen(Screen parent, Map<String, Pair<Field, ConfigComponent<?>>> components, ConfigObject<T> config) {
        super(Text.translatable("nucleus.config_screen.title", config.name + ".jsonc"));
        this.parent = parent;
        this.config = config;
        this.components = components;
    }

    public ConfigScreen(Screen parent, ConfigEntriesComponent<T> component) {
        super(Text.translatable("nucleus.config_screen.inner_title"));
        this.parent = parent;
        this.config = null;
        this.components = null;
        this.widget = component;
    }

    @Override
    protected void init() {
        if (skipNextInit) {
            skipNextInit = false;
            /*widget.setPosition(0, 32);
            widget.setWidth(width - 6);
            widget.setHeight(height - 64);*/
            widget.performPositionUpdate();
        } else {
            if (widget == null) {
                widget = new ConfigEntriesComponent<>(components, client.textRenderer, 0, 32, width - 6, height - 64);
                widget.performPositionUpdate();
                widget.setValue(config.getInstance());
            } else {
                oldWidgetParent = widget.getParent();
                widget.setParent(null);
                widget.setPosition(0, 32);
                widget.setWidth(width - 6);
                widget.setHeight(height - 64);
                widget.performPositionUpdate();
            }

            discardButton = ButtonWidget.builder(config != null ? Text.translatable("nucleus.config_screen.discard") : Text.translatable("nucleus.config_screen.back"), wid -> {
                close();
            }).dimensions(16, height - 26, 96, 20).build();

            if (config != null) {
                saveButton = ButtonWidget.builder(Text.translatable("nucleus.config_screen.save"), wid -> {
                    if (widget.checkValidity()) {
                        config.setInstance(widget.getValue());
                        config.save();
                        config.load();
                        close();
                    } else {
                        client.getToastManager().add(new SystemToast(
                                SystemToast.Type.PACK_LOAD_FAILURE,
                                Text.translatable("nucleus.config_screen.save_fail"),
                                Text.translatable("nucleus.config_screen.save_fail.description")));
                    }
                }).dimensions(128, height - 26, 96, 20).build();
            }

            Text enabledText = Text.translatable("nucleus.config_screen.tips_toggle.enabled");
            Text disabledText = Text.translatable("nucleus.config_screen.tips_toggle.disabled");
            instructionsButton = new ButtonWidget(width - 104, height - 26, 96, 20, enabledText, wid -> {
                renderInstructions = !renderInstructions;
                widget.renderInstructions = renderInstructions;
                wid.setMessage(renderInstructions ? enabledText : disabledText);
            }, Supplier::get) {
                @Override
                public boolean isSelected() {
                    return isHovered();
                }
            };
        }

        addDrawableChild(discardButton);
        if (saveButton != null) addDrawableChild(saveButton);
        addDrawableChild(instructionsButton);
        addDrawableChild(widget);

        setInitialFocus(widget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (saveButton != null) saveButton.active = widget.invalids.isEmpty();
        context.drawText(textRenderer, title, 8, 16 - textRenderer.fontHeight/2, -11184811, true);
        if (widget.overflows())
            context.fill(widget.getX() + widget.getWidth(), widget.getY(), widget.getX() + widget.getWidth() + 8, widget.getY() + widget.getHeight(), -16777216);
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, -15);
        renderBackgroundTexture(context);
        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        widget.setParent(oldWidgetParent);
        client.setScreen(parent);
    }
}
