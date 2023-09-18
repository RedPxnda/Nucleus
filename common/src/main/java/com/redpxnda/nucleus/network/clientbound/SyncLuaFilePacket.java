package com.redpxnda.nucleus.network.clientbound;

import com.mojang.datafixers.util.Pair;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.lua.LuaSyncedReloadListener;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SyncLuaFilePacket implements SimplePacket {
    protected final Map<Identifier, byte[]> values;
    protected final String syncId;

    public SyncLuaFilePacket(Map<Identifier, byte[]> values, String syncId) {
        this.values = values;
        this.syncId = syncId;
    }

    public SyncLuaFilePacket(PacketByteBuf buf) {
        this.syncId = buf.readString();
        this.values = new HashMap<>();
        int amnt = buf.readInt();
        for (int i = 0; i < amnt; i++) {
            values.put(new Identifier(buf.readString()), buf.readByteArray());
        }
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeString(syncId);
        buf.writeInt(values.keySet().size());
        values.forEach((key, value) -> {
            buf.writeString(key.toString());
            buf.writeBytes(value);
        });
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        Globals globals = LuaSyncedReloadListener.clientGlobals.get(syncId);
        Map<Identifier, LuaValue> parsed = values.entrySet().stream().map(entry -> {
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
