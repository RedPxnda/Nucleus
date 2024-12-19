package com.redpxnda.nucleus;

import com.google.gson.Gson;
import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.network.NucleusPacket;
import com.redpxnda.nucleus.network.clientbound.ParticleCreationPacket;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ReloadSyncPackets;
import com.redpxnda.nucleus.util.SupporterUtil;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nucleus {
    public static final String MOD_ID = "nucleus";
    public static final Gson GSON = new Gson();
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    public static @Nullable MinecraftServer SERVER;

    public static void init() {
        packets();
        SupporterUtil.init();
        NucleusRegistries.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Rendering::init);
        ReloadSyncPackets.init();
        InterpolateMode.init();

        LifecycleEvent.SERVER_BEFORE_START.register(server -> SERVER = server);
    }

    private static void packets() {
        registerPacket(NetworkManager.Side.S2C, ParticleCreationPacket.TYPE, ParticleCreationPacket.STREAM_CODEC);
    }

    public static <T extends NucleusPacket> void registerPacket(NetworkManager.Side side, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (side == NetworkManager.Side.S2C && Platform.getEnvironment() != Env.CLIENT)
            NetworkManager.registerS2CPayloadType(type, streamCodec);
        else
            NetworkManager.registerReceiver(side, type, streamCodec, (packet, context) -> context.queue(() -> packet.handle(context)));
    }

    public static ResourceLocation loc(String str) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, str);
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger("Nucleus: " + STACK_WALKER.getCallerClass().getSimpleName());
    }
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger("Nucleus: " + name);
    }
}