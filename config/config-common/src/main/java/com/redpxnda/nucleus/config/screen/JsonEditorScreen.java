package com.redpxnda.nucleus.config.screen;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.Nucleus;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class JsonEditorScreen extends Screen {
    protected ButtonWidget discardButton;
    protected ButtonWidget saveButton;
    protected EditBoxWidget editBox;
    protected final Screen parent;
    protected final Consumer<JsonElement> updateListener;
    protected final String initialText;

    public JsonEditorScreen(Screen parent, Consumer<JsonElement> updateListener, String initialText) {
        super(Text.translatable("nucleus.json_editor.title"));
        this.parent = parent;
        this.updateListener = updateListener;
        this.initialText = initialText;
    }

    public void setText(String text) {
        editBox.setText(text);
    }

    @Override
    protected void init() {
        discardButton = ButtonWidget.builder(Text.translatable("nucleus.config_screen.discard"), wid -> {
            close();
        }).dimensions(16, height-26, 96, 20).build();
        saveButton = ButtonWidget.builder(Text.translatable("nucleus.config_screen.save"), wid -> {
            JsonElement element = getJson();
            if (element != null) {
                updateListener.accept(element);
                closeNoUpdate();
            } else {
                client.getToastManager().add(new SystemToast(
                        SystemToast.Type.PACK_LOAD_FAILURE,
                        Text.translatable("nucleus.json_editor.save_fail"),
                        Text.translatable("nucleus.json_editor.save_fail.description")));
            }
        }).dimensions(128, height-26, 96, 20).build();

        editBox = new EditBoxWidget(client.textRenderer, 8, 8, width-8, height-40, Text.empty(), Text.empty());

        addDrawableChild(discardButton);
        addDrawableChild(saveButton);
        addDrawableChild(editBox);

        setInitialFocus(editBox);
        setText(initialText);
    }

    public @Nullable JsonElement getJson() {
        try {
            return Nucleus.GSON.fromJson(editBox.getText(), JsonElement.class);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, -15);
        renderBackgroundTexture(context);
        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        updateListener.accept(null);
        client.setScreen(parent);
    }

    public void closeNoUpdate() {
        client.setScreen(parent);
    }
}
