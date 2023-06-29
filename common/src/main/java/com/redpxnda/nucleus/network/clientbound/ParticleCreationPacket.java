package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class ParticleCreationPacket implements SimplePacket {
    private final ParticleOptions options;
    private final double x;
    private final double y;
    private final double z;
    private final double xs;
    private final double ys;
    private final double zs;

    public ParticleCreationPacket(ParticleOptions options, double x, double y, double z, double xs, double ys, double zs) {
        this.options = options;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xs = xs;
        this.ys = ys;
        this.zs = zs;
    }

    public ParticleCreationPacket(FriendlyByteBuf buf) {
        ParticleType<?> particleType = buf.readById(BuiltInRegistries.PARTICLE_TYPE);
        assert particleType != null : "Bro what particle are you sending over??? (ParticleType in ParticleCreationPacket non-existent.)";

        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.xs = buf.readDouble();
        this.ys = buf.readDouble();
        this.zs = buf.readDouble();
        this.options = readParticle(buf, particleType);
    }

    @Override
    public void send(ServerLevel level) {
        Nucleus.CHANNEL.sendToPlayers(level.getPlayers(serverPlayer -> {
            if (serverPlayer.level() != level) return false;
            BlockPos blockPos = serverPlayer.blockPosition();
            return blockPos.closerToCenterThan(new Vec3(x, y, z), 32.0);
        }), this);
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf friendlyByteBuf, ParticleType<T> particleType) {
        return particleType.getDeserializer().fromNetwork(particleType, friendlyByteBuf);
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeId(BuiltInRegistries.PARTICLE_TYPE, options.getType());
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(xs);
        buf.writeDouble(ys);
        buf.writeDouble(zs);
        options.writeToNetwork(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.createClientParticle(options, x, y, z, xs, ys, zs);
    }
}
