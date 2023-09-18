package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.redpxnda.nucleus.capability.entity.EntityDataManager;
import com.redpxnda.nucleus.capability.entity.EntityDataRegistry;
import com.redpxnda.nucleus.capability.entity.TrackingUpdateSyncer;
import com.redpxnda.nucleus.capability.entity.doubles.CapabilityRegistryListener;
import com.redpxnda.nucleus.capability.entity.doubles.DoublesCapability;
import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.datapack.lua.LuaSetupListener;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.*;
import com.redpxnda.nucleus.pose.ClientPoseCapability;
import com.redpxnda.nucleus.pose.PoseAnimationResourceListener;
import com.redpxnda.nucleus.pose.ServerPoseCapability;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.resolving.wrappers.Wrappers;
import com.redpxnda.nucleus.util.ReloadSyncPackets;
import com.redpxnda.nucleus.util.SupporterUtil;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
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
    public static MinecraftServer SERVER;
    private static final List<String> ADDON_NAMESPACES = new ArrayList<>(List.of("nucleus"));

    public static void init() {
        reloadListeners();
        packets();
        events();
        capabilities();
        SupporterUtil.init();
        AutoCodec.init();
        NucleusRegistries.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Rendering::init);
        ReloadSyncPackets.init();
        TrackingUpdateSyncer.init();
        Wrappers.init();

        LifecycleEvent.SERVER_BEFORE_START.register(server -> SERVER = server);

        // temp test code
        /*if (Platform.isDevelopmentEnvironment()) {
            InteractionEvent.RIGHT_CLICK_ITEM.register((p, hand) -> {
                if (p instanceof ServerPlayerEntity player) {
                    if (player.getMainHandStack().isOf(Items.STICK) || player.getMainHandStack().isOf(Items.SADDLE)) {
                        double amnt = player.getMainHandStack().isOf(Items.STICK) ? 5 : -5;
                        DoublesCapability cap = DoublesCapability.getAllFor(player);
                        double val = cap.get("nucleus:test");
                        cap.set("nucleus:test", val + amnt);
                        cap.sendToClient(player);
                    } else if (player.getMainHandStack().isOf(Items.ALLIUM)) {
                        ServerPoseCapability cap = ServerPoseCapability.getFor(player);
                        String animation = cap.getPose().equals("none") ? "nucleus:test" : "none";
                        Hand usedHand = !player.isSneaking() ? Hand.MAIN_HAND : Hand.OFF_HAND;
                        cap.set(animation, player.getWorld().getTime(), usedHand);
                        cap.sendToClient(player);
                    }
                }
                return CompoundEventResult.pass();
            });
        }*/
    }

    private static void packets() {
        registerPacket(SyncLuaFilePacket.class, SyncLuaFilePacket::new);
        registerPacket(ParticleCreationPacket.class, ParticleCreationPacket::new);
        registerPacket(PlaySoundPacket.class, PlaySoundPacket::new);
        registerPacket(CapabilitySyncPacket.class, CapabilitySyncPacket::new);
        registerPacket(DoublesCapabilitySyncPacket.class, DoublesCapabilitySyncPacket::new);
        registerPacket(SyncCapabilitiesJsonPacket.class, SyncCapabilitiesJsonPacket::new);
        registerPacket(PoseCapabilitySyncPacket.class, PoseCapabilitySyncPacket::new);
    }
    private static void events() {
    }
    private static void capabilities() {
        EntityDataRegistry.register(loc("simple_doubles"), e -> true, DoublesCapability.class, DoublesCapability::new);
        EntityDataRegistry.register(loc("pose"), e -> e instanceof PlayerEntity && !e.getWorld().isClient, ServerPoseCapability.class, ServerPoseCapability::new);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () ->
                EntityDataRegistry.register(ClientPoseCapability.loc, e -> e instanceof PlayerEntity && e.getWorld().isClient, ClientPoseCapability.class, ClientPoseCapability::new)
        );

        EntityEvent.ADD.register((entity, world) -> {
            EntityDataRegistry.CAPABILITIES.forEach((cls, cap) -> {
                if (!cap.predicate().test(entity)) return;
                EntityDataManager.getOrCreateCapability(entity, cls);
            });

            return EventResult.pass();
        });

        PlayerEvent.PLAYER_JOIN.register(player -> {
            if (!Platform.isForge()) // FU forge, don't change stuff like this pls
                EntityDataRegistry.CAPABILITIES.forEach((cls, cap) -> {
                    if (!cap.predicate().test(player)) return;
                    EntityDataManager.getOrCreateCapability(player, cls);
                });

            DoublesCapability doublesCap = DoublesCapability.getAllFor(player);
            ServerPoseCapability poseCap = ServerPoseCapability.getFor(player);
            if (doublesCap != null) doublesCap.sendToClient(player);
            if (poseCap != null) poseCap.sendToClient(player);
        });
    }

    public static <T extends SimplePacket> void registerPacket(Class<T> cls, Function<PacketByteBuf, T> decoder) {
        CHANNEL.register(cls, T::toBuffer, decoder, T::wrappedHandle);
    }

    public static Identifier loc(String str) {
        return new Identifier(MOD_ID, str);
    }

    /*public static class TestEntityCap implements EntityCapability<CompoundTag> {
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
        public void loadNbt(CompoundTag tag) {
            value = tag.getInt("Int");
        }
    }*/

    private static void reloadListeners() {
        ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new LuaSetupListener()); // works for all namespaces
        ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new CapabilityRegistryListener()); // works for nucleus and addon namespaces
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, new PoseAnimationResourceListener())); // works for nucleus and addon namespaces
    }

    public static void addAddonNamespace(String namespace) {
        ADDON_NAMESPACES.add(namespace);
    }
    public static boolean isNamespaceValid(String namespace) {
        return ADDON_NAMESPACES.contains(namespace);
    }
}