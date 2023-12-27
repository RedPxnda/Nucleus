package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.mixin.client.DrawContextAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class GuiDrawUtil {
    public static void fill(DrawContext graphics, float minX, float minY, float maxX, float maxY, Color color) {
        fill(graphics, minX, minY, maxX, maxY, 0, color);
    }
    public static void fill(DrawContext graphics, float minX, float minY, float maxX, float maxY, int z, Color color) {
        fill(graphics, RenderLayer.getGui(), minX, minY, maxX, maxY, z, color);
    }
    public static void fill(DrawContext graphics, RenderLayer renderType, float minX, float minY, float maxX, float maxY, int z, Color color) {
        float i;
        Matrix4f matrix4f = graphics.getMatrices().peek().getPositionMatrix();
        if (minX < maxX) {
            i = minX;
            minX = maxX;
            maxX = i;
        }
        if (minY < maxY) {
            i = minY;
            minY = maxY;
            maxY = i;
        }
        float a = color.alphaAsFloat();
        float r = color.redAsFloat();
        float g = color.greenAsFloat();
        float b = color.blueAsFloat();
        VertexConsumer vertexConsumer = graphics.getVertexConsumers().getBuffer(renderType);
        vertexConsumer.vertex(matrix4f, minX, minY, z).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, minX, maxY, z).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, maxX, maxY, z).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, maxX, minY, z).color(r, g, b, a).next();
        ((DrawContextAccessor) graphics).callTryDraw();
    }
}
