package com.redpxnda.nucleus.widgets.world;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

/**
 * Custom Render Target class.
 * creates a second render target, renders UI to it to be later rendered to world
 */
public class WidgetRenderTarget {

    private final Minecraft minecraft;

    private TextureTarget target;
    private int width;
    private int height;
    private boolean valid;
    private int resolutionScale = 8;

    public WidgetRenderTarget() {
        this.minecraft = Minecraft.getInstance();
    }

    public void setResolutionScale(int scale) {
        this.resolutionScale = Math.max(1, scale);
        this.valid = false;
    }

    public void resize(int width, int height) {
        width = Math.max(1, width);
        height = Math.max(1, height);
        int targetWidth = width * resolutionScale;
        int targetHeight = height * resolutionScale;

        if (target != null
            && this.width == width
            && this.height == height
            && target.width == targetWidth
            && target.height == targetHeight) {
            return;
        }
        destroy();

        target = new TextureTarget(targetWidth, targetHeight, true, false);

        target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);

        this.width = width;
        this.height = height;
        this.valid = false;
    }

    /**
     * should be called during Screen/ui rendering to avoid state issues
     */
    public void renderToTarget(
            int width,
            int height,
            RenderCallback callback
    ) {
        resize(width, height);

        RenderTarget previousTarget = minecraft.getMainRenderTarget();

        target.bindWrite(true);
        RenderSystem.clear(
                GL11.GL_COLOR_BUFFER_BIT,
                Minecraft.ON_OSX
        );
        int renderWidth = width * resolutionScale;
        int renderHeight = height * resolutionScale;
        RenderSystem.viewport(
                0,
                0,
                renderWidth,
                renderHeight
        );
        RenderSystem.disableScissor();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
        RenderSystem.disablePolygonOffset();

        RenderSystem.backupProjectionMatrix();

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();

        ByteBufferBuilder byteBufferBuilder =
                new ByteBufferBuilder(256);

        MultiBufferSource.BufferSource bufferSource =
                MultiBufferSource.immediate(byteBufferBuilder);

        try {
            RenderSystem.setProjectionMatrix(
                    new Matrix4f().setOrtho(
                            0.0F,
                            (float) width,
                            (float) height,
                            0.0F,
                            1000.0F,
                            21000.0F
                    ),
                    VertexSorting.ORTHOGRAPHIC_Z
            );
            modelViewStack.translation(0.0F, 0.0F, -11000.0F);
            RenderSystem.applyModelViewMatrix();
            TargetGuiGraphics graphics = new TargetGuiGraphics(
                    minecraft,
                    bufferSource,
                    renderWidth,
                    renderHeight,
                    resolutionScale
            );
            graphics.pose().pushPose();
            graphics.pose().scale(1, 1, 1);
            callback.render(graphics);
            //graphics.drawString(Minecraft.getInstance().font, "DEBUG", 10, 10, Color.RED.argb());
            //graphics.fill(0, 0, width, height, Color.GREEN.withAlpha(0.4f).argb());
            graphics.pose().popPose();

            /*
             * Flush while the TextureTarget is still bound.
             */
            graphics.flush();

            valid = true;
        } finally {
            bufferSource.endBatch();
            byteBufferBuilder.close();
            modelViewStack.popMatrix();
            RenderSystem.applyModelViewMatrix();

            RenderSystem.restoreProjectionMatrix();

            RenderSystem.setShaderColor(
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F
            );

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();

            previousTarget.bindWrite(true);
        }
    }

    /**
     * can be called whenever, but is designed to be rendered during World Render to allow World Widgets
     */
    public void drawTargetToWorld(
            PoseStack poseStack,
            int width,
            int height,
            boolean debug
    ) {
        if (!valid || target == null) {
            return;
        }

        Matrix4f pose = poseStack.last().pose();
        if (debug) {
            drawDebugSquare(width, height, pose);
        }
        RenderSystem.disableCull();
        RenderSystem.disableScissor();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        RenderSystem.colorMask(true, true, true, true);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(
                0,
                target.getColorTextureId()
        );

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX_COLOR
        );

        buffer.addVertex(pose, 0.0F, height, 0.0F)
                .setUv(0.0F, 0.0F)
                .setColor(255, 255, 255, 255);

        buffer.addVertex(pose, width, height, 0.0F)
                .setUv(1.0F, 0.0F)
                .setColor(255, 255, 255, 255);

        buffer.addVertex(pose, width, 0, 0.0F)
                .setUv(1.0F, 1.0F)
                .setColor(255, 255, 255, 255);

        buffer.addVertex(pose, 0.0F, 0, 0.0F)
                .setUv(0.0F, 1.0F)
                .setColor(255, 255, 255, 255);

        MeshData mesh = buffer.buildOrThrow();
        BufferUploader.drawWithShader(mesh);

        RenderSystem.enableCull();
        RenderSystem.disableScissor();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(true, true, true, true);
    }

    private static void drawDebugSquare(int width, int height, Matrix4f pose) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder debugBuffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINES,
                DefaultVertexFormat.POSITION_COLOR
        );

        // Corners:
        // TL = (0,      0)
        // TR = (width,  0)
        // BR = (width,  height)
        // BL = (0,      height)

        float r = 1.0F;
        float g = 0.0F;
        float b = 0.0F;
        float a = 1.0F;

        // Top
        debugBuffer.addVertex(pose, 0.0F, 0.0F, 0.0F)
                .setColor(r, g, b, a);
        debugBuffer.addVertex(pose, width, 0.0F, 0.0F)
                .setColor(r, g, b, a);

        // Right
        debugBuffer.addVertex(pose, width, 0.0F, 0.0F)
                .setColor(r, g, b, a);
        debugBuffer.addVertex(pose, width, height, 0.0F)
                .setColor(r, g, b, a);

        // Bottom
        debugBuffer.addVertex(pose, width, height, 0.0F)
                .setColor(r, g, b, a);
        debugBuffer.addVertex(pose, 0.0F, height, 0.0F)
                .setColor(r, g, b, a);

        // Left
        debugBuffer.addVertex(pose, 0.0F, height, 0.0F)
                .setColor(r, g, b, a);
        debugBuffer.addVertex(pose, 0.0F, 0.0F, 0.0F)
                .setColor(r, g, b, a);

        // Center horizontal
        float centerX = width * 0.5F;
        float centerY = height * 0.5F;

        debugBuffer.addVertex(pose, 0.0F, centerY, 0.0F)
                .setColor(0.0F, 1.0F, 0.0F, 1.0F);
        debugBuffer.addVertex(pose, width, centerY, 0.0F)
                .setColor(0.0F, 1.0F, 0.0F, 1.0F);

        // Center vertical
        debugBuffer.addVertex(pose, centerX, 0.0F, 0.0F)
                .setColor(0.0F, 1.0F, 0.0F, 1.0F);
        debugBuffer.addVertex(pose, centerX, height, 0.0F)
                .setColor(0.0F, 1.0F, 0.0F, 1.0F);

        MeshData debugMesh = debugBuffer.buildOrThrow();
        BufferUploader.drawWithShader(debugMesh);
    }

    public boolean isValid() {
        return valid;
    }

    public int getTextureId() {
        return target.getColorTextureId();
    }

    public void destroy() {
        if (target != null) {
            target.destroyBuffers();
            target = null;
        }

        valid = false;
        width = 0;
        height = 0;
    }

    @FunctionalInterface
    public interface RenderCallback {
        void render(GuiGraphics graphics);
    }
}