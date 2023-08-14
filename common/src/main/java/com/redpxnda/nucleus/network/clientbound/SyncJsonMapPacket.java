package com.redpxnda.nucleus.network.clientbound;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.network.SimplePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

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

    public SyncJsonMapPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.elements = new HashMap<>();
        for (int i = 0; i < size; i++) {
            elements.put(buf.readUtf(), GsonHelper.parse(buf.readUtf()));
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeInt(elements.size());
        elements.forEach((s, j) -> {
            buf.writeUtf(s);
            buf.writeUtf(j.toString());
        });
    }
}
