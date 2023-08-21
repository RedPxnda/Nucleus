package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public abstract class DynamicParticleOptions implements ParticleOptions {
    public int lifetime = 100;
    public float gravity = 0f, friction = 0.98f, scale = 1,
    red = 1, green = 1, blue = 1, alpha = 1;
    public boolean physics = true;

    public DynamicParticleOptions() {}

    public abstract Codec<? extends DynamicParticleOptions> codec();

    @Override
    public abstract ParticleType<?> getType();

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        ByteBufUtil.writeWithCodec(buf, this, (Codec) codec());
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()).toString();
    }
}
