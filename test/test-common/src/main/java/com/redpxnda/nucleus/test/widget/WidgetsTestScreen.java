package com.redpxnda.nucleus.test.widget;

import com.redpxnda.nucleus.widgets.layout.LayoutModifier;
import com.redpxnda.nucleus.widgets.layout.LayoutParams;
import com.redpxnda.nucleus.widgets.layout.layouter.*;
import com.redpxnda.nucleus.widgets.widgets.NucleusWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleConsumer;

/**
 * test class written by chat gpt since i cant be bothered
 */
public final class WidgetsTestScreen extends Screen {
    private Panel root;
    private StringWidget status;

    public WidgetsTestScreen() {
        super(Component.literal("Nucleus Widgets Test"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new WidgetsTestScreen());
    }

    @Override
    protected void init() {
        int rootWidth = Math.min(324, width - 8);
        int rootHeight = Math.min(164, height - 8);
        int rootX = (width - rootWidth) / 2;
        int rootY = (height - rootHeight) / 2;
        int contentWidth = Math.max(0, rootWidth - 12);
        int columnWidth = Math.max(64, (contentWidth - 8) / 2);
        Font font = Minecraft.getInstance().font;

        root = panel(rootX, rootY, rootWidth, rootHeight,
            new PaddingLayouter(6, 6, 6, 6, new ColumnLayouter(5)));
        root.debug = true;
        addRenderableWidget(root);
        root.setParent(this);

        StringWidget title = new StringWidget(0, 0, contentWidth, 14,
            Component.literal("Nucleus widget test"), font).alignCenter();
        root.addLayoutedChild(wrap(title));

        status = new StringWidget(0, 0, Math.max(50, contentWidth - 84), 14,
            Component.literal("Ready"), font);

        Panel left = panel(0, 0, columnWidth, 95, new ColumnLayouter(5));
        Panel right = panel(0, 0, columnWidth, 84, new ColumnLayouter(5));
        Panel controls = panel(0, 0, contentWidth, 95, new RowLayouter(8));
        left.debug = true;
        right.debug = true;
        controls.debug = true;

        Button button = Button.builder(Component.literal("Button"), b ->
            status.setMessage(Component.literal("Button clicked")))
            .bounds(0, 0, columnWidth, 20).build();
        left.addLayoutedChild(wrap(button), params(LayoutModifiers.maxSize(columnWidth, 20)));

        Checkbox checkbox = Checkbox.builder(Component.literal("Checkbox"), font)
            .pos(0, 0)
            .selected(true)
            .maxWidth(columnWidth)
            .onValueChange((box, selected) -> status.setMessage(
                Component.literal("Checkbox: " + selected)))
            .build();
        left.addLayoutedChild(wrap(checkbox), params(LayoutModifiers.maxSize(columnWidth, 20)));

        CycleButton<Boolean> cycle = CycleButton.onOffBuilder(true)
            .create(0, 0, columnWidth, 20, Component.literal("Cycle"),
                (buttonWidget, value) -> status.setMessage(
                    Component.literal("Cycle: " + value)));
        left.addLayoutedChild(wrap(cycle));

        DemoSlider slider = new DemoSlider(0, 0, columnWidth, 20,
            value -> status.setMessage(Component.literal("Slider: " + Math.round(value * 100) + "%")));
        left.addLayoutedChild(wrap(slider));

        EditBox editBox = new EditBox(font, 0, 0, columnWidth, 20,
            Component.literal("Text input"));
        editBox.setValue("Type here");
        editBox.setResponder(value -> status.setMessage(Component.literal("Text: " + trim(value, 22))));
        right.addLayoutedChild(wrap(editBox), params(LayoutModifiers.maxSize(columnWidth, 20)));

        StringWidget multilineLabel = new StringWidget(0, 0, columnWidth, 14,
            Component.literal("Multiline input"), font);
        right.addLayoutedChild(wrap(multilineLabel));

        MultiLineEditBox multiLineEditBox = new MultiLineEditBox(font, 0, 0,
            columnWidth, 40, Component.literal("Notes"), Component.empty());
        multiLineEditBox.setValueListener(value -> status.setMessage(
            Component.literal("Notes: " + trim(value.replace('\n', ' '), 22))));
        right.addLayoutedChild(wrap(multiLineEditBox), params(LayoutModifiers.maxSize(columnWidth, 40)));

        controls.addLayoutedChild(left, LayoutParams.defaults().flexToSize(1));
        controls.addLayoutedChild(right, LayoutParams.defaults().flexToSize(1));
        root.addLayoutedChild(controls);

        Panel footer = panel(0, 0, contentWidth, 20, new RowLayouter(8));
        Button close = Button.builder(Component.literal("Close"), b -> onClose())
            .bounds(0, 0, 76, 20).build();
        footer.addLayoutedChild(wrap(status), params(LayoutModifiers.maxSize(
            Math.max(50, contentWidth - 84), 20)));
        footer.addLayoutedChild(wrap(close));
        root.addLayoutedChild(footer);
    }

    private static String trim(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }

    private static LayoutParams params(LayoutModifier modifier) {
        return LayoutParams.defaults().then(modifier);
    }

    private static Panel panel(int x, int y, int width, int height, Layouter layouter) {
        return new Panel(x, y, width, height, layouter);
    }

    private static NucleusWidget wrap(AbstractWidget widget) {
        return new NativeWidget(widget);
    }

    private static final class Panel extends NucleusWidget {
        private Panel(int x, int y, int width, int height,
                      Layouter layouter) {
            super(x, y, width, height, Component.empty(), layouter);
        }
    }

    private static final class NativeWidget extends NucleusWidget {
        private final AbstractWidget delegate;

        private NativeWidget(AbstractWidget delegate) {
            super(delegate.getX(), delegate.getY(), delegate.getWidth(), delegate.getHeight(), delegate.getMessage());
            this.delegate = delegate;
            syncDelegate();
        }

        @Override
        public void sizeUpdate() {
            syncDelegate();
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            syncDelegate();
            delegate.render(graphics, mouseX, mouseY, delta);
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            super.mouseMoved(mouseX, mouseY);
            delegate.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) return true;
            boolean handled = delegate.mouseClicked(mouseX, mouseY, button);
            if (handled) setFocused(true);
            return handled;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return super.mouseReleased(mouseX, mouseY, button)
                || delegate.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                || delegate.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
            return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY)
                || delegate.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return super.keyPressed(keyCode, scanCode, modifiers)
                || delegate.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return super.keyReleased(keyCode, scanCode, modifiers)
                || delegate.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char codePoint, int modifiers) {
            return super.charTyped(codePoint, modifiers)
                || delegate.charTyped(codePoint, modifiers);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            delegate.setFocused(focused);
        }

        @Override
        public boolean isFocused() {
            return delegate.isFocused() || super.isFocused();
        }

        private void syncDelegate() {
            if (delegate.getX() != getX()) delegate.setX(getX());
            if (delegate.getY() != getY()) delegate.setY(getY());
            if (delegate.getWidth() != getWidth()) delegate.setWidth(getWidth());
            if (delegate.getHeight() != getHeight()) delegate.setHeight(getHeight());
        }
    }

    private static final class DemoSlider extends AbstractSliderButton {
        private final DoubleConsumer onChange;

        private DemoSlider(int x, int y, int width, int height, DoubleConsumer onChange) {
            super(x, y, width, height, Component.literal("Slider"), 0.5);
            this.onChange = onChange;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Slider: " + Math.round(value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            onChange.accept(value);
        }
    }
}
