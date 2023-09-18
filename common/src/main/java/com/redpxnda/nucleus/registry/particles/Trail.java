package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.client.Rendering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;

import java.util.List;

public class Trail {
    public float width = 0.2f;
    public int saveInterval = 1;
    public int maxLength = 20;
    public boolean emissive = false;
    public float red = 1;
    public float green = 1;
    public float blue = 1;
    public float alpha = 1;

    public Trail setWidth(float width) {
        this.width = width;
        return this;
    }

    public Trail setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public Trail setEmissive(boolean emissive) {
        this.emissive = emissive;
        return this;
    }

    public Trail setSaveInterval(int interval) {
        this.saveInterval = interval;
        return this;
    }

    public Trail setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        return this;
    }

    public Trail setRed(float red) {
        this.red = red;
        return this;
    }

    public Trail setGreen(float green) {
        this.green = green;
        return this;
    }

    public Trail setBlue(float blue) {
        this.blue = blue;
        return this;
    }

    public Trail setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    @Environment(EnvType.CLIENT)
    public void render(VertexConsumerProvider bufferSource, MatrixStack stack, float pt, List<Vector3f> oldPositions, Vector3f oldestPosition, Vector3f currentPosition) {
        VertexConsumer vc = bufferSource.getBuffer(Rendering.transparentTriangleStrip);
        int length = oldPositions.size();
        float width = this.width/length;
        for (int i = 0; i < length; i++) {
            Vector3f past = i == 0 ? oldestPosition : oldPositions.get(i-1);
            Vector3f current = oldPositions.get(i);
            Vector3f next = i == length-1 ? currentPosition : oldPositions.get(i+1);

            next = new Vector3f(current).lerp(next, pt);
            current = new Vector3f(past).lerp(current, pt);

            float offset = width*(i);
            float nextOffset = width*(i+1);

            int light = emissive ? LightmapTextureManager.MAX_LIGHT_COORDINATE : LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE;

            vc.vertex(stack.peek().getPositionMatrix(), current.x, current.y+offset, current.z).color(red, green, blue, 0f).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), current.x, current.y-offset, current.z).color(red, green, blue, 0f).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), next.x, next.y-nextOffset, next.z).color(red, green, blue, 0f).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), current.x, current.y+offset, current.z).color(red, green, blue, alpha).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), next.x, next.y-nextOffset, next.z).color(red, green, blue, alpha).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), next.x, next.y+nextOffset, next.z).color(red, green, blue, alpha).light(light).next();
        }
    }
}
