package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent.KEY_TEXT_WIDTH;

public class MapComponent<K, V, M extends Map<K, V>> extends ClickableWidget implements ConfigComponent<M> {
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.map.description");
    public static final Text UP_ICON = Text.literal("∧");
    public static final Text DOWN_ICON = Text.literal("∨");
    public static final Text REMOVE_ICON = Text.literal("×");

    public final Supplier<M> creator;
    public final Supplier<Pair<ConfigComponent<K>, ConfigComponent<V>>> elementCreator;
    public final Map<ConfigComponent<K>, Pair<ConfigComponent<V>, ButtonWidget>> elements = new LinkedHashMap<>();
    public ConfigComponent<?> parent;
    public boolean minimized = true;
    public ConfigComponent<?> focusedComponent = null;
    public final ButtonWidget adder;
    public final ButtonWidget minimizer;

    public MapComponent(Supplier<M> creator, Type keyType, Type valueType, int x, int y) {
        super(x, y, 142, 8, Text.empty());
        this.creator = creator;

        this.elementCreator = () -> {
            ConfigComponent<V> comp = ConfigComponentBehavior.getComponent(valueType, new ArrayList<>());
            ConfigComponent<K> keyComp = ConfigComponentBehavior.getComponent(keyType, new ArrayList<>());

            comp.setParent(this);
            keyComp.setParent(this);

            elements.put(keyComp, new Pair<>(
                    comp,
                    ButtonWidget.builder(REMOVE_ICON, wid -> {
                        if (Screen.hasShiftDown())
                            MiscUtil.moveMapKeyDown(elements, keyComp);
                        else if (Screen.hasControlDown())
                            MiscUtil.moveMapKeyUp(elements, keyComp);
                        else {
                            keyComp.onRemoved();
                            comp.onRemoved();
                            elements.remove(keyComp);
                        }
                        requestPositionUpdate();
                    }).dimensions(0, 0, 20, 20).build()
            ));
            return new Pair<>(keyComp, comp);
        };

        adder = ButtonWidget.builder(Text.literal("＋"), wid -> {
            elementCreator.get();
            requestPositionUpdate();
        }).dimensions(0, 0, 20, 20).build();

        Text minimizedText = Text.literal(">");
        Text maximizedText = Text.literal("∨");
        minimizer = ButtonWidget.builder(minimizedText, wid -> {
            minimized = !minimized;
            if (minimized) focusedComponent = null;
            wid.setMessage(minimized ? minimizedText : maximizedText);
            requestPositionUpdate();
        }).dimensions(0, 0, 20, 20).build();

        //performPositionUpdate();
    }

    @Override
    public void drawInstructionText(DrawContext context, int mouseX, int mouseY) {
        if (minimizer.isMouseOver(mouseX, mouseY))
            ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public @Nullable Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public InlineMode getInlineMode() {
        return minimized ? InlineMode.NONE : InlineMode.DRAW_LINE;
    }

    @Override
    public boolean checkValidity() {
        for (var entry : elements.entrySet()) {
            if (!entry.getValue().getLeft().checkValidity()) return false;
        }
        return true;
    }

    @Override
    public M getValue() {
        M result = creator.get();
        for (var entry : elements.entrySet()) {
            result.put(entry.getKey().getValue(), entry.getValue().getLeft().getValue());
        }
        return result;
    }

    @Override
    public void setValue(M value) {
        elements.clear();
        value.forEach((k, v) -> {
            var pair = elementCreator.get();
            pair.getLeft().setValue(k);
            pair.getRight().setValue(v);
        });
        requestPositionUpdate();
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            if (focusedComponent != null) focusedComponent.setFocused(false);
            focusedComponent = null;
        }
    }

    @Override
    public void onRemoved() {
        if (!minimized)
            minimizer.onPress();
    }

    @Override
    public void performPositionUpdate() {
        minimizer.setPosition(getX()+KEY_TEXT_WIDTH-38, getY());
        width = KEY_TEXT_WIDTH-18;
        height = 20;
        if (!minimized) {
            elements.forEach((key, pair) -> {
                ConfigComponent<V> element = pair.getLeft();
                ButtonWidget remover = pair.getRight();

                height += 8;
                key.setPosition(getX() + 8, getY() + height);
                element.setPosition(key.getX() + key.getWidth() + 20, getY() + height);

                int newWidth = key.getWidth() + element.getWidth() + 56;
                if (newWidth > width) width = newWidth;
                height += element.getHeight();
                remover.setPosition(element.getX() + element.getWidth() + 8, element.getY());
                key.performPositionUpdate();
                element.performPositionUpdate();
            });
            height += 8;
            adder.setPosition(getX() + 8, getY() + height);
            height += 20;
        }
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        minimizer.render(context, mouseX, mouseY, delta);
        if (!minimized) {
            int index = 0;
            for (var entry : elements.entrySet()) {
                ConfigComponent<K> key = entry.getKey();
                ConfigComponent<V> element = entry.getValue().getLeft();
                ButtonWidget remover = entry.getValue().getRight();

                key.render(context, mouseX, mouseY, delta);
                context.drawText(MinecraftClient.getInstance().textRenderer, "=", key.getX() + key.getWidth() + 8, key.getY() + 6, -11184811, true);
                element.render(context, mouseX, mouseY, delta);
                if (Screen.hasShiftDown()) {
                    remover.setMessage(DOWN_ICON);
                    remover.active = index != elements.size()-1;
                } else if (Screen.hasControlDown()) {
                    remover.setMessage(UP_ICON);
                    remover.active = index != 0;
                } else {
                    remover.setMessage(REMOVE_ICON);
                    remover.active = true;
                }
                remover.render(context, mouseX, mouseY, delta);
                index++;
            }
            adder.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mX, double mY, int button) {
        //if (!isMouseOver(mX, mY)) return false;
        if (minimizer.isMouseOver(mX, mY)) return minimizer.mouseClicked(mX, mY, button);
        if (!minimized) {
            if (adder.isMouseOver(mX, mY)) return adder.mouseClicked(mX, mY, button);
            for (var entry : elements.entrySet()) {
                ConfigComponent<K> key = entry.getKey();
                ConfigComponent<V> component = entry.getValue().getLeft();
                ButtonWidget remover = entry.getValue().getRight();
                if (focusedComponent != null && focusedComponent.mouseClicked(mX, mY, button)) {
                    return true;
                } else if (component.isMouseOver(mX, mY)) {
                    if (button == 0) {
                        if (focusedComponent != null) focusedComponent.setFocused(false);
                        component.setFocused(true);
                        focusedComponent = component;
                    }
                    component.mouseClicked(mX, mY, button);
                    return true;
                } else if (key.isMouseOver(mX, mY)) {
                    if (button == 0) {
                        if (focusedComponent != null) focusedComponent.setFocused(false);
                        key.setFocused(true);
                        focusedComponent = key;
                    }
                    key.mouseClicked(mX, mY, button);
                    return true;
                } else if (remover.isMouseOver(mX, mY) && remover.mouseClicked(mX, mY, button)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mX, double mY, int button) {
        if (!minimized && focusedComponent != null && focusedComponent.mouseReleased(mX, mY, button))
            return true;
        return super.mouseReleased(mX, mY, button);
    }

    @Override
    public boolean mouseDragged(double mX, double mY, int button, double deltaX, double deltaY) {
        if (!minimized && focusedComponent != null && focusedComponent.mouseDragged(mX, mY, button, deltaX, deltaY))
            return true;
        return super.mouseDragged(mX, mY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mX, double mY, double amount) {
        if (!minimized && focusedComponent != null && focusedComponent.mouseScrolled(mX, mY, amount))
            return true;
        return super.mouseScrolled(mX, mY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!minimized && focusedComponent != null && focusedComponent.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!minimized && focusedComponent != null && focusedComponent.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!minimized && focusedComponent != null && focusedComponent.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
