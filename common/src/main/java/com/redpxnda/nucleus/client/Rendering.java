package com.redpxnda.nucleus.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.redpxnda.nucleus.facet.doubles.ClientCapabilityListener;
import com.redpxnda.nucleus.facet.doubles.NumericalsFacet;
import com.redpxnda.nucleus.facet.doubles.RenderingMode;
import com.redpxnda.nucleus.event.RenderEvents;
import com.redpxnda.nucleus.impl.MiscAbstraction;
import com.redpxnda.nucleus.impl.ShaderRegistry;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.mixin.client.ClientLevelAccessor;
import com.redpxnda.nucleus.pose.PoseAnimationResourceListener;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.registry.effect.RenderingMobEffect;
import com.redpxnda.nucleus.registry.particles.*;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleMorpher;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import com.redpxnda.nucleus.util.MiscUtil;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
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
        //particleShaping();
        Class<?> classLoading = ClientCapabilityListener.class;

        PoseAnimationResourceListener.init();
        ShaderRegistry.register(loc("rendertype_alpha_animation"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, i -> alphaAnimationShader = i);
        ShaderRegistry.register(loc("rendertype_trail"), VertexFormats.LINES, i -> trailShader = i);
        ParticleProviderRegistry.register(NucleusRegistries.emittingParticle, new EmitterParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.mimicParticle, new MimicParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.controllerParticle, new ControllerParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.cubeParticle, new CubeParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.blockChunkParticle, new ChunkParticle.Provider());

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

        ClientGuiEvent.RENDER_HUD.register((graphics, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                PlayerEntity player = mc.player;
                NumericalsFacet cap = NumericalsFacet.get(player);
                if (cap == null) return;

                int width = graphics.getScaledWindowWidth()/2;
                int height = graphics.getScaledWindowHeight()/2 + 15;
                int yOffset = 0;

                RenderSystem.enableBlend();
                for (Map.Entry<String, Double> entry : cap.doubles.entrySet()) {
                    String key = entry.getKey();
                    double value = entry.getValue();
                    long lastMod = cap.getModificationTime(key, -1000);

                    RenderingMode mode = ClientCapabilityListener.renderers.get(key);
                    if (mode != null && mode.predicate.canRender(value, lastMod)) {
                        long currentTime = Util.getMeasuringTimeMs();
                        float dif = (currentTime - lastMod) / 1000f;
                        float lerpDelta = MathHelper.clamp(dif/mode.interpolateTime, 0f, 1f);
                        value = mode.interpolate.interpolate(lerpDelta, cap.prevValues.getOrDefault(key, value), value);

                        float alpha = mode.predicate.getAlpha(lastMod);
                        mode.render(value, graphics, width, height + yOffset, alpha);
                        yOffset+=mode.getHeight()+mode.margin;
                    }
                }
                RenderSystem.disableBlend();
            }
        });

        // event testing
        /*MiscEvents.CAN_CLIENT_SPRINT.register(player -> {
            //if (player.hasEffect(NucleusRegistries.testEffect.get())) return EventResult.interruptFalse();
            return EventResult.pass();
        });
        MiscEvents.LIVING_JUMP_POWER.register(player -> {
            //if (player.hasEffect(NucleusRegistries.testEffect.get())) return CompoundEventResult.interruptFalse(0.5f);
            return CompoundEventResult.pass();
        });
        MiscEvents.MODIFY_CAMERA_MOTION.register((mc, motion) -> {
            if (mc.player != null && mc.player.hasEffect(NucleusRegistries.testEffect.get())) {
                if (motion.x != 0 || motion.y != 0) motion.normalize();
                motion.add(MathUtil.random(-10d, 10), 0);
            }
        });*/
    }

    public static void particleShaping() {
        if (Platform.isDevelopmentEnvironment()) {
            InteractionEvent.RIGHT_CLICK_ITEM.register((p, e) -> {
                if (!p.getMainHandStack().isOf(Items.NAME_TAG) || !(p.getWorld() instanceof ClientWorld level) || !(p.isCreative()))
                    return CompoundEventResult.pass();

                ParticleMorpher morpher = new ParticleMorpher(level, p.getX(), p.getY(), p.getZ());
                ParticleShape outerCube = new ParticleShape();

                outerCube.setParticle(MiscUtil.initialize(new CubeParticleOptions(), op -> {
                    op.invert = true;
                }));
                outerCube.setColor(0.58f, 0f, 1f, 1f);
                outerCube.setScale(1.5f);
                outerCube.setSpawnFunction(ParticleShape.SpawnFunction.SINGLE);
                outerCube.setOuterMotionFunction((pos, motion, age) -> {
                    if (age < 37/400f) {
                        age*=4;
                        motion.set(0.3, 0, (Math.sin(age * Math.PI * 12) / 3));
                    } else motion.set(0, 0, 0);
                });
                outerCube.setAnimationFunction((matrix, manager, age) -> {
                    matrix.rotate(new Quaternionf().rotationXYZ(
                            0,
                            0.1f,
                            0
                    ));
                });
                outerCube.setFriction(0.97f);
                outerCube.setLifetime(400);
                outerCube.setOuterTickerFunction(manager -> {
                    Trail trail = manager.getTrail();
                    if (trail.width > 0) trail.width-=0.004f;
                });
                outerCube.setTickerFunction(manager -> {
                    manager.setScale(Math.max(manager.getScale() - 0.04f, 0));
                });
                outerCube.setOuterTrail(new Trail()
                        .setColor(0.58f, 0f, 1f, 0.6f)
                        .setMaxLength(20));

                ParticleShape innerCube = new ParticleShape(outerCube);
                innerCube.setParticle(MiscUtil.initialize(new CubeParticleOptions(), op -> {
                    op.invert = false;
                }));
                innerCube.setColor(0f, 0f, 0f, 1f);
                innerCube.setScale(1.25f);
                innerCube.setLifetime(100);


                ParticleShape explosion = new ParticleShape();
                explosion.setParticle(MiscUtil.initialize(new CubeParticleOptions(), o -> {
                    o.xSize = 0.1f;
                    o.ySize = 0.1f;
                    o.zSize = 0.1f;
                }));
                explosion.setSpawnFunction(ParticleShape.SpawnFunction.SINGLE);
                explosion.setFriction(1f);
                explosion.setLifetime(80);
                explosion.setDelay(38);
                explosion.setGravity(0f);
                explosion.setPhysicsEnabled(true);
                explosion.setColor(0.5f, 0.5f, 0.5f, 1f);
                explosion.setMotionFunction((pos, motion, t) -> {
                    if (t < 60/80f) {
                        double pt = t * Math.PI * 30;
                        pos.x = t * 3 * Math.cos(pt);
                        pos.y = 7 * (t);
                        pos.z = t * 3 * Math.sin(pt);
                    } else if (t == 60/80f)
                        pos.set(0, pos.y, 0);
                });
                Trail greyTrail = new Trail().setWidth(0.1f).setMaxLength(3).setEmissive(false).setColor(0.5f, 0.5f, 0.5f, 1f);
                explosion.setTickerFunction(manager -> {
                    if (manager.getAge() == 60) {
                        manager.setGravity(1f);
                        manager.clearSavedPositions();
                        manager.setTrail(greyTrail);
                        manager.setXSpeed(MathUtil.random(-0.5, 0.5));
                        manager.setYSpeed(MathUtil.random(-0.25, 0.25));
                        manager.setZSpeed(MathUtil.random(-0.5, 0.5));
                    }
                });
                explosion.setOuterTickerFunction(manager -> {
                    if (manager.getAge() == 0) {
                        manager.disconnect();
                    }
                });
                explosion.setOuterAnimationFunction((matrix, manager, age) -> {
                    matrix.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(10));
                });
                explosion.setTrail(new Trail().setWidth(0.1f).setMaxLength(3).setEmissive(true));
                explosion.setLoopFunction(ParticleShape.LoopFunction.DUPE_CHILDREN);
                explosion.setLoops(100);
                explosion.setLoopInterval(0);

                outerCube.addChild(explosion);
                outerCube.addChild(innerCube);

                morpher.setLifetime(400);
                morpher.add(outerCube);
                morpher.spawn();

                return CompoundEventResult.pass();
            });
        }
    }

    public static <T extends ParticleEffect> Particle createParticle(ClientWorld level, T options, double x, double y, double z, double xs, double ys, double zs) {
        ParticleFactory<T> provider = (ParticleFactory<T>) MiscAbstraction.getProviderFromType(options.getType());
        if (provider == null) return null;
        return provider.createParticle(options, level, x, y, z, xs, ys, zs);
    }
    public static Particle addParticleToWorld(ClientWorld level, ParticleEffect options, boolean overrideLimiter, boolean canSpawnOnMinimal, double x, double y, double z, double xs, double ys, double zs) {
        try {
            return ((ClientLevelAccessor) level).getWorldRenderer().spawnParticle(
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
