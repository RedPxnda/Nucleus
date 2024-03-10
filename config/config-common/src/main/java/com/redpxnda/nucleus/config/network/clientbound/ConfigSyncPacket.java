package com.redpxnda.nucleus.config.network.clientbound;

import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ConfigSyncPacket implements SimplePacket {
    public final Identifier config;
    public final String data;

    public ConfigSyncPacket(Identifier config, String data) {
        this.config = config;
        this.data = data;
    }

    public ConfigSyncPacket(PacketByteBuf buf) {
        this.config = new Identifier(buf.readString());
        this.data = buf.readString();
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeString(config.toString());
        buf.writeString(data);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ConfigManager.getConfigObject(config).load(data);
    }
}
