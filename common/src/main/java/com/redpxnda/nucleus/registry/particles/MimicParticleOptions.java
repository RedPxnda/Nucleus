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

//todo fix
public record MimicParticleOptions(ValueAssigner<MimicParticle> setup,
                                   ValueAssigner<MimicParticle> tick) implements ParticleOptions {
    public static Codec<ValueAssigner<MimicParticle>> vaSetupCodec = new ValueAssigner.Builder<MimicParticle>()
            .add("lifetime", Codec.INT, MimicParticle::setLifetime, 100)
            .add("friction", Codec.FLOAT, MimicParticle::setFriction, 0.98f)
            .add("red", Codec.FLOAT, (p, f) -> p.red = f, 1f)
            .add("green", Codec.FLOAT, (p, f) -> p.green = f, 1f)
            .add("blue", Codec.FLOAT, (p, f) -> p.blue = f, 1f)
            .add("alpha", Codec.FLOAT, (p, f) -> p.alpha = f, 1f)
            .add("scale", Codec.FLOAT, (p, f) -> p.scale = f, 1f)
            .add("physics", Codec.BOOL, MimicParticle::setPhysics, true)
            .add("texture", ResourceLocation.CODEC, (p, rl) -> p.texture = rl, new ResourceLocation("block/dirt"))
            .codec();
    public static Codec<ValueAssigner<MimicParticle>> vaTickCodec = new ValueAssigner.Builder<MimicParticle>()
            .add("red", Codec.FLOAT, (p, f) -> p.red += f, 0f)
            .add("green", Codec.FLOAT, (p, f) -> p.green += f, 0f)
            .add("blue", Codec.FLOAT, (p, f) -> p.blue += f, 0f)
            .add("alpha", Codec.FLOAT, (p, f) -> p.alpha += f, 0f)
            .add("scale", Codec.FLOAT, (p, f) -> p.scale += f, 0f)
            //.add("texture", ResourceLocation.CODEC, (p, rl) -> p.texture = rl, new ResourceLocation("block/dirt"))
            .codec();
    public static Codec<MimicParticleOptions> codec = RecordCodecBuilder.create(inst -> inst.group(
            vaSetupCodec.fieldOf("setup").forGetter(i -> i.setup),
            vaTickCodec.fieldOf("tick").forGetter(i -> i.tick)
    ).apply(inst, MimicParticleOptions::new));

    @Override
    public ParticleType<?> getType() {
        return null;//NucleusRegistries.mimicParticle.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        ByteBufUtil.writeWithCodec(buf, this, codec);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()).toString();
    }

    protected record onSetup(int lifetime, float friction, float red, float green, float blue, float alpha, float scale, boolean physics, ResourceLocation texture) {

    }
}
