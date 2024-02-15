package com.redpxnda.nucleus.config.screen.widget;

import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.function.Consumer;

public class SelectableOptionsWidget extends ScrollableWidget {
    public final Collection<String> options;
    public final TextRenderer textRenderer;
    public final Consumer<String> onSelected;

    public SelectableOptionsWidget(TextRenderer textRenderer, Collection<String> options, Consumer<String> onSelected, int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.options = options;
        this.onSelected = onSelected;
    }

    @Override
    protected int getContentsHeight() {
        return 8 + options.size()*(textRenderer.fontHeight+1);
    }

    @Override
    protected double getDeltaYPerScroll() {
        return Screen.hasShiftDown() ? 8 : 4;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int y = getY()+4;
        for (String option : options) {
            int sectionHeight = textRenderer.fontHeight+1;
            boolean hovered = mouseY >= y-getScrollY() && mouseY < y-getScrollY()+sectionHeight && mouseX >= getX() && mouseX < getX()+getWidth();
            context.drawText(textRenderer, option, getX() + (hovered ? 4 : 2), y, hovered ? Color.WHITE.argb() : Color.TEXT_GRAY.argb(), true);
            y += sectionHeight;
        }
    }

    @Override
    protected void renderOverlay(DrawContext context) {
        if (this.overflows()) {
            int thumbHeight = 8;
            int left = getX() + width;
            int right = getX() + width + 4;
            int top = Math.max(getY(), (int)getScrollY() * (height - thumbHeight) / getMaxScrollY() + getY());
            int bottom = top + thumbHeight;
            context.fill(left, top, right, bottom, -8355712);
            context.fill(left, top, right - 1, bottom - 1, -4144960);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width+4 && mouseY < getY() + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == 0) {
            int y = getY()+2;
            for (String option : options) {
                int sectionHeight = textRenderer.fontHeight+1;
                if (mouseY >= y-getScrollY() && mouseY < y-getScrollY()+sectionHeight && mouseX >= getX() && mouseX < getX()+getWidth()) {
                    onSelected.accept(option);
                    return true;
                }
                y += sectionHeight;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
