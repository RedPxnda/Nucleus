package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.datapack.json.listeners.ParticleShaperListener;
import com.redpxnda.nucleus.datapack.lua.LuaSetupListener;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.ParticleCreationPacket;
import com.redpxnda.nucleus.network.clientbound.PlaySoundPacket;
import com.redpxnda.nucleus.network.clientbound.SyncParticleShapersPacket;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.registry.particles.*;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleMorpher;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import com.redpxnda.nucleus.util.MiscUtil;
import com.redpxnda.nucleus.util.ReloadSyncPackets;
import com.redpxnda.nucleus.util.RenderUtil;
import com.redpxnda.nucleus.util.SupporterUtil;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;

public class Nucleus {
    public static final ExpressionConfiguration EXPRESSION_CONFIG = ExpressionConfiguration.defaultConfiguration().withAdditionalFunctions(
            Map.entry("LIST_HAS", new ListContains()),
            Map.entry("SWITCH", new Switch())
    );
    public static final String MOD_ID = "nucleus";
    public static final NetworkChannel CHANNEL = NetworkChannel.create(loc("main"));
    public static final Gson GSON = new Gson();
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        reloadListeners();
        packets();
        events();
        SupporterUtil.init();
        AutoCodec.init();
        NucleusRegistries.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> RenderUtil::init);
        ReloadSyncPackets.init();

        // temp
        if (Platform.isDevelopmentEnvironment()) {
            Matrix4f EMPTY_MATRIX = new Matrix4f();

            InteractionEvent.RIGHT_CLICK_ITEM.register((p, e) -> {
                if (!p.getMainHandItem().is(Items.NAME_TAG) || !(p.level() instanceof ClientLevel level))
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
                    //if (!motion.equals(Vec3.ZERO)) return motion;
                    return new Vec3(0.3, 0, (Math.sin(age*Math.PI*12)/3));
                });
                outerCube.setAnimationFunction((matrix, age) -> {
                    matrix.rotate(new Quaternionf().rotationXYZ(
                            0,
                            0.1f,
                            0
                    ));
                });
                outerCube.setFriction(0.97f);
                outerCube.setLifetime(100);
                outerCube.setTickerFunction(manager -> {
                    manager.setScale(Math.max(manager.getScale() - 0.04f, 0));
                });

                ParticleShape innerCube = new ParticleShape(outerCube);
                innerCube.setParticle(MiscUtil.initialize(new CubeParticleOptions(), op -> {
                    op.invert = false;
                }));
                innerCube.setColor(0f, 0f, 0f, 1f);
                innerCube.setScale(1.25f);
                innerCube.setLifetime(100);

                ParticleShape explosion = new ParticleShape();
                explosion.setParticle(MiscUtil.initialize(new BlockChunkParticleOptions(), o -> {

                }));
                explosion.setSpawnFunction(spawner -> {
                    for (int i = 0; i < 60; i++) {
                        spawner.spawn(0, 0, 0);
                    }
                });
                explosion.setFriction(0.98f);
                explosion.setLifetime(100);
                explosion.setDelay(38);
                explosion.setGravity(1f);
                //explosion.setScale(0.1f * (MathUtil.random.nextFloat() * 0.5f + 0.5f) * 2f);
                explosion.setPhysicsEnabled(true);
                explosion.setTickerFunction(manager -> {
                    if (manager.getAge() == 0) {
                        manager.setXSpeed(MathUtil.random(-0.5, 0.5));
                        manager.setYSpeed(MathUtil.random(0.5, 0.75));
                        manager.setZSpeed(MathUtil.random(-0.5, 0.5));
                    }
                });
                explosion.setOuterTickerFunction(manager -> {
                    if (manager.getAge() == 0) {
                        manager.disconnect();
                    }
                });

                outerCube.addChild(explosion);
                outerCube.addChild(innerCube);

                morpher.setLifetime(200);
                morpher.add(outerCube);
                morpher.spawn();

                return CompoundEventResult.pass();
            });
        }
    }

    private static void packets() {
        registerPacket(ParticleCreationPacket.class, ParticleCreationPacket::new);
        registerPacket(SyncParticleShapersPacket.class, SyncParticleShapersPacket::new);
        registerPacket(PlaySoundPacket.class, PlaySoundPacket::new);
    }
    private static void events() {
    }
    public static <T extends SimplePacket> void registerPacket(Class<T> cls, Function<FriendlyByteBuf, T> decoder) {
        CHANNEL.register(cls, T::toBuffer, decoder, T::wrappedHandle);
    }

    public static ResourceLocation loc(String str) {
        return new ResourceLocation(MOD_ID, str);
    }

    public static class TestEntityCap implements EntityCapability<CompoundTag> {
        public static void init() {
            EntityDataRegistry.register(new ResourceLocation(MOD_ID, "test"), e -> e instanceof Player, TestEntityCap.class, TestEntityCap::new);
        }

        private int value = 0;
        public int getValue() {
            return value;
        }

        @Override
        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Int", value);
            return tag;
        }

        @Override
        public void loadNbt(Tag tag) {
            value = ((CompoundTag) tag).getInt("Int");
        }
    }

    private static void reloadListeners() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new LuaSetupListener());
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ParticleShaperListener());
    }
}