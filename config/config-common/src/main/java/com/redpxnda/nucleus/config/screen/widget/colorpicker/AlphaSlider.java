package com.redpxnda.nucleus.config.screen.widget.colorpicker;

import com.redpxnda.nucleus.math.MathUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class AlphaSlider extends ClickableWidget {
    public static final Identifier SCROLLBAR_TEXTURE = HueSlider.SCROLLBAR_TEXTURE;

    public float value = 1f;
    public boolean dragging = false;
    public final Consumer<Float> updateListener;

    public AlphaSlider(int x, int y, int width, int height, Consumer<Float> updateListener) {
        super(x, y, width, height, Text.empty());
        this.updateListener = updateListener;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        dragging = true;
        value = MathUtil.clamp((float) (mouseX-getX())/width, 0f, 1f);
        update();
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        dragging = false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        value = MathUtil.clamp((float) (mouseX-getX())/width, 0f, 1f);
        update();
    }

    protected boolean isDragging(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY) || dragging;
    }

    protected boolean isReleasing(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY) || dragging;
    }

    protected void update() {
        updateListener.accept(value);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        renderScrollbar(context, mouseX, mouseY, delta);
        VertexConsumer vc = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        vc.vertex(matrix4f, getX(), getY(), 0).color(1f, 1f, 1f, 0f).next();
        vc.vertex(matrix4f, getX(), getY()+height-1, 0).color(1f, 1f, 1f, 0f).next();
        vc.vertex(matrix4f, getX()+width, getY()+height-1, 0).color(1f, 1f, 1f, 1f).next();
        vc.vertex(matrix4f, getX()+width, getY(), 0).color(1f, 1f, 1f, 1f).next();
    }

    protected void renderScrollbar(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 1);
        context.drawTexture(SCROLLBAR_TEXTURE, getX() - 1 + (int) (value*(width-getScrollbarWidth())), getY()-1, 6, 8, isHovered() ? 6 : 0, 0, 6, 8, 12, 8);
        context.getMatrices().pop();
    }

    protected int getScrollbarWidth() {
        return 4;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
