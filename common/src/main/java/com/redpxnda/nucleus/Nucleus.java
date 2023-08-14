package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.redpxnda.nucleus.capability.doubles.DoublesCapability;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.capability.doubles.CapabilityRegistryListener;
import com.redpxnda.nucleus.datapack.lua.LuaSetupListener;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.*;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ReloadSyncPackets;
import com.redpxnda.nucleus.util.RenderUtil;
import com.redpxnda.nucleus.util.SupporterUtil;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
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
    public static MinecraftServer SERVER;

    public static void init() {
        reloadListeners();
        packets();
        events();
        capabilities();
        SupporterUtil.init();
        AutoCodec.init();
        NucleusRegistries.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> RenderUtil::init);
        ReloadSyncPackets.init();

        LifecycleEvent.SERVER_BEFORE_START.register(server -> SERVER = server);

        // temp test code
        if (Platform.isDevelopmentEnvironment()) {
            InteractionEvent.RIGHT_CLICK_ITEM.register((p, hand) -> {
                if (p instanceof ServerPlayer player && (player.getMainHandItem().is(Items.STICK) || player.getMainHandItem().is(Items.SADDLE))) {
                    double amnt = player.getMainHandItem().is(Items.STICK) ? 5 : -5;
                    DoublesCapability cap = DoublesCapability.getAllFor(player);
                    double val = cap.get("nucleus:test");
                    cap.set("nucleus:test", val+amnt);
                    cap.sendToClient(player);
                }
                return CompoundEventResult.pass();
            });
        }
    }

    private static void packets() {
        registerPacket(SyncLuaFilePacket.class, SyncLuaFilePacket::new);
        registerPacket(ParticleCreationPacket.class, ParticleCreationPacket::new);
        registerPacket(PlaySoundPacket.class, PlaySoundPacket::new);
        registerPacket(CapabilitySyncPacket.class, CapabilitySyncPacket::new);
        registerPacket(DoublesCapabilitySyncPacket.class, DoublesCapabilitySyncPacket::new);
        registerPacket(SyncCapabilitiesJsonPacket.class, SyncCapabilitiesJsonPacket::new);
    }
    private static void events() {
    }
    private static void capabilities() {
        EntityDataRegistry.register(loc("simple_doubles"), e -> true, DoublesCapability.class, DoublesCapability::new);
        PlayerEvent.PLAYER_JOIN.register(player -> {
            DoublesCapability cap = DoublesCapability.getAllFor(player);
            cap.sendToClient(player);
        });
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
        public void loadNbt(CompoundTag tag) {
            value = tag.getInt("Int");
        }
    }

    private static void reloadListeners() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new LuaSetupListener());
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new CapabilityRegistryListener());
    }
}