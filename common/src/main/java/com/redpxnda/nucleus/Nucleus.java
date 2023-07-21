package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.datapack.lua.LuaSetupListener;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.ParticleCreationPacket;
import com.redpxnda.nucleus.network.clientbound.PlaySoundPacket;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ReloadSyncPackets;
import com.redpxnda.nucleus.util.RenderUtil;
import com.redpxnda.nucleus.util.SupporterUtil;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
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
    }

    private static void packets() {
        registerPacket(ParticleCreationPacket.class, ParticleCreationPacket::new);
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
    }
}