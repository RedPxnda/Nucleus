package com.redpxnda.nucleus.widgets.world;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom render target for world-space GUI widgets. * <p> * Renders GUI content into a TextureTarget and later renders that texture * through vanilla's entity translucent RenderType.
 */
public class WidgetRenderTarget {
    private static final AtomicInteger NEXT_TEXTURE_ID = new AtomicInteger();
    private final Minecraft minecraft;
    private final TextureManager textureManager;
    private final ResourceLocation textureLocation;
    private final RenderTargetTexture texture;
    private TextureTarget target;
    private int width;
    private int height;
    private boolean valid;
    private int resolutionScale = 8;

    public WidgetRenderTarget() {
        this.minecraft = Minecraft.getInstance();
        this.textureManager = minecraft.getTextureManager();
        int id = NEXT_TEXTURE_ID.getAndIncrement();
        this.textureLocation = ResourceLocation.fromNamespaceAndPath("nucleus", "widget_target/" + id);
        this.texture = new RenderTargetTexture();
        this.textureManager.register(textureLocation, texture);
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
        if (target != null && this.width == width && this.height == height && target.width == targetWidth && target.height == targetHeight) {
            return;
        }
        destroyTarget();
        target = new TextureTarget(targetWidth, targetHeight, true, false);
        target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.width = width;
        this.height = height;
        texture.setTextureId(target.getColorTextureId());
        valid = false;
    }

    /**
     * Call during GUI/screen rendering.
     */
    public void renderToTarget(int width, int height, RenderCallback callback) {
        resize(width, height);
        RenderTarget previousTarget = minecraft.getMainRenderTarget();
        target.bindWrite(true);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
        int renderWidth = width * resolutionScale;
        int renderHeight = height * resolutionScale;
        RenderSystem.viewport(0, 0, renderWidth, renderHeight);
        RenderSystem.disableScissor();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.backupProjectionMatrix();
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(256);
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(byteBufferBuilder);
        try {
            RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, width, height, 0.0F, 1000.0F, 21000.0F), VertexSorting.ORTHOGRAPHIC_Z);
            modelViewStack.translation(0.0F, 0.0F, -11000.0F);
            RenderSystem.applyModelViewMatrix();
            TargetGuiGraphics graphics = new TargetGuiGraphics(minecraft, bufferSource, renderWidth, renderHeight, resolutionScale);
            callback.render(graphics);
            graphics.flush();
            valid = true;
        } finally {
            bufferSource.endBatch();
            byteBufferBuilder.close();
            modelViewStack.popMatrix();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            previousTarget.bindWrite(true);
        }
    }

    /**
     * Call during world rendering.     * <p>     * Uses vanilla entity translucent rendering, including:     * * lighting     * * overlay     * * depth testing     * * blending     * * vanilla/Iris shader-pack integration
     */
    public void drawTargetToWorld(PoseStack poseStack, MultiBufferSource buffers, int width, int height, int light, int overlay, boolean debug) {
        if (!valid || target == null) {return;}
        Matrix4f pose = poseStack.last().pose();
        if (debug) {drawDebugSquare(width, height, pose);}
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityTranslucent(textureLocation));
        buffer.addVertex(pose, 0, height, 0).setColor(255, 255, 255, 255).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        buffer.addVertex(pose, width, height, 0).setColor(255, 255, 255, 255).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        buffer.addVertex(pose, width, 0, 0).setColor(255, 255, 255, 255).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        buffer.addVertex(pose, 0, 0, 0).setColor(255, 255, 255, 255).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
    }

    private static void drawDebugSquare(int width, int height, Matrix4f pose) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        float r = 1.0F;
        float g = 0.0F;
        float b = 0.0F;
        float a = 1.0F;
        buffer.addVertex(pose, 0, 0, 0).setColor(r, g, b, a);
        buffer.addVertex(pose, width, 0, 0).setColor(r, g, b, a);
        buffer.addVertex(pose, width, 0, 0).setColor(r, g, b, a);
        buffer.addVertex(pose, width, height, 0).setColor(r, g, b, a);
        buffer.addVertex(pose, width, height, 0).setColor(r, g, b, a);
        buffer.addVertex(pose, 0, height, 0).setColor(r, g, b, a);
        buffer.addVertex(pose, 0, height, 0).setColor(r, g, b, a);
        buffer.addVertex(pose, 0, 0, 0).setColor(r, g, b, a);
        float centerX = width * 0.5F;
        float centerY = height * 0.5F;
        buffer.addVertex(pose, 0, centerY, 0).setColor(0, 1, 0, 1);
        buffer.addVertex(pose, width, centerY, 0).setColor(0, 1, 0, 1);
        buffer.addVertex(pose, centerX, 0, 0).setColor(0, 1, 0, 1);
        buffer.addVertex(pose, centerX, height, 0).setColor(0, 1, 0, 1);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public boolean isValid() {return valid;}

    public int getTextureId() {return target != null ? target.getColorTextureId() : -1;}

    public void destroy() {
        destroyTarget();
        texture.setTextureId(-1);
        textureManager.release(textureLocation);
    }

    private void destroyTarget() {
        if (target != null) {
            target.destroyBuffers();
            target = null;
        }
        valid = false;
        width = 0;
        height = 0;
    }

    /**
     * Represents an existing OpenGL texture to Minecraft's TextureManager.     * <p>     * The RenderTarget owns the actual GL texture. Therefore this object     * must never delete or recreate the texture itself.
     */
    private static final class RenderTargetTexture extends AbstractTexture {
        private RenderTargetTexture() {this.id = -1;}

        private void setTextureId(int id) {this.id = id;}

        @Override
        public void load(ResourceManager resourceManager) {            /* The GL texture is owned by RenderTarget.*/ }

        @Override
        public void reset(TextureManager textureManager, ResourceManager resourceManager, ResourceLocation location, Executor executor) {            /* Never reload/recreate the RenderTarget's GL texture.*/ }

        @Override
        public void releaseId() {            /* RenderTarget owns the texture ID.*/ }

        @Override
        public void close() {            /* RenderTarget owns the texture lifetime.*/ }
    }

    @FunctionalInterface
    public interface RenderCallback {
        void render(GuiGraphics graphics);
    }
}