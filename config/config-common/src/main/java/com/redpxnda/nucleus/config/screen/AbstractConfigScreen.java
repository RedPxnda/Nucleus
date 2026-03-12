package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.config.screen.component.ConfigComponent;
import com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigScreen<T> extends Screen {
    protected ConfigEntriesComponent<T> widget;
    protected Button discardButton;
    protected Button saveButton;
    protected Button instructionsButton;
    protected boolean renderInstructions = true;
    public boolean skipNextInit = false;
    protected @Nullable ConfigComponent<?> oldWidgetParent;

    protected final Screen parent;

    public AbstractConfigScreen(Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }

    protected abstract void setupWidget();
    protected abstract boolean hasSaveButton();
    protected abstract void saveConfig();

    @Override
    protected void init() {
        setupWidget();

        discardButton = Button.builder(Component.translatable("nucleus.config_screen.back"), wid -> onClose())
                .bounds(16, height - 26, 96, 20).build();

        if (hasSaveButton()) {
            saveButton = Button.builder(Component.translatable("nucleus.config_screen.save"), wid -> {
                if (widget.checkValidity()) {
                    saveConfig();
                    onClose();
                } else {
                    minecraft.getToasts().addToast(new SystemToast(
                            SystemToast.SystemToastId.PACK_LOAD_FAILURE,
                            Component.translatable("nucleus.config_screen.save_fail"),
                            Component.translatable("nucleus.config_screen.save_fail.description")));
                }
            }).bounds(128, height - 26, 96, 20).build();
        }

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
        if (saveButton != null) addRenderableWidget(saveButton);
        addRenderableWidget(instructionsButton);
        addRenderableWidget(widget);

        setInitialFocus(widget);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (saveButton != null) saveButton.active = widget.invalids.isEmpty();
        if (widget.scrollbarVisible())
            context.fill(widget.getX() + widget.getWidth(), widget.getY(), widget.getX() + widget.getWidth() + 8, widget.getY() + widget.getHeight(), -16777216);
        context.pose().pushPose();
        context.pose().translate(0, 0, -15);
        renderBackground(context, mouseX, mouseY, delta);
        context.pose().popPose();
        super.render(context, mouseX, mouseY, delta);
        context.drawString(font, title, 8, 16 - font.lineHeight / 2, -11184811, true);
    }

    @Override
    public void onClose() {
        widget.setParent(oldWidgetParent);
        minecraft.setScreen(parent);
    }
}
