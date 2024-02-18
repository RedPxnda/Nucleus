package com.redpxnda.nucleus.config.screen.widget;

import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.function.BiConsumer;

public class SelectableOptionsWidget<T> extends ScrollableWidget {
    public Map<String, T> options;
    public final TextRenderer textRenderer;
    public final BiConsumer<String, T> onSelected;

    public SelectableOptionsWidget(TextRenderer textRenderer, Map<String, T> options, BiConsumer<String, T> onSelected, int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.options = options;
        this.onSelected = onSelected;
    }

    @Override
    protected int getContentsHeight() {
        return 4 + options.size()*(textRenderer.fontHeight+1);
    }

    @Override
    protected double getDeltaYPerScroll() {
        return Screen.hasShiftDown() ? 8 : 4;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int y = getY()+4;
        for (String option : options.keySet()) {
            int sectionHeight = textRenderer.fontHeight+1;
            boolean hovered = mouseY >= y-getScrollY() && mouseY < y-getScrollY()+sectionHeight && mouseX >= getX() && mouseX < getX()+getWidth();
            drawScrollableText(context, textRenderer, Text.literal(option), getX(), y, getX()+width, y+sectionHeight, hovered ? Color.WHITE.argb() : Color.TEXT_GRAY.argb());
            y += sectionHeight;
        }
    }

    protected static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int left, int top, int right, int bottom, int color) {
        int i = textRenderer.getWidth(text);
        int j = (top + bottom - textRenderer.fontHeight) / 2 + 1;
        int k = right - left;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.max((double)l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = MathHelper.lerp(f, 0.0, (double)l);
            //context.enableScissor(left, top, right, bottom);
            context.drawTextWithShadow(textRenderer, text, left - (int)g, j, color);
            //context.disableScissor();
        } else {
            context.drawCenteredTextWithShadow(textRenderer, text, (left + right) / 2, j, color);
        }
    }

    @Override
    public void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
    }

    @Override
    protected void renderOverlay(DrawContext context) {
        if (this.overflows()) {
            int thumbHeight = 12;
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
            for (Map.Entry<String, T> option : options.entrySet()) {
                int sectionHeight = textRenderer.fontHeight+1;
                if (mouseY >= y-getScrollY() && mouseY < y-getScrollY()+sectionHeight && mouseX >= getX() && mouseX < getX()+getWidth()) {
                    onSelected.accept(option.getKey(), option.getValue());
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
