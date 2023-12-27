package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;

public class ConfigSyncPacket implements SimplePacket {
    public final String config;
    public final String data;

    public ConfigSyncPacket(String config, String data) {
        this.config = config;
        this.data = data;
    }

    public ConfigSyncPacket(PacketByteBuf buf) {
        this.config = buf.readString();
        this.data = buf.readString();
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeString(config);
        buf.writeString(data);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ConfigManager.getConfigObject(config).load(data);
    }
}
