package com.redpxnda.nucleus.config.screen;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.config.preset.ConfigProvider;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PresetComponent<C, E extends Enum<E> & ConfigProvider<C>> extends DropdownComponent<ConfigPreset<C, E>> {
    public static final Identifier WARNING = new Identifier("textures/gui/report_button.png");
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.preset.description");
    public static final Text WARNING_TEXT = Text.translatable("nucleus.config_screen.preset.warning");

    public PresetComponent(TextRenderer textRenderer, int x, int y, int width, int height, Class<E> enumClass) {
        super(textRenderer, x, y, width, height, MiscUtil.evaluateSupplier(() -> {
            E[] constants = enumClass.getEnumConstants();
            BiMap<String, ConfigPreset<C, E>> map = HashBiMap.create();
            map.put("none", ConfigPreset.none());
            Map<String, Text> comments = new HashMap<>();
            for (E constant : constants) {
                String name = constant.name();
                try {
                    Field f = enumClass.getField(name);
                    Comment comment = f.getAnnotation(Comment.class);
                    if (comment != null) comments.put(name, Text.literal(comment.value()));
                } catch (NoSuchFieldException ignored) {}
                map.put(name, ConfigPreset.of(constant));
            }
            return new Pair<>(map, comments);
        }));
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (selected.getEntry() != null) {
            boolean hovered = mouseX >= getX()+getWidth()+8 && mouseX < getX()+getWidth()+28 && mouseY >= getY() && mouseY < getY()+20;
            context.drawTexture(WARNING, getX()+getWidth()+8, getY(), 0, hovered ? 20 : 0, 20, 20, 64, 64);
            if (hovered) context.drawTooltip(textRenderer, textRenderer.wrapLines(WARNING_TEXT, 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
        }
        super.renderButton(context, mouseX, mouseY, delta);
    }

    @Override
    public @Nullable Text getInstructionText() {
        return DESC_TEXT;
    }
}
