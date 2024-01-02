package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

public class CollectionComponent<T, C extends Collection<T>> extends ClickableWidget implements ConfigComponent<C> {
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.collection.description");
    public static final Text UP_ICON = Text.literal("∧");
    public static final Text DOWN_ICON = Text.literal("∨");
    public static final Text REMOVE_ICON = Text.literal("×");

    public final Supplier<C> creator;
    public final Supplier<ConfigComponent<T>> elementCreator;
    public final List<ConfigComponent<T>> elements = new ArrayList<>();
    public final Map<ConfigComponent<T>, ButtonWidget> removers = new HashMap<>();
    public ConfigComponent<?> parent;
    public boolean minimized = true;
    public ConfigComponent<?> focusedComponent = null;
    public ButtonWidget adder;
    public ButtonWidget minimizer;

    public CollectionComponent(Supplier<C> creator, Type type, int x, int y) {
        super(x, y, 142, 8, Text.empty());
        this.creator = creator;

        this.elementCreator = () -> {
            ConfigComponent<T> comp = (ConfigComponent<T>) ConfigComponentType.getFor(type).get();
            comp.setParent(this);
            elements.add(comp);
            removers.put(comp, ButtonWidget.builder(REMOVE_ICON, wid -> {
                if (Screen.hasShiftDown())
                    MiscUtil.moveListElementDown(elements, comp);
                else if (Screen.hasControlDown())
                    MiscUtil.moveListElementUp(elements, comp);
                else {
                    comp.onRemoved();
                    elements.remove(comp);
                    removers.remove(comp);
                }
                requestPositionUpdate();
            }).dimensions(0, 0, 20, 20).build());
            return comp;
        };

        adder = ButtonWidget.builder(Text.literal("＋"), wid -> {
            elementCreator.get();
            requestPositionUpdate();
        }).dimensions(0, 0, 20, 20).build();

        Text minimizedText = Text.literal(">");
        Text maximizedText = Text.literal("∨");
        minimizer = ButtonWidget.builder(minimizedText, wid -> {
            minimized = !minimized;
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
        for (ConfigComponent<T> comp : elements) {
            if (!comp.checkValidity()) return false;
        }
        return true;
    }

    @Override
    public C getValue() {
        C result = creator.get();
        for (ConfigComponent<T> component : elements) {
            result.add(component.getValue());
        }
        return result;
    }

    @Override
    public void setValue(C value) {
        elements.clear();
        value.forEach(t -> {
            var element = elementCreator.get();
            element.setValue(t);
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
    public void performPositionUpdate() {
        minimizer.setPosition(getX()+122, getY());
        height = 20;
        if (!minimized) {
            elements.forEach(element -> {
                height += 8;
                element.setPosition(getX() + 8, getY() + height); // todo element infos
                if (element.getWidth() + 28 > width) width = element.getWidth() + 28;
                height += element.getHeight();
                removers.get(element).setPosition(element.getX() + element.getWidth() + 8, element.getY());
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
            for (ConfigComponent<T> element : elements) {
                element.render(context, mouseX, mouseY, delta);

                ButtonWidget remover = removers.get(element);
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
        if (!isMouseOver(mX, mY)) return false;
        if (minimizer.isMouseOver(mX, mY)) return minimizer.mouseClicked(mX, mY, button);
        if (!minimized && adder.isMouseOver(mX, mY)) return adder.mouseClicked(mX, mY, button);
        for (ConfigComponent<T> component : elements) {
            ButtonWidget remover;
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
            } else if ((remover = removers.get(component)).isMouseOver(mX, mY) && remover.mouseClicked(mX, mY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mX, double mY, int button) {
        if (focusedComponent != null && focusedComponent.mouseReleased(mX, mY, button))
            return true;
        return super.mouseReleased(mX, mY, button);
    }

    @Override
    public boolean mouseDragged(double mX, double mY, int button, double deltaX, double deltaY) {
        if (focusedComponent != null && focusedComponent.mouseDragged(mX, mY, button, deltaX, deltaY))
            return true;
        return super.mouseDragged(mX, mY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mX, double mY, double amount) {
        if (focusedComponent != null && focusedComponent.mouseScrolled(mX, mY, amount))
            return true;
        return super.mouseScrolled(mX, mY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null && focusedComponent.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null && focusedComponent.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (focusedComponent != null && focusedComponent.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
