package com.redpxnda.nucleus.config.screen.widget.colorpicker;

import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class ColorGrid extends ClickableWidget {
    public float saturation = 1;
    public float lightness = 1;
    public Color hueColor = Color.RED.copy();
    public Color color = Color.RED.copy();
    public boolean dragging = false;
    public final Consumer<Color> updateListener;

    public ColorGrid(int x, int y, int width, int height, Consumer<Color> updateListener) {
        super(x, y, width, height, Text.empty());
        this.updateListener = updateListener;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        dragging = true;
        saturation = MathHelper.clamp((float) (mouseX-getX())/width, 0f, 1f);
        lightness = MathHelper.clamp(1f - (float)(mouseY-getY())/height, 0f, 1f);
        update();
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        dragging = false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        saturation = MathHelper.clamp((float) (mouseX-getX())/width, 0f, 1f);
        lightness = MathHelper.clamp(1f - (float)(mouseY-getY())/height, 0f, 1f);
        update();
    }

    protected boolean isDragging(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY) || dragging;
    }

    protected boolean isReleasing(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY) || dragging;
    }

    protected Color calculateColor() {
        color = Color.WHITE.copy();
        color.lerp(saturation, hueColor);
        color.lerp(1-lightness, Color.BLACK);
        return color;
    }

    protected void update() {
        calculateColor();
        updateListener.accept(color);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        VertexConsumer vc = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        float iLightness = 1-lightness;
        vc.vertex(matrix4f, getX() + saturation*width - 3, getY() + iLightness*height - 3, 1).color(Color.WHITE.r(), Color.WHITE.g(), Color.WHITE.b(), Color.WHITE.a()).next();
        vc.vertex(matrix4f, getX() + saturation*width - 3, getY() + iLightness*height + 3, 1).color(Color.WHITE.r(), Color.WHITE.g(), Color.WHITE.b(), Color.WHITE.a()).next();
        vc.vertex(matrix4f, getX() + saturation*width + 3, getY() + iLightness*height + 3, 1).color(Color.WHITE.r(), Color.WHITE.g(), Color.WHITE.b(), Color.WHITE.a()).next();
        vc.vertex(matrix4f, getX() + saturation*width + 3, getY() + iLightness*height - 3, 1).color(Color.WHITE.r(), Color.WHITE.g(), Color.WHITE.b(), Color.WHITE.a()).next();

        vc.vertex(matrix4f, getX() + saturation*width - 2, getY() + iLightness*height - 2, 1).color(color.r(), color.g(), color.b(), color.a()).next();
        vc.vertex(matrix4f, getX() + saturation*width - 2, getY() + iLightness*height + 2, 1).color(color.r(), color.g(), color.b(), color.a()).next();
        vc.vertex(matrix4f, getX() + saturation*width + 2, getY() + iLightness*height + 2, 1).color(color.r(), color.g(), color.b(), color.a()).next();
        vc.vertex(matrix4f, getX() + saturation*width + 2, getY() + iLightness*height - 2, 1).color(color.r(), color.g(), color.b(), color.a()).next();

        for (int i = 0; i < height; i++) {
            float delt = (float) i/height;

            Color left = Color.WHITE.copy();
            left.lerp(delt, Color.BLACK);

            Color right = hueColor.copy();
            right.lerp(delt, Color.BLACK);

            vc.vertex(matrix4f, getX(), getY()+i, 0).color(left.r(), left.g(), left.b(), left.a()).next();
            vc.vertex(matrix4f, getX(), getY()+i+1, 0).color(left.r(), left.g(), left.b(), left.a()).next();
            vc.vertex(matrix4f, getX()+width, getY()+i+1, 0).color(right.r(), right.g(), right.b(), right.a()).next();
            vc.vertex(matrix4f, getX()+width, getY()+i, 0).color(right.r(), right.g(), right.b(), right.a()).next();
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
