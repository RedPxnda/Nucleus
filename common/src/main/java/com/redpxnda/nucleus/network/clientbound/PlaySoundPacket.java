package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PlaySoundPacket implements SimplePacket {
    private final double x;
    private final double y;
    private final double z;
    private final SoundEvent event;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;

    public PlaySoundPacket(double x, double y, double z, SoundEvent event, SoundCategory category, float volume, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.event = event;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    public PlaySoundPacket(PacketByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.event = Registries.SOUND_EVENT.get(new Identifier(buf.readString()));
        this.category = SoundCategory.valueOf(buf.readString());
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeString(Registries.SOUND_EVENT.getId(event).toString());
        buf.writeString(category.name());
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.playClientSound(x, y, z, event, category, volume, pitch);
    }
}
