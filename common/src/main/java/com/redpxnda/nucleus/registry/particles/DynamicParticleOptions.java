package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

public abstract class DynamicParticleOptions implements ParticleEffect {
    public int lifetime = 100;
    public float gravity = 0f, friction = 0.98f, scale = 1,
    red = 1, green = 1, blue = 1, alpha = 1;
    public boolean physics = true;

    public DynamicParticleOptions() {}

    public abstract Codec<? extends DynamicParticleOptions> codec();

    @Override
    public abstract ParticleType<?> getType();

    @Override
    public void write(PacketByteBuf buf) {
        ByteBufUtil.writeWithCodec(buf, this, (Codec) codec());
    }

    @Override
    public String asString() {
        return Registries.PARTICLE_TYPE.getId(this.getType()).toString();
    }
}
