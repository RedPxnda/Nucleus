package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.config.ConfigObject;
import com.redpxnda.nucleus.config.screen.component.ConfigComponent;
import com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

import java.lang.reflect.Field;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ConfigScreen<T> extends AbstractConfigScreen<T> {
    private final Map<String, Tuple<Field, ConfigComponent<?>>> components;
    private final ConfigObject<T> config;

    // Original constructor (components + config)
    public ConfigScreen(Screen parent, Map<String, Tuple<Field, ConfigComponent<?>>> components, ConfigObject<T> config) {
        super(parent, Component.translatable("nucleus.config_screen.title", config.id + ".jsonc"));
        this.components = components;
        this.config = config;
    }

    // Original constructor (with pre-built widget)
    public ConfigScreen(Screen parent, ConfigEntriesComponent<T> widget) {
        super(parent, Component.translatable("nucleus.config_screen.inner_title"));
        this.widget = widget;
        this.components = null;
        this.config = null;
    }

    @Override
    protected void setupWidget() {
        if (skipNextInit) {
            skipNextInit = false;
            widget.performPositionUpdate();
        } else {
            if (widget == null) {
                widget = new ConfigEntriesComponent<>(components, minecraft.font, 0, 32, width - 6, height - 64);
                widget.performPositionUpdate();
                widget.setConfigValue(config.getInstance());
            } else {
                oldWidgetParent = widget.getParent();
                widget.setParent(null);
                widget.setPosition(0, 32);
                widget.setWidth(width - 6);
                widget.setHeight(height - 64);
                widget.performPositionUpdate();
            }
        }
    }

    @Override
    protected boolean hasSaveButton() {
        return config != null;
    }

    @Override
    protected void saveConfig() {
        config.setInstance(widget.getConfigValue());
        config.save();
        config.load();
    }
}
