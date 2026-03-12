package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.config.ConfigObject;
import com.redpxnda.nucleus.config.screen.component.ConfigComponent;
import com.redpxnda.nucleus.config.screen.component.ConfigComponentBehavior;
import com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class MultiConfigScreen extends Screen {
    private final Screen parent;
    private final List<? extends TypedConfig<?>> configs;
    private int activeIndex;

    private ConfigEntriesComponent<?> widget;
    private Button discardButton;
    private Button saveButton;
    private Button instructionsButton;
    public boolean renderInstructions = true;
    private ConfigComponent<?> oldWidgetParent;

    public MultiConfigScreen(Screen parent, List<? extends TypedConfig<?>> configs) {
        super(Component.translatable("nucleus.config_screen.multi_title"));
        this.parent = parent;
        this.configs = configs;
        this.activeIndex = 0;
    }

    private <T> void setupWidgetTyped(ConfigObject.Automatic<T> current, String tabName) {
        if (current.getInstance() == null) current.load();

        Map<String, Tuple<Field, ConfigComponent<?>>> map = new LinkedHashMap<>();
        if (current.getFieldMap() != null) {
            for (Map.Entry<String, Field> entry : current.getFieldMap().entrySet()) {
                var comp = ConfigComponentBehavior.getComponent(entry.getValue(), new ArrayList<>());
                map.put(entry.getKey(), new Tuple<>(entry.getValue(), comp));
            }
        }

        if (widget != null) oldWidgetParent = widget.getParent();
        ConfigEntriesComponent<T> currentWidget = new ConfigEntriesComponent<>(map, minecraft.font, 0, 32, width - 6, height - 64);
        widget = currentWidget;
        currentWidget.setConfigValue(current.getInstance());
        currentWidget.performPositionUpdate();
    }

    @SuppressWarnings("unchecked")
    private <T> void saveCurrentTyped(ConfigObject.Automatic<T> current) {
        if (widget.checkValidity()) {
            T value = ((ConfigEntriesComponent<T>) widget).getConfigValue();
            current.setInstance(value);
            current.save();
            current.load();
        } else {
            minecraft.getToasts().addToast(new SystemToast(
                    SystemToast.SystemToastId.PACK_LOAD_FAILURE,
                    Component.translatable("nucleus.config_screen.save_fail"),
                    Component.translatable("nucleus.config_screen.save_fail.description")));
        }
    }

    @Override
    protected void init() {
        TypedConfig<?> active = configs.get(activeIndex);
        setupWidgetTyped(active.config, active.name);

        discardButton = Button.builder(Component.translatable("nucleus.config_screen.back"), wid -> onClose())
                .bounds(16, height - 26, 96, 20).build();

        saveButton = Button.builder(Component.translatable("nucleus.config_screen.save"), wid -> saveCurrentTyped(active.config))
                .bounds(128, height - 26, 96, 20).build();

        Component enabledText = Component.translatable("nucleus.config_screen.tips_toggle.enabled");
        Component disabledText = Component.translatable("nucleus.config_screen.tips_toggle.disabled");
        instructionsButton = new Button(width - 104, height - 26, 96, 20, enabledText, wid -> {
            renderInstructions = !renderInstructions;
            widget.renderInstructions = renderInstructions;
            wid.setMessage(renderInstructions ? enabledText : disabledText);
        }, Supplier::get) {
            @Override
            public boolean isHoveredOrFocused() {
                return isHovered();
            }
        };

        addRenderableWidget(discardButton);
        addRenderableWidget(saveButton);
        addRenderableWidget(instructionsButton);
        addRenderableWidget(widget);

        setInitialFocus(widget);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = 10;
        int tabWidth = 100;
        for (int i = 0; i < configs.size(); i++) {
            if (mouseX >= x && mouseX <= x + tabWidth && mouseY >= 10 && mouseY <= 30) {
                if (activeIndex != i) {
                    activeIndex = i;
                    TypedConfig<?> active = configs.get(activeIndex);
                    setupWidgetTyped(active.config, active.name);
                }
                return true;
            }
            x += tabWidth + 5;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int x = 10;
        int tabWidth = 100;
        for (int i = 0; i < configs.size(); i++) {
            boolean active = i == activeIndex;
            context.fill(x, 10, x + tabWidth, 30, active ? 0xFF555555 : 0xFF333333);
            context.drawString(font, Component.literal(configs.get(i).name()), x + 5, 15, -1, false);
            x += tabWidth + 5;
        }

        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawString(font, title, 8, 16 - font.lineHeight / 2, -11184811, true);
    }

    @Override
    public void onClose() {
        if (widget != null) widget.setParent(oldWidgetParent);
        minecraft.setScreen(parent);
    }

    @Environment(EnvType.CLIENT)
    public record TypedConfig<T>(String name, ConfigObject.Automatic<T> config) {
    }

}
