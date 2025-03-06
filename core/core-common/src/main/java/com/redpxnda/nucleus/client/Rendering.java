package com.redpxnda.nucleus.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.redpxnda.nucleus.event.RenderEvents;
import com.redpxnda.nucleus.impl.MiscAbstraction;
import com.redpxnda.nucleus.impl.ShaderRegistry;
import com.redpxnda.nucleus.mixin.client.ClientWorldAccessor;
import com.redpxnda.nucleus.registry.effect.RenderingMobEffect;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.function.BiFunction;

import static com.redpxnda.nucleus.Nucleus.loc;
import static net.minecraft.client.renderer.RenderStateShard.*;

@Environment(EnvType.CLIENT)
public class Rendering {
    public static final Vector3f[][] CUBE = {
            // TOP
            {new Vector3f(1, 1, -1), new Vector3f(1, 1, 1), new Vector3f(-1, 1, 1), new Vector3f(-1, 1, -1)},
            // BOTTOM
            {new Vector3f(-1, -1, -1), new Vector3f(-1, -1, 1), new Vector3f(1, -1, 1), new Vector3f(1, -1, -1)},
            // FRONT
            {new Vector3f(-1, -1, 1), new Vector3f(-1, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, -1, 1)},
            // BACK
            {new Vector3f(1, -1, -1), new Vector3f(1, 1, -1), new Vector3f(-1, 1, -1), new Vector3f(-1, -1, -1)},
            // LEFT
            {new Vector3f(-1, -1, -1), new Vector3f(-1, 1, -1), new Vector3f(-1, 1, 1), new Vector3f(-1, -1, 1)},
            // RIGHT
            {new Vector3f(1, -1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, -1), new Vector3f(1, -1, -1)}};
    public static final Vector3f[] QUAD = {
            new Vector3f(-1, -1, 0), new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0)
    };

    public static ShaderInstance alphaAnimationShader;
    //public static ShaderInstance trailShader;

    public static RenderType transparentTriangleStrip = RenderType.create(
            "nucleus_triangle_strip", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLE_STRIP,
            256, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LEASH_SHADER).setTextureState(NO_TEXTURE)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    public static RenderType alphaAnimation = RenderType.create(
            "nucleus_alpha_animation_translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
            0x200000, true, true, RenderType.translucentState(new RenderStateShard.ShaderStateShard(() -> alphaAnimationShader)));

    /*public static final RenderType.CompositeRenderType trail = RenderType.create(
            "nucleus_trail", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES,
            256, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> trailShader))
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(4))).setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setCullState(NO_CULL).createCompositeState(false));*/

    public static final ParticleRenderType blockSheetTranslucent = new ParticleRenderType() {

        @Nullable
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public String toString() {
            return "BLOCK_SHEET_TRANSLUCENT";
        }
    };

    public static void init() {
        ShaderRegistry.register(loc("rendertype_alpha_animation"), DefaultVertexFormat.BLOCK, i -> alphaAnimationShader = i);
        //ShaderRegistry.register(loc("rendertype_trail"), DefaultVertexFormat.POSITION_COLOR_NORMAL, i -> trailShader = i);

        RenderEvents.LIVING_ENTITY_RENDER.register((stage, model, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage != RenderEvents.EntityRenderStage.PRE) return EventResult.pass();
            for (Map.Entry<Holder<MobEffect>, MobEffectInstance> entry : entity.getActiveEffectsMap().entrySet()) {
                MobEffectInstance instance = entry.getValue();
                Holder<MobEffect> effect = entry.getKey();
                if (effect.value() instanceof RenderingMobEffect rendering && (instance.getDuration() > 0 || instance.isInfiniteDuration())) {
                    boolean result = rendering.renderPre(instance, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight);
                    if (result)
                        return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
        RenderEvents.LIVING_ENTITY_RENDER.register((stage, model, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage != RenderEvents.EntityRenderStage.POST) return EventResult.pass();
            entity.getActiveEffectsMap().forEach((effect, instance) -> {
                if (effect.value() instanceof RenderingMobEffect rendering && (instance.getDuration() > 0 || instance.isInfiniteDuration())) {
                    rendering.renderPost(instance, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight);
                }
            });
            return EventResult.pass();
        });
        RenderEvents.HUD_RENDER_PRE.register((minecraft, graphics, partialTick) -> {
            for (Map.Entry<Holder<MobEffect>, MobEffectInstance> entry : minecraft.player.getActiveEffectsMap().entrySet()) {
                if (entry.getKey() instanceof RenderingMobEffect rendering) {
                    boolean result = rendering.renderHud(entry.getValue(), minecraft, graphics, partialTick);
                    if (result)
                        return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
    }

    public static <T extends ParticleOptions> Particle createParticle(ClientLevel level, T options, double x, double y, double z, double xs, double ys, double zs) {
        ParticleProvider<T> provider = (ParticleProvider<T>) MiscAbstraction.getProviderFromType(options.getType());
        if (provider == null) return null;
        return provider.createParticle(options, level, x, y, z, xs, ys, zs);
    }

    public static Particle addParticleToWorld(ClientLevel level, ParticleOptions options, boolean overrideLimiter, boolean canSpawnOnMinimal, double x, double y, double z, double xs, double ys, double zs) {
        try {
            return ((ClientWorldAccessor) level).getWorldRenderer().addParticleInternal(
                    options, overrideLimiter, canSpawnOnMinimal,
                    x, y, z, xs, ys, zs);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Exception while adding particle");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being added");
            crashReportCategory.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType()));
            crashReportCategory.setDetail("Parameters", options.toString());
            crashReportCategory.setDetail("Position", () -> CrashReportCategory.formatLocation(level, x, y, z));
            throw new ReportedException(crashReport);
        }
    }

    public static long getGameTime() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null ? -100 : mc.level.getGameTime();
    }

    public static double getGameAndDeltaTime() {
        Minecraft mc = Minecraft.getInstance();
        mc.getFrameTimeNs();
        return mc.level == null ? -100 : mc.getTimer().getGameTimeDeltaTicks();
    }

    public static double getGameAndPartialTime() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null ? -100 : mc.getTimer().getGameTimeDeltaPartialTick(true);
    }

    public static float[] lerpColors(long gameTime, int duration, float[][] colors) {
        if (colors.length < 1) return new float[]{1, 1, 1};
        int time = (int) (gameTime % duration * 2);
        if (time >= duration) time = time - duration;

        int colorIndex = (int) Math.floor((time / (float) duration) * colors.length);
        float progress = ((time / (float) duration) * colors.length) - colorIndex;

        boolean tooLarge = colorIndex + 1 >= colors.length;
        return new float[]{
                Mth.lerp(progress, colors[colorIndex][0], colors[tooLarge ? 0 : colorIndex + 1][0]) / 255f,
                Mth.lerp(progress, colors[colorIndex][1], colors[tooLarge ? 0 : colorIndex + 1][1]) / 255f,
                Mth.lerp(progress, colors[colorIndex][2], colors[tooLarge ? 0 : colorIndex + 1][2]) / 255f
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
        int k = (int) (g * 255.0f) << 24;
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
            addQuad((f, bl) -> bl ? f : -f + xOffset, (f, bl) -> bl ? -f : f + xOffset, stack, vc, red, green, blue, alpha, x, y, z, u1, u0, v0, v1, light);
        else
            addQuad((f, bl) -> bl ? f : f + xOffset, (f, bl) -> bl ? -f : -f + xOffset, stack, vc, red, green, blue, alpha, x, y, z, u0, u1, v0, v1, light);
    }

    public static void addQuad(BiFunction<Float, Boolean, Float> primary, BiFunction<Float, Boolean, Float> secondary, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u0, float u1, float v0, float v1, int light) {
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);
    }

    public static void addQuad(Vector3f[] vertices, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x() * xMult, vertices[0].y() * yMult, vertices[0].z() * zMult, u0, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x() * xMult, vertices[1].y() * yMult, vertices[1].z() * zMult, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x() * xMult, vertices[2].y() * yMult, vertices[2].z() * zMult, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x() * xMult, vertices[3].y() * yMult, vertices[3].z() * zMult, u1, v0, light);
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
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x() * xMult, vertices[0].y() * yMult, vertices[0].z() * zMult, u0, v0, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x() * xMult, vertices[1].y() * yMult, vertices[1].z() * zMult, u0, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x() * xMult, vertices[2].y() * yMult, vertices[2].z() * zMult, u1, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x() * xMult, vertices[3].y() * yMult, vertices[3].z() * zMult, u1, v0, light);
    }

    public static void addDoubleParticleQuad(Vector3f[] vertices, PoseStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x() * xMult, vertices[0].y() * yMult, vertices[0].z() * zMult, u0, v0, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x() * xMult, vertices[1].y() * yMult, vertices[1].z() * zMult, u0, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x() * xMult, vertices[2].y() * yMult, vertices[2].z() * zMult, u1, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x() * xMult, vertices[3].y() * yMult, vertices[3].z() * zMult, u1, v0, light);
        //reverse
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x() * xMult, vertices[3].y() * yMult, vertices[3].z() * zMult, u1, v0, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x() * xMult, vertices[2].y() * yMult, vertices[2].z() * zMult, u1, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x() * xMult, vertices[1].y() * yMult, vertices[1].z() * zMult, u0, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x() * xMult, vertices[0].y() * yMult, vertices[0].z() * zMult, u0, v0, light);
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
        vc.addVertex(x, y, z).setUv(u, v).setColor(red, green, blue, 1.0f).setLight(light);
    }

    public static void addParticleVertex(PoseStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.addVertex(stack.last().pose(),x, y, z).setUv(u, v).setColor(red, green, blue, 1.0f).setLight(light);
    }

    public static void addVertex(PoseStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.addVertex(stack.last().pose(),x, y, z).setUv(u, v).setColor(red, green, blue, 1.0f).setLight(light).setNormal(stack.last(), 1.0f, 0.0f, 0.0f);
    }
}
