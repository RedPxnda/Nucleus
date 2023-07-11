package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.codec.ValueAssigner;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record MimicParticleOptions(ValueAssigner<Manager> setup,
                                   ValueAssigner<Manager> tick) implements ParticleOptions {
    public static Codec<ValueAssigner<Manager>> vaSetupCodec = new ValueAssigner.Builder<Manager>()
            .add("lifetime", Codec.INT, Manager::setLifetime, 100)
            .add("friction", Codec.FLOAT, Manager::setFriction, 0.98f)
            .add("red", Codec.FLOAT, Manager::setRed, 1f)
            .add("green", Codec.FLOAT, Manager::setGreen, 1f)
            .add("blue", Codec.FLOAT, Manager::setBlue, 1f)
            .add("alpha", Codec.FLOAT, Manager::setAlpha, 1f)
            .add("scale", Codec.FLOAT, Manager::setScale, 1f)
            .add("physics", Codec.BOOL, Manager::setPhysicsEnabled, true)
            .add("texture", ResourceLocation.CODEC, Manager::setTexture, new ResourceLocation("block/dirt"))
            .codec();
    public static Codec<ValueAssigner<Manager>> vaTickCodec = new ValueAssigner.Builder<Manager>()
            .add("red", Codec.FLOAT, (p, f) -> p.setRed(p.getRed()+f), 0f)
            .add("green", Codec.FLOAT, (p, f) -> p.setGreen(p.getGreen()+f), 0f)
            .add("blue", Codec.FLOAT, (p, f) -> p.setBlue(p.getBlue()+f), 0f)
            .add("alpha", Codec.FLOAT, (p, f) -> p.setAlpha(p.getAlpha()+f), 0f)
            .add("scale", Codec.FLOAT, (p, f) -> p.setScale(p.getScale()+f), 0f)
            //.add("texture", ResourceLocation.CODEC, (p, rl) -> p.texture = rl, new ResourceLocation("block/dirt"))
            .codec();
    public static Codec<MimicParticleOptions> codec = RecordCodecBuilder.create(inst -> inst.group(
            vaSetupCodec.fieldOf("setup").forGetter(i -> i.setup),
            vaTickCodec.fieldOf("tick").forGetter(i -> i.tick)
    ).apply(inst, MimicParticleOptions::new));

    @Override
    public ParticleType<?> getType() {
        return NucleusRegistries.mimicParticle.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        ByteBufUtil.writeWithCodec(buf, this, codec);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()).toString();
    }

    public interface Manager extends DynamicParticleManager {
        ResourceLocation getTexture();
        void setTexture(ResourceLocation rl);
    }
}
