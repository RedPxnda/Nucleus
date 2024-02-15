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

import java.lang.reflect.Field;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ConfigScreen<T> extends Screen {
    protected final Map<String, Pair<Field, ConfigComponent<?>>> components;
    protected ConfigEntriesComponent<T> widget;
    protected ButtonWidget discardButton;
    protected ButtonWidget saveButton;
    protected final ConfigObject<T> config;
    protected final Screen parent;

    public ConfigScreen(Screen parent, Map<String, Pair<Field, ConfigComponent<?>>> components, ConfigObject<T> config) {
        super(Text.translatable("nucleus.config_screen.title", config.name + ".jsonc"));
        this.parent = parent;
        this.config = config;
        this.components = components;
    }

    @Override
    protected void init() {
        widget = new ConfigEntriesComponent<>(components, client.textRenderer, 0, 32, width-6, height-64);
        widget.performPositionUpdate();
        widget.setValue(config.getInstance());

        addDrawable(widget);
        addSelectableChild(widget);

        discardButton = ButtonWidget.builder(Text.translatable("nucleus.config_screen.discard"), wid -> {
            close();
        }).dimensions(16, height-26, 96, 20).build();
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
        }).dimensions(128, height-26, 96, 20).build();

        addDrawable(discardButton);
        addSelectableChild(discardButton);

        addDrawable(saveButton);
        addSelectableChild(saveButton);

        setInitialFocus(widget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        saveButton.active = widget.invalids.isEmpty();
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
        client.setScreen(parent);
    }
}
