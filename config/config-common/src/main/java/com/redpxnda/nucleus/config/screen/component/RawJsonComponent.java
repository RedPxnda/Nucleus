package com.redpxnda.nucleus.config.screen.component;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.screen.ConfigScreen;
import com.redpxnda.nucleus.config.screen.JsonEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class RawJsonComponent<T> extends ButtonWidget implements ConfigComponent<T> {
    public static final Logger LOGGER = Nucleus.getLogger("Raw Json Config Editing");
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.json.description");
    public static final Text INVALID_DESC_TEXT = Text.translatable("nucleus.config_screen.json.invalid_description");
    public static final Text INVALID_TOAST_TITLE = Text.translatable("nucleus.config_screen.json.invalid_toast_title");
    public static final Text INVALID_TOAST_DESC = Text.translatable("nucleus.config_screen.json.invalid_toast_description");

    protected final Codec<T> codec;
    protected T value;
    protected boolean isValid = true;
    protected ConfigComponent<?> parent;

    protected RawJsonComponent(Codec<T> codec, int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal("+"), null, DEFAULT_NARRATION_SUPPLIER);
        this.codec = codec;
    }

    @Override
    public void onPress() {
        String initialText = "";
        if (value != null) {
            Optional<JsonElement> optional = codec.encodeStart(JsonOps.INSTANCE, value).result();
            if (optional.isPresent()) initialText = optional.get().toString();
        }
        Screen oldScreen = MinecraftClient.getInstance().currentScreen;
        if (oldScreen instanceof ConfigScreen<?> cs) cs.skipNextInit = true;
        JsonEditorScreen sc = new JsonEditorScreen(oldScreen, this::onJsonUpdate, initialText);
        MinecraftClient.getInstance().setScreen(sc);
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
            var result = codec.parse(JsonOps.INSTANCE, element).get();
            result.ifLeft(t -> value = t);
            result.ifRight(partial -> {
                LOGGER.error("Failed to parse JSON for unknown config field! -> {}", partial.message());
                MinecraftClient.getInstance().getToastManager().add(new SystemToast(
                        SystemToast.Type.PACK_LOAD_FAILURE,
                        INVALID_TOAST_TITLE,
                        INVALID_TOAST_DESC));
            });
        }
        updateValidity();
    }

    @Override
    public boolean isSelected() {
        return isHovered();
    }

    @Override
    public boolean checkValidity() {
        return value != null;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        updateValidity();
    }

    @Override
    public @Nullable Text getInstructionText() {
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
