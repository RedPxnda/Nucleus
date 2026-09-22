package com.redpxnda.nucleus.widgets.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public final class CubeRenderHelper {

    private CubeRenderHelper() {}

    public static void render(
            PoseStack poseStack,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            int color
    ) {
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR
        );

        // Front
        vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);
        vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
        vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);
        vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);

        // Back
        vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);
        vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
        vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);
        vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);

        // Left
        vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
        vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);
        vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
        vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);

        // Right
        vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
        vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);
        vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
        vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);

        // Top
        vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
        vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);
        vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
        vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);

        // Bottom
        vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
        vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);
        vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
        vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableBlend();
    }

    private static void vertex(
            BufferBuilder buffer,
            Matrix4f matrix,
            float x,
            float y,
            float z,
            float r,
            float g,
            float b,
            float a
    ) {
        buffer.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a);
    }
}