package com.redpxnda.nucleus.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.redpxnda.nucleus.capability.entity.doubles.ClientCapabilityListener;
import com.redpxnda.nucleus.capability.entity.doubles.DoublesCapability;
import com.redpxnda.nucleus.capability.entity.doubles.RenderingMode;
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
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.BiFunction;

import static com.redpxnda.nucleus.Nucleus.loc;
import static net.minecraft.client.renderer.RenderStateShard.*;

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

    public static ShaderInstance alphaAnimationShader;
    public static ShaderInstance trailShader;

    public static RenderType transparentTriangleStrip = RenderType.create(
            "nucleus_triangle_strip", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLE_STRIP,
            256, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LEASH_SHADER).setTextureState(NO_TEXTURE)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP)
            .createCompositeState(false));

    public static RenderType alphaAnimation = RenderType.create(
            "nucleus_alpha_animation_translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
            0x200000, true, true, RenderType.translucentState(new RenderStateShard.ShaderStateShard(() -> alphaAnimationShader)));

    public static final RenderType.CompositeRenderType trail = RenderType.create(
            "nucleus_trail", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES,
            256, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> trailShader))
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(4))).setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE)
            .setCullState(NO_CULL).createCompositeState(false));

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
        //particleShaping();
        Class<?> classLoading = ClientCapabilityListener.class;

        PoseAnimationResourceListener.init();
        ShaderRegistry.register(loc("rendertype_alpha_animation"), DefaultVertexFormat.BLOCK, i -> alphaAnimationShader = i);
        ShaderRegistry.register(loc("rendertype_trail"), DefaultVertexFormat.POSITION_COLOR_NORMAL, i -> trailShader = i);
        ParticleProviderRegistry.register(NucleusRegistries.emittingParticle, new EmitterParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.mimicParticle, new MimicParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.controllerParticle, new ControllerParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.cubeParticle, new CubeParticle.Provider());
        ParticleProviderRegistry.register(NucleusRegistries.blockChunkParticle, new ChunkParticle.Provider());

        RenderEvents.LIVING_ENTITY_RENDER.register((stage, model, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage != RenderEvents.EntityRenderStage.PRE) return EventResult.pass();
            for (Map.Entry<MobEffect, MobEffectInstance> entry : entity.getActiveEffectsMap().entrySet()) {
                MobEffectInstance instance = entry.getValue();
                MobEffect effect = entry.getKey();
                if (effect instanceof RenderingMobEffect rendering && (instance.getDuration() > 0 || instance.isInfiniteDuration())) {
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
                if (effect instanceof RenderingMobEffect rendering && (instance.getDuration() > 0 || instance.isInfiniteDuration())) {
                    rendering.renderPost(instance, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight);
                }
            });
            return EventResult.pass();
        });
        RenderEvents.HUD_RENDER_PRE.register((minecraft, graphics, partialTick) -> {
            for (Map.Entry<MobEffect, MobEffectInstance> entry : minecraft.player.getActiveEffectsMap().entrySet()) {
                if (entry.getKey() instanceof RenderingMobEffect rendering) {
                    boolean result = rendering.renderHud(entry.getValue(), minecraft, graphics, partialTick);
                    if (result)
                        return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });

        ClientGuiEvent.RENDER_HUD.register((graphics, tickDelta) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                Player player = mc.player;
                DoublesCapability cap = DoublesCapability.getAllFor(player);
                if (cap == null) return;

                int width = graphics.guiWidth()/2;
                int height = graphics.guiHeight()/2 + 15;
                int yOffset = 0;

                RenderSystem.enableBlend();
                for (Map.Entry<String, Double> entry : cap.doubles.entrySet()) {
                    String key = entry.getKey();
                    double value = entry.getValue();
                    long lastMod = cap.getModificationTime(key, -1000);

                    RenderingMode mode = ClientCapabilityListener.renderers.get(key);
                    if (mode != null && mode.predicate.canRender(value, lastMod)) {
                        long currentTime = Util.getMillis();
                        float dif = (currentTime - lastMod) / 1000f;
                        float lerpDelta = Mth.clamp(dif/mode.interpolateTime, 0f, 1f);
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
                if (!p.getMainHandItem().is(Items.NAME_TAG) || !(p.level() instanceof ClientLevel level) || !(p.isCreative()))
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
                    matrix.rotate(Axis.YP.rotationDegrees(10));
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

    public static <T extends ParticleOptions> Particle createParticle(ClientLevel level, T options, double x, double y, double z, double xs, double ys, double zs) {
        ParticleProvider<T> provider = (ParticleProvider<T>) MiscAbstraction.getProviderFromType(options.getType());
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

    public static long getGameTime() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null ? -100 : mc.level.getGameTime();
    }
    public static double getGameAndDeltaTime() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null ? -100 : mc.level.getGameTime()+mc.getDeltaFrameTime();
    }
    public static double getGameAndPartialTime() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null ? -100 : mc.level.getGameTime()+mc.getFrameTime();
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
