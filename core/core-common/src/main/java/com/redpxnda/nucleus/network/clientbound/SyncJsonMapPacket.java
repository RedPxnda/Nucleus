package com.redpxnda.nucleus.network.clientbound;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.network.SimplePacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public abstract class SyncJsonMapPacket implements SimplePacket {
    public void send() {
        send(Nucleus.SERVER);
    }

    protected final Map<String, JsonElement> elements;

    public SyncJsonMapPacket(Map<String, JsonElement> elements) {
        this.elements = elements;
    }

    public SyncJsonMapPacket(PacketByteBuf buf) {
        int size = buf.readInt();
        this.elements = new HashMap<>();
        for (int i = 0; i < size; i++) {
            elements.put(buf.readString(), JsonHelper.deserialize(buf.readString()));
        }
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeInt(elements.size());
        elements.forEach((s, j) -> {
            buf.writeString(s);
            buf.writeString(j.toString());
        });
    }
}
