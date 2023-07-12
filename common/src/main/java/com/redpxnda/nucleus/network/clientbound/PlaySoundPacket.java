package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class PlaySoundPacket implements SimplePacket {
    private final double x;
    private final double y;
    private final double z;
    private final SoundEvent event;
    private final SoundSource category;
    private final float volume;
    private final float pitch;

    public PlaySoundPacket(double x, double y, double z, SoundEvent event, SoundSource category, float volume, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.event = event;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    public PlaySoundPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.event = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(buf.readUtf()));
        this.category = SoundSource.valueOf(buf.readUtf());
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeUtf(BuiltInRegistries.SOUND_EVENT.getKey(event).toString());
        buf.writeUtf(category.name());
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.playClientSound(x, y, z, event, category, volume, pitch);
    }
}
