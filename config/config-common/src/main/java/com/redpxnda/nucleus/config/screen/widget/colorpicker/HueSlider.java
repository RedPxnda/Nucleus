package com.redpxnda.nucleus.config.screen.widget.colorpicker;

import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.function.Consumer;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;

public class HueSlider extends ClickableWidget {
    public static final Identifier SCROLLBAR_TEXTURE = new Identifier(MOD_ID, "textures/gui/small_scrollbars.png");

    public float value;
    public boolean dragging = false;
    public final Consumer<Float> updateListener;

    public HueSlider(int x, int y, int width, int height, Consumer<Float> updateListener) {
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
        float startY = getY();
        float endY = getY()+height-2;
        int len = Color.RAINBOW.length;
        float segmentSize = (float) width/len;
        VertexConsumer vc = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        for (int i = 0; i < len; i++) {
            Color current = Color.RAINBOW[i];
            Color next = Color.RAINBOW[(i+1) % len];

            float startX = getX()+i*segmentSize;
            float endX = getX()+(i+1)*segmentSize;

            vc.vertex(matrix4f, startX, startY, 0).color(current.r(), current.g(), current.b(), current.a()).next();
            vc.vertex(matrix4f, startX, endY, 0).color(current.r(), current.g(), current.b(), current.a()).next();
            vc.vertex(matrix4f, endX, endY, 0).color(next.r(), next.g(), next.b(), next.a()).next();
            vc.vertex(matrix4f, endX, startY, 0).color(next.r(), next.g(), next.b(), next.a()).next();
        }
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
