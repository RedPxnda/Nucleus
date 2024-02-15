package com.redpxnda.nucleus.config.screen.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.config.screen.widget.EmptyButtonWidget;
import com.redpxnda.nucleus.config.screen.widget.SelectableOptionsWidget;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DropdownComponent<E> extends TextFieldWidget implements ConfigComponent<E> { // todo colored when invalid
    public static final Text OPEN_TEXT = Text.literal("∨");
    public static final Text CLOSED_TEXT = Text.literal(">");
    public static final Text DESC_TEXT = Text.translatable("nucleus.config_screen.dropdown.description");

    public ConfigComponent<?> parent;
    public final TextRenderer textRenderer;
    public final BiMap<String, E> entries;
    public final Map<String, Text> comments;
    public E selected;
    public boolean isValid = false;
    public boolean isOpen = false;
    public final ButtonWidget dropdownOpener;
    public final ScrollableWidget dropdown;

    public DropdownComponent(TextRenderer textRenderer, int x, int y, int width, int height, Class<E> enumClass) {
        this(textRenderer, x, y, width, height, MiscUtil.evaluateSupplier(() -> {
            assert enumClass.isEnum() : "Inputted class must be an enum!";
            E[] constants = enumClass.getEnumConstants();
            BiMap<String, E> map = HashBiMap.create();
            Map<String, Text> comments = new HashMap<>();
            for (E constant : constants) {
                String name = ((Enum) constant).name();
                try {
                    Field f = enumClass.getField(name);
                    Comment comment = f.getAnnotation(Comment.class);
                    if (comment != null) comments.put(name, Text.literal(comment.value()));
                } catch (NoSuchFieldException ignored) {}
                map.put(name, constant);
            }
            return new Pair<>(map, comments);
        }));
    }

    public DropdownComponent(TextRenderer textRenderer, int x, int y, int width, int height, BiMap<String, E> entries) {
        this(textRenderer, x, y, width, height, new Pair<>(entries, new HashMap<>()));
    }

    public DropdownComponent(TextRenderer textRenderer, int x, int y, int width, int height, Pair<BiMap<String, E>, Map<String, Text>> entriesAndComments) {
        super(textRenderer, x, y, width, height, Text.empty());
        this.entries = entriesAndComments.getLeft();
        this.comments = entriesAndComments.getRight();
        this.textRenderer = textRenderer;

        this.dropdownOpener = new EmptyButtonWidget(x+width-20, y, 20, 20, CLOSED_TEXT, wid -> {
            isOpen = !isOpen;
            wid.setMessage(isOpen ? OPEN_TEXT : CLOSED_TEXT);
        }, Color.WHITE.argb(), Color.TEXT_GRAY.argb());
        this.dropdown = getDropdownWidget();

        setEditable(false);
        setUneditableColor(Color.WHITE.argb());
    }

    @Override
    public void onRemoved() {
        if (parent != null && !isValid) parent.validateChild(this);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused && isOpen) dropdownOpener.onPress();
    }

    public void updateValidity() {
        isValid = checkValidity();
        if (parent != null) {
            if (isValid) parent.validateChild(this);
            else parent.invalidateChild(this);
        }
    }

    public ScrollableWidget getDropdownWidget() {
        return new SelectableOptionsWidget(textRenderer, entries.keySet(), op -> {
            setValue(getValue(op));
            dropdownOpener.onPress();
        }, getX(), getY(), getWidth(), (textRenderer.fontHeight+1)*(Math.min(entries.size(), 5)) + 8);
    }

    @Override
    public void drawInstructionText(DrawContext context, int mouseX, int mouseY) {
        if (getComment(getText()) == null)
            ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public @Nullable Text getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public void performPositionUpdate() {
        dropdownOpener.setPosition(getX()+width-20, getY());
        dropdown.setPosition(getX(), getY());
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isOpen) {
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 5);
            dropdown.render(context, mouseX, mouseY, delta);
            context.getMatrices().pop();
        } else {
            dropdownOpener.render(context, mouseX, mouseY, delta);
            super.renderButton(context, mouseX, mouseY, delta);
            if (isValid && isHovered()) {
                if (!getText().isEmpty()) {
                    Text text = getComment(getText());
                    if (text != null) context.drawTooltip(textRenderer, textRenderer.wrapLines(text, 150), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (dropdownOpener.isMouseOver(mouseX, mouseY) && dropdownOpener.mouseClicked(mouseX, mouseY, button)) return true;
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isOpen && dropdown.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public @Nullable Text getComment(String key) {
        return comments.get(key);
    }

    public String getKey(E value) {
        return entries.inverse().get(value);
    }

    public E getValue(String key) {
        return entries.get(key);
    }

    @Override
    public boolean checkValidity() {
        return selected != null;
    }

    @Override
    public E getValue() {
        return selected;
    }

    @Override
    public void setValue(E value) {
        selected = value;
        setText(getKey(value));
        updateValidity();
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
        updateValidity();
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }
}
