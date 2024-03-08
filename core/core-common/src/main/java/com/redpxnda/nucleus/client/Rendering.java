package com.redpxnda.nucleus.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.redpxnda.nucleus.event.RenderEvents;
import com.redpxnda.nucleus.impl.MiscAbstraction;
import com.redpxnda.nucleus.impl.ShaderRegistry;
import com.redpxnda.nucleus.mixin.client.ClientWorldAccessor;
import com.redpxnda.nucleus.registry.effect.RenderingMobEffect;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.BiFunction;

import static com.redpxnda.nucleus.Nucleus.loc;
import static net.minecraft.client.render.RenderPhase.*;

@Environment(EnvType.CLIENT)
public class Rendering {
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

    public static ShaderProgram alphaAnimationShader;
    public static ShaderProgram trailShader;

    public static RenderLayer transparentTriangleStrip = RenderLayer.of(
            "nucleus_triangle_strip", VertexFormats.POSITION_COLOR_LIGHT, VertexFormat.DrawMode.TRIANGLE_STRIP,
            256, RenderLayer.MultiPhaseParameters.builder().program(LEASH_PROGRAM).texture(NO_TEXTURE)
            .transparency(TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP)
            .build(false));

    public static RenderLayer alphaAnimation = RenderLayer.of(
            "nucleus_alpha_animation_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS,
            0x200000, true, true, RenderLayer.of(new RenderPhase.ShaderProgram(() -> alphaAnimationShader)));

    public static final RenderLayer.MultiPhase trail = RenderLayer.of(
            "nucleus_trail", VertexFormats.LINES, VertexFormat.DrawMode.LINES,
            256, RenderLayer.MultiPhaseParameters.builder().program(new RenderPhase.ShaderProgram(() -> trailShader))
            .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(4))).layering(VIEW_OFFSET_Z_LAYERING)
            .transparency(TRANSLUCENT_TRANSPARENCY).target(ITEM_ENTITY_TARGET).writeMaskState(ALL_MASK)
            .cull(DISABLE_CULLING).build(false));

    public static final ParticleTextureSheet blockSheetTranslucent = new ParticleTextureSheet(){
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator tesselator) {
            tesselator.draw();
        }

        public String toString() {
            return "BLOCK_SHEET_TRANSLUCENT";
        }
    };

    public static void init() {
        ShaderRegistry.register(loc("rendertype_alpha_animation"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, i -> alphaAnimationShader = i);
        ShaderRegistry.register(loc("rendertype_trail"), VertexFormats.LINES, i -> trailShader = i);

        RenderEvents.LIVING_ENTITY_RENDER.register((stage, model, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage != RenderEvents.EntityRenderStage.PRE) return EventResult.pass();
            for (Map.Entry<StatusEffect, StatusEffectInstance> entry : entity.getActiveStatusEffects().entrySet()) {
                StatusEffectInstance instance = entry.getValue();
                StatusEffect effect = entry.getKey();
                if (effect instanceof RenderingMobEffect rendering && (instance.getDuration() > 0 || instance.isInfinite())) {
                    boolean result = rendering.renderPre(instance, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight);
                    if (result)
                        return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
        RenderEvents.LIVING_ENTITY_RENDER.register((stage, model, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage != RenderEvents.EntityRenderStage.POST) return EventResult.pass();
            entity.getActiveStatusEffects().forEach((effect, instance) -> {
                if (effect instanceof RenderingMobEffect rendering && (instance.getDuration() > 0 || instance.isInfinite())) {
                    rendering.renderPost(instance, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight);
                }
            });
            return EventResult.pass();
        });
        RenderEvents.HUD_RENDER_PRE.register((minecraft, graphics, partialTick) -> {
            for (Map.Entry<StatusEffect, StatusEffectInstance> entry : minecraft.player.getActiveStatusEffects().entrySet()) {
                if (entry.getKey() instanceof RenderingMobEffect rendering) {
                    boolean result = rendering.renderHud(entry.getValue(), minecraft, graphics, partialTick);
                    if (result)
                        return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
    }

    public static <T extends ParticleEffect> Particle createParticle(ClientWorld level, T options, double x, double y, double z, double xs, double ys, double zs) {
        ParticleFactory<T> provider = (ParticleFactory<T>) MiscAbstraction.getProviderFromType(options.getType());
        if (provider == null) return null;
        return provider.createParticle(options, level, x, y, z, xs, ys, zs);
    }
    public static Particle addParticleToWorld(ClientWorld level, ParticleEffect options, boolean overrideLimiter, boolean canSpawnOnMinimal, double x, double y, double z, double xs, double ys, double zs) {
        try {
            return ((ClientWorldAccessor) level).getWorldRenderer().spawnParticle(
                    options, overrideLimiter, canSpawnOnMinimal,
                    x, y, z, xs, ys, zs);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while adding particle");
            CrashReportSection crashReportCategory = crashReport.addElement("Particle being added");
            crashReportCategory.add("ID", Registries.PARTICLE_TYPE.getId(options.getType()));
            crashReportCategory.add("Parameters", options.asString());
            crashReportCategory.add("Position", () -> CrashReportSection.createPositionString(level, x, y, z));
            throw new CrashException(crashReport);
        }
    }

    public static long getGameTime() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.world == null ? -100 : mc.world.getTime();
    }
    public static double getGameAndDeltaTime() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.world == null ? -100 : mc.world.getTime()+mc.getLastFrameDuration();
    }
    public static double getGameAndPartialTime() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.world == null ? -100 : mc.world.getTime()+mc.getTickDelta();
    }

    public static float[] lerpColors(long gameTime, int duration, float[][] colors) {
        if (colors.length < 1) return new float[] { 1, 1, 1 };
        int time = (int) (gameTime % duration*2);
        if (time >= duration) time = time-duration;

        int colorIndex = (int) Math.floor((time/(float)duration)*colors.length);
        float progress = ((time/(float)duration)*colors.length)-colorIndex;

        boolean tooLarge = colorIndex+1 >= colors.length;
        return new float[] {
                MathHelper.lerp(progress, colors[colorIndex][0], colors[tooLarge ? 0 : colorIndex+1][0])/255f,
                MathHelper.lerp(progress, colors[colorIndex][1], colors[tooLarge ? 0 : colorIndex+1][1])/255f,
                MathHelper.lerp(progress, colors[colorIndex][2], colors[tooLarge ? 0 : colorIndex+1][2])/255f
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

    public static void renderNameTag(BlockEntityRendererFactory.Context context, boolean increaseHeight, Text component, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i) {
        poseStack.push();
        poseStack.translate(0.5, increaseHeight ? 3.35 : 2.5, 0.5);
        poseStack.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = poseStack.peek().getPositionMatrix();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int k = (int)(g * 255.0f) << 24;
        TextRenderer font = context.getTextRenderer();
        float h = -font.getWidth(component) / 2f;
        //font.drawInBatch(component, h, 0, 0x20FFFFFF, false, matrix4f, multiBufferSource, true, k, i);
        font.draw(component, h, 0, -1, false, matrix4f, multiBufferSource, TextRenderer.TextLayerType.NORMAL, k, i);
        poseStack.pop();
    }

    public static void addDoubleQuad(MatrixStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float xOffset, float u0, float u1, float v0, float v1, int light) {
        addQuad(false, stack, vc, red, green, blue, alpha, x, y, z, xOffset, u0, u1, v0, v1, light);
        addQuad(true, stack, vc, red, green, blue, alpha, x, y, z, xOffset, u0, u1, v0, v1, light);
    }

    public static void addQuad(boolean reverse, MatrixStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float xOffset, float u0, float u1, float v0, float v1, int light) {
        if (reverse)
            addQuad((f, bl) -> bl ? f : -f+xOffset, (f, bl) -> bl ? -f : f+xOffset, stack, vc, red, green, blue, alpha, x, y, z, u1, u0, v0, v1, light);
        else
            addQuad((f, bl) -> bl ? f : f+xOffset, (f, bl) -> bl ? -f : -f+xOffset, stack, vc, red, green, blue, alpha, x, y, z, u0, u1, v0, v1, light);
    }
    public static void addQuad(BiFunction<Float, Boolean, Float> primary, BiFunction<Float, Boolean, Float> secondary, MatrixStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u0, float u1, float v0, float v1, int light) {
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), primary.apply(y, true), z, u0, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, primary.apply(x, false), secondary.apply(y, true), z, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), secondary.apply(y, true), z, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, secondary.apply(x, false), primary.apply(y, true), z, u1, v0, light);
    }
    public static void addQuad(Vector3f[] vertices, MatrixStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x()*xMult, vertices[0].y()*yMult, vertices[0].z()*zMult, u0, v0, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x()*xMult, vertices[1].y()*yMult, vertices[1].z()*zMult, u0, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x()*xMult, vertices[2].y()*yMult, vertices[2].z()*zMult, u1, v1, light);
        addVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x()*xMult, vertices[3].y()*yMult, vertices[3].z()*zMult, u1, v0, light);
    }
    public static void addDoubleQuad(BiFunction<Float, Boolean, Float> primary, BiFunction<Float, Boolean, Float> secondary, MatrixStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u0, float u1, float v0, float v1, int light) {
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
    public static void addParticleQuad(Vector3f[] vertices, MatrixStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[0].x()*xMult, vertices[0].y()*yMult, vertices[0].z()*zMult, u0, v0, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[1].x()*xMult, vertices[1].y()*yMult, vertices[1].z()*zMult, u0, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[2].x()*xMult, vertices[2].y()*yMult, vertices[2].z()*zMult, u1, v1, light);
        addParticleVertex(poseStack, vc, red, green, blue, alpha, vertices[3].x()*xMult, vertices[3].y()*yMult, vertices[3].z()*zMult, u1, v0, light);
    }
    public static void addDoubleParticleQuad(Vector3f[] vertices, MatrixStack poseStack, VertexConsumer vc, float red, float green, float blue, float alpha, float xMult, float yMult, float zMult, float u0, float u1, float v0, float v1, int light) {
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
        vc.vertex(x, y, z).texture(u, v).color(red, green, blue, alpha).light(light).next();
    }
    public static void addParticleVertex(MatrixStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.vertex(stack.peek().getPositionMatrix(), x, y, z).texture(u, v).color(red, green, blue, alpha).light(light).next();
    }

    public static void addVertex(MatrixStack stack, VertexConsumer vc, float red, float green, float blue, float alpha, float x, float y, float z, float u, float v, int light) {
        vc.vertex(stack.peek().getPositionMatrix(), x, y, z).color(red, green, blue, alpha).texture(u, v).light(light).normal(stack.peek().getNormalMatrix(), 1, 0, 0).next();
    }
}
