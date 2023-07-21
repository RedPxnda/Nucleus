package com.redpxnda.nucleus.network.clientbound;

import com.mojang.datafixers.util.Pair;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.lua.LuaSyncedReloadListener;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SyncLuaFilePacket implements SimplePacket {
    protected final Map<ResourceLocation, byte[]> values;
    protected final String syncId;

    public SyncLuaFilePacket(Map<ResourceLocation, byte[]> values, String syncId) {
        this.values = values;
        this.syncId = syncId;
    }

    public SyncLuaFilePacket(FriendlyByteBuf buf) {
        this.syncId = buf.readUtf();
        this.values = new HashMap<>();
        int amnt = buf.readInt();
        for (int i = 0; i < amnt; i++) {
            values.put(new ResourceLocation(buf.readUtf()), buf.readByteArray());
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeUtf(syncId);
        buf.writeInt(values.keySet().size());
        values.forEach((key, value) -> {
            buf.writeUtf(key.toString());
            buf.writeBytes(value);
        });
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        Globals globals = LuaSyncedReloadListener.clientGlobals.get(syncId);
        Map<ResourceLocation, LuaValue> parsed = values.entrySet().stream().map(entry -> {
            try {
                return Pair.of(entry.getKey(), globals.load(new String(entry.getValue(), StandardCharsets.UTF_8)));
            } catch (Exception e) {
                e.printStackTrace();
                Nucleus.LOGGER.error("Failed to load LuaScript at '{}' from sync id '{}' on the client! -> {}", entry.getKey(), syncId, e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        LuaSyncedReloadListener.clientHandlers.get(syncId).accept(parsed);
    }
}
