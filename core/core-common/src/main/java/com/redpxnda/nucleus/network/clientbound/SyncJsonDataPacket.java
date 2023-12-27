package com.redpxnda.nucleus.network.clientbound;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.network.SimplePacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;

public abstract class SyncJsonDataPacket implements SimplePacket {
    protected final JsonElement element;

    public SyncJsonDataPacket(JsonElement element) {
        this.element = element;
    }

    public SyncJsonDataPacket(PacketByteBuf buf) {
        this.element = JsonHelper.deserialize(buf.readString());
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeString(element.toString());
    }
}
