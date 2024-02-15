package com.redpxnda.nucleus;

import com.google.gson.Gson;
import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.ParticleCreationPacket;
import com.redpxnda.nucleus.network.clientbound.PlaySoundPacket;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ReloadSyncPackets;
import com.redpxnda.nucleus.util.SupporterUtil;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Nucleus {
    public static final String MOD_ID = "nucleus";
    public static final NetworkChannel CHANNEL = NetworkChannel.create(loc("main"));
    public static final Gson GSON = new Gson();
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    public static @Nullable MinecraftServer SERVER;

    public static void init() {
        packets();
        SupporterUtil.init();
        NucleusRegistries.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Rendering::init);
        ReloadSyncPackets.init();

        LifecycleEvent.SERVER_BEFORE_START.register(server -> SERVER = server);
    }

    private static void packets() {
        registerPacket(ParticleCreationPacket.class, ParticleCreationPacket::new);
        registerPacket(PlaySoundPacket.class, PlaySoundPacket::new);
    }

    public static <T extends SimplePacket> void registerPacket(Class<T> cls, Function<PacketByteBuf, T> decoder) {
        CHANNEL.register(cls, T::toBuffer, decoder, T::wrappedHandle);
    }

    public static Identifier loc(String str) {
        return new Identifier(MOD_ID, str);
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger("Nucleus: " + STACK_WALKER.getCallerClass().getSimpleName());
    }
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger("Nucleus: " + name);
    }
}