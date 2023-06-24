package com.redpxnda.nucleus.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.redpxnda.nucleus.impl.ShaderRegistry;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.registry.particles.EmittingParticle;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.BiFunction;

import static com.redpxnda.nucleus.registry.NucleusRegistries.loc;

public class RenderUtil {
    public static ShaderInstance alphaAnimationShader;
    public static RenderType alphaAnimation = RenderType.create("translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 0x200000, true, true, RenderType.translucentState(new RenderStateShard.ShaderStateShard(() -> alphaAnimationShader)));

    public static void init() {
        ShaderRegistry.register(loc("rendertype_alpha_animation"), DefaultVertexFormat.BLOCK, i -> alphaAnimationShader = i);
        ParticleProviderRegistry.register(NucleusRegistries.emittingParticle, new EmittingParticle.Provider());
    }

    public static float[] lerpColors(long gameTime, int duration, float[][] colors) {
        if (colors.length < 1) return new float[] { 1, 1, 1 };
        int time = (int) (gameTime % duration*2);
        if (time >= duration) time = time-duration;

        int colorIndex = (int) Math.floor((time/(float)duration)*colors.length);
        float progress = ((time/(float)duration)*colors.length)-colorIndex;

        boolean tooLarge = colorIndex+1 >= colors.length;
        return new float[] {
                Mth.lerp(progress, colors[colorIndex][0], colors[tooLarge ? 0 : colorIndex+1][0])/255f,
                Mth.lerp(progress, colors[colorIndex][1], colors[tooLarge ? 0 : colorIndex+1][1])/255f,
                Mth.lerp(progress, colors[colorIndex][2], colors[tooLarge ? 0 : colorIndex+1][2])/255f
        };
    }

    public static void rotateVectors(Vector3f[] vectors, Quaternionf quaternion) {
        for (Vector3f vec : vectors) {
            vec.rotate(quaternion);
        }
    }
    public static void translateVectors(Vector3f[] vectors, float x, float y, float z) {
        for (Vector3f vec : vectors) {
            vec.add(x, y, z);
        }
    }
    public static void scaleVectors(Vector3f[] vectors, float amnt) {
        for (Vector3f vec : vectors) {
            vec.mul(amnt);
        }
    }

    public static void renderNameTag(BlockEntityRendererProvider.Context context, boolean increaseHeight, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.translate(0.5, increaseHeight ? 3.35 : 2.5, 0.5);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = poseStack.last().pose();
        float g = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int k = (int)(g * 255.0f) << 24;
        Font font = context.getFont();
        float h = -font.width(component) / 2f;
        //font.drawInBatch(component, h, 0, 0x20FFFFFF, false, matrix4f, multiBufferSource, true, k, i);
        font.drawInBatch(component, h, 0, -1, false, matrix4f, multiBufferSource, false, k, i);
        poseStack.popPose();
    }

    public static void addDoubleQuad(PoseStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float xOffset, float u0, float u1, float v0, float v1, int light) {
        addQuad(false, stack.last().pose(), vc, red, green, blue, alpha, x, y, z, xOffset, u0, u1, v0, v1, light);
        addQuad(true, stack.last().pose(), vc, red, green, blue, alpha, x, y, z, xOffset, u0, u1, v0, v1, light);
    }

    public static void addQuad(boolean reverse, Matrix4f matrix4f, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float xOffset, float u0, float u1, float v0, float v1, int light) {
        if (reverse)
            addQuad((f, bl) -> bl ? f : -f+xOffset, (f, bl) -> bl ? -f : f+xOffset, matrix4f, vc, red, green, blue, alpha, x, y, z, u1, u0, v0, v1, light);
        else
            addQuad((f, bl) -> bl ? f : f+xOffset, (f, bl) -> bl ? -f : -f+xOffset, matrix4f, vc, red, green, blue, alpha, x, y, z, u0, u1, v0, v1, light);
    }
    public static void addQuad(BiFunction<Float, Boolean, Float> primary, BiFunction<Float, Boolean, Float> secondary, Matrix4f matrix4f, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u0, float u1, float v0, float v1, int light) {
        addVertex(matrix4f, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);
    }
    public static void addDoubleQuad(BiFunction<Float, Boolean, Float> primary, BiFunction<Float, Boolean, Float> secondary, Matrix4f matrix4f, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u0, float u1, float v0, float v1, int light) {
        addVertex(matrix4f, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);

        addVertex(matrix4f, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(matrix4f, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
    }

    public static void addParticleQuad(Vector3f[] vertices, VertexConsumer vc, float red, float green, float blue, float alpha, float u0, float u1, float v0, float v1, int light) {
        addParticleVertex(vc, red, green, blue, alpha, vertices[0].x(), vertices[0].y(), vertices[0].z(), u0, v0, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[1].x(), vertices[1].y(), vertices[1].z(), u0, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[2].x(), vertices[2].y(), vertices[2].z(), u1, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[3].x(), vertices[3].y(), vertices[3].z(), u1, v0, light);
    }

    public static void addDoubleParticleQuad(Vector3f[] vertices, VertexConsumer vc, float red, float green, float blue, float alpha, float u0, float u1, float v0, float v1, int light) {
        addParticleVertex(vc, red, green, blue, alpha, vertices[0].x(), vertices[0].y(), vertices[0].z(), u0, v0, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[1].x(), vertices[1].y(), vertices[1].z(), u0, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[2].x(), vertices[2].y(), vertices[2].z(), u1, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[3].x(), vertices[3].y(), vertices[3].z(), u1, v0, light);
        // render in reverse to well... reverse
        addParticleVertex(vc, red, green, blue, alpha, vertices[3].x(), vertices[3].y(), vertices[3].z(), u1, v0, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[2].x(), vertices[2].y(), vertices[2].z(), u1, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[1].x(), vertices[1].y(), vertices[1].z(), u0, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[0].x(), vertices[0].y(), vertices[0].z(), u0, v0, light);
    }

    public static void addParticleVertex(VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.vertex(x, y, z).uv(u, v).color(red, green, blue, alpha).uv2(light).endVertex();
    }

    public static void addVertex(Matrix4f matrix4f, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.vertex(matrix4f, x, y, z).color(red, green, blue, alpha).uv(u, v).uv2(light).normal(1, 0, 0).endVertex();
    }
}
