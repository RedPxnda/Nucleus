package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ParticleCreationPacket implements SimplePacket {
    private final ParticleEffect options;
    private final double x;
    private final double y;
    private final double z;
    private final double xs;
    private final double ys;
    private final double zs;

    public ParticleCreationPacket(ParticleEffect options, double x, double y, double z, double xs, double ys, double zs) {
        this.options = options;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xs = xs;
        this.ys = ys;
        this.zs = zs;
    }

    public ParticleCreationPacket(PacketByteBuf buf) {
        ParticleType<?> particleType = buf.readRegistryValue(Registries.PARTICLE_TYPE);
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
    public void send(ServerWorld level) {
        Nucleus.CHANNEL.sendToPlayers(level.getPlayers(serverPlayer -> {
            if (serverPlayer.getWorld() != level) return false;
            BlockPos blockPos = serverPlayer.getBlockPos();
            return blockPos.isWithinDistance(new Vec3d(x, y, z), 32.0);
        }), this);
    }

    private <T extends ParticleEffect> T readParticle(PacketByteBuf friendlyByteBuf, ParticleType<T> particleType) {
        return particleType.getParametersFactory().read(particleType, friendlyByteBuf);
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeRegistryValue(Registries.PARTICLE_TYPE, options.getType());
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(xs);
        buf.writeDouble(ys);
        buf.writeDouble(zs);
        options.write(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.createClientParticle(options, x, y, z, xs, ys, zs);
    }
}
