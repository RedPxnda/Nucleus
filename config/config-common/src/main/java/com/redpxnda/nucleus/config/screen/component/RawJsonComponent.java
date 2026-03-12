package com.redpxnda.nucleus.config.screen.component;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.screen.ConfigScreen;
import com.redpxnda.nucleus.config.screen.JsonEditorScreen;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RawJsonComponent<T> extends Button implements ConfigComponent<T> {
    public static final Logger LOGGER = Nucleus.getLogger("Raw Json Config Editing");
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.json.description");
    public static final Component INVALID_DESC_TEXT = Component.translatable("nucleus.config_screen.json.invalid_description");
    public static final Component INVALID_TOAST_TITLE = Component.translatable("nucleus.config_screen.json.invalid_toast_title");
    public static final Component INVALID_TOAST_DESC = Component.translatable("nucleus.config_screen.json.invalid_toast_description");

    protected final Codec<T> codec;
    protected T value;
    protected boolean isValid = true;
    protected ConfigComponent<?> parent;

    protected RawJsonComponent(Codec<T> codec, int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("+"), null, DEFAULT_NARRATION);
        this.codec = codec;
    }

    @Override
    public void onPress() {
        String initialText = "";
        if (value != null) {
            Optional<JsonElement> optional = codec.encodeStart(JsonOps.INSTANCE, value).result();
            if (optional.isPresent()) initialText = optional.get().toString();
        }
        Screen oldScreen = Minecraft.getInstance().screen;
        if (oldScreen instanceof ConfigScreen<?> cs) cs.skipNextInit = true;
        JsonEditorScreen sc = new JsonEditorScreen(oldScreen, this::onJsonUpdate, initialText);
        Minecraft.getInstance().setScreen(sc);
    }

    @Override
    public void onRemoved() {
        if (parent != null && !isValid) parent.validateChild(this);
    }

    public void updateValidity() {
        if (parent != null) {
            if (!checkValidity()) {
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

    public void onJsonUpdate(@Nullable JsonElement element) {
        if (element != null) {
            var result = codec.decode(JsonOps.INSTANCE, element);
            result.ifSuccess(t -> value = t.getFirst());
            result.ifError(partial -> {
                LOGGER.error("Failed to parse JSON for unknown config field! -> {}", partial.message());
                Minecraft.getInstance().getToasts().addToast(new SystemToast(
                        SystemToast.SystemToastId.PACK_LOAD_FAILURE,
                        INVALID_TOAST_TITLE,
                        INVALID_TOAST_DESC));
            });
        }
        updateValidity();
    }

    @Override
    public boolean isHoveredOrFocused() {
        return isHovered();
    }

    @Override
    public boolean checkValidity() {
        return value != null;
    }

    @Override
    public T getConfigValue() {
        return value;
    }

    @Override
    public void setConfigValue(T value) {
        this.value = value;
        updateValidity();
    }

    @Override
    public @Nullable Component getInstructionText() {
        return checkValidity() ? DESC_TEXT : INVALID_DESC_TEXT;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }
}
