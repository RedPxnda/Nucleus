package com.redpxnda.nucleus.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.redpxnda.nucleus.impl.ShaderRegistry;
import com.redpxnda.nucleus.mixin.ClientLevelAccessor;
import com.redpxnda.nucleus.mixin.ParticleEngineAccessor;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.registry.particles.*;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.BiFunction;

import static com.redpxnda.nucleus.Nucleus.loc;

public class RenderUtil {
    public static final Vector3f[][] CUBE = {
            // TOP
            { new Vector3f(1, 1, -1), new Vector3f(1, 1, 1), new Vector3f(-1, 1, 1), new Vector3f(-1, 1, -1) },

            // BOTTOM
            { new Vector3f(-1, -1, -1), new Vector3f(-1, -1, 1), new Vector3f(1, -1, 1), new Vector3f(1, -1, -1) },

            // FRONT
            { new Vector3f(-1, -1, 1), new Vector3f(-1, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, -1, 1) },

            // BACK
            { new Vector3f(1, -1, -1), new Vector3f(1, 1, -1), new Vector3f(-1, 1, -1), new Vector3f(-1, -1, -1) },

            // LEFT
            { new Vector3f(-1, -1, -1), new Vector3f(-1, 1, -1), new Vector3f(-1, 1, 1), new Vector3f(-1, -1, 1) },

            // RIGHT
            { new Vector3f(1, -1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, -1), new Vector3f(1, -1, -1) }};
    public static final Vector3f[] QUAD = {
            new Vector3f(-1, -1, 0), new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0)
    };

    public static ShaderInstance alphaAnimationShader;
    public static RenderType alphaAnimation = RenderType.create("alpha_animation_translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 0x200000, true, true, RenderType.translucentState(new RenderStateShard.ShaderStateShard(() -> alphaAnimationShader)));
    public static final ParticleRenderType blockSheetTranslucent = new ParticleRenderType(){
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        public String toString() {
            return "BLOCK_SHEET_TRANSLUCENT";
        }
    };

    public static void init() {
        ShaderRegistry.register(loc("rendertype_alpha_animation"), DefaultVertexFormat.BLOCK, i -> alphaAnimationShader = i);
        ParticleProviderRegistry.register(NucleusRegistries.emittingParticle, new EmitterParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.mimicParticle, new MimicParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.controllerParticle, new ControllerParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.cubeParticle, new CubeParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.blockChunkParticle, new BlockChunkParticle.Provider());
    }

    public static <T extends ParticleOptions> Particle createParticle(ClientLevel level, T options, double x, double y, double z, double xs, double ys, double zs) {
        ParticleProvider<T> provider = (ParticleProvider<T>) ((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getProviders()
                .get(BuiltInRegistries.PARTICLE_TYPE.getId(options.getType()));
        if (provider == null) return null;
        return provider.createParticle(options, level, x, y, z, xs, ys, zs);
    }
    public static Particle addParticleToWorld(ClientLevel level, ParticleOptions options, boolean overrideLimiter, boolean canSpawnOnMinimal, double x, double y, double z, double xs, double ys, double zs) {
        try {
            return ((ClientLevelAccessor) level).getLevelRenderer().addParticleInternal(
                    options, overrideLimiter, canSpawnOnMinimal,
                    x, y, z, xs, ys, zs);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Exception while adding particle");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being added");
            crashReportCategory.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType()));
            crashReportCategory.setDetail("Parameters", options.writeToString());
            crashReportCategory.setDetail("Position", () -> CrashReportCategory.formatLocation(level, x, y, z));
            throw new ReportedException(crashReport);
        }
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
        font.drawInBatch(component, h, 0, -1, false, matrix4f, multiBufferSource, Font.DisplayMode.NORMAL, k, i);
        poseStack.popPose();
    }

    public static void addDoubleQuad(PoseStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float xOffset, float u0, float u1, float v0, float v1, int light) {
        addQuad(false, stack, vc, red, green, blue, alpha, x, y, z, xOffset, u0, u1, v0, v1, light);
        addQuad(true, stack, vc, red, green, blue, alpha, x, y, z, xOffset, u0, u1, v0, v1, light);
    }

    public static void addQuad(boolean reverse, PoseStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float xOffset, float u0, float u1, float v0, float v1, int light) {
        if (reverse)
            addQuad((f, bl) -> bl ? f : -f+xOffset, (f, bl) -> bl ? -f : f+xOffset, stack, vc, red, green, blue, alpha, x, y, z, u1, u0, v0, v1, light);
        else
            addQuad((f, bl) -> bl ? f : f+xOffset, (f, bl) -> bl ? -f : -f+xOffset, stack, vc, red, green, blue, alpha, x, y, z, u0, u1, v0, v1, light);
    }
    public static void addQuad(BiFunction<Float, Boolean, Float> primary, BiFunction<Float, Boolean, Float> secondary, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u0, float u1, float v0, float v1, int light) {
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);
    }
    public static void addQuad(Vector3f[] vertices, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x()*xMult, vertices[0].y()*yMult, vertices[0].z()*zMult, u0, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x()*xMult, vertices[1].y()*yMult, vertices[1].z()*zMult, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x()*xMult, vertices[2].y()*yMult, vertices[2].z()*zMult, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x()*xMult, vertices[3].y()*yMult, vertices[3].z()*zMult, u1, v0, light);
    }
    public static void addDoubleQuad(BiFunction<Float, Boolean, Float> primary, BiFunction<Float, Boolean, Float> secondary, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u0, float u1, float v0, float v1, int light) {
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);

        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
    }

    public static void addParticleQuad(Vector3f[] vertices, VertexConsumer vc, float red, float green, float blue, float alpha, float u0, float u1, float v0, float v1, int light) {
        addParticleVertex(vc, red, green, blue, alpha, vertices[0].x(), vertices[0].y(), vertices[0].z(), u0, v0, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[1].x(), vertices[1].y(), vertices[1].z(), u0, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[2].x(), vertices[2].y(), vertices[2].z(), u1, v1, light);
        addParticleVertex(vc, red, green, blue, alpha, vertices[3].x(), vertices[3].y(), vertices[3].z(), u1, v0, light);
    }
    public static void addParticleQuad(Vector3f[] vertices, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x()*xMult, vertices[0].y()*yMult, vertices[0].z()*zMult, u0, v0, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x()*xMult, vertices[1].y()*yMult, vertices[1].z()*zMult, u0, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x()*xMult, vertices[2].y()*yMult, vertices[2].z()*zMult, u1, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x()*xMult, vertices[3].y()*yMult, vertices[3].z()*zMult, u1, v0, light);
    }
    public static void addDoubleParticleQuad(Vector3f[] vertices, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x()*xMult, vertices[0].y()*yMult, vertices[0].z()*zMult, u0, v0, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x()*xMult, vertices[1].y()*yMult, vertices[1].z()*zMult, u0, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x()*xMult, vertices[2].y()*yMult, vertices[2].z()*zMult, u1, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x()*xMult, vertices[3].y()*yMult, vertices[3].z()*zMult, u1, v0, light);
        //reverse
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x()*xMult, vertices[3].y()*yMult, vertices[3].z()*zMult, u1, v0, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x()*xMult, vertices[2].y()*yMult, vertices[2].z()*zMult, u1, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x()*xMult, vertices[1].y()*yMult, vertices[1].z()*zMult, u0, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x()*xMult, vertices[0].y()*yMult, vertices[0].z()*zMult, u0, v0, light);
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
    public static void addParticleVertex(PoseStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.vertex(stack.last().pose(), x, y, z).uv(u, v).color(red, green, blue, alpha).uv2(light).endVertex();
    }

    public static void addVertex(PoseStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).uv2(light).normal(stack.last().normal(), 1, 0, 0).endVertex();
    }
}
