package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.codec.ValueAssigner;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.registry.particles.manager.DynamicParticleManager;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class MimicParticleOptions implements ParticleOptions {
    public ValueAssigner<Manager> setup, tick;

    public static ValueAssigner.CodecBuilder<Manager> setupBuilder = DynamicParticleManager.setupBuilder.extend(Manager.class)
            .add("texture", ResourceLocation.CODEC, Manager::setTexture, new ResourceLocation("item/stick"));
    public static ValueAssigner.CodecBuilder<Manager> tickBuilder = DynamicParticleManager.tickBuilder.extend(Manager.class);
    public static Codec<ValueAssigner<Manager>> vaSetupCodec = setupBuilder.build();
    public static Codec<ValueAssigner<Manager>> vaTickCodec = tickBuilder.build();
    public static Codec<MimicParticleOptions> codec = RecordCodecBuilder.create(inst -> inst.group(
            vaSetupCodec.fieldOf("setup").forGetter(i -> i.setup),
            vaTickCodec.fieldOf("tick").forGetter(i -> i.tick)
    ).apply(inst, MimicParticleOptions::new));

    public MimicParticleOptions(ValueAssigner<Manager> setup, ValueAssigner<Manager> tick) {
        this.setup = setup;
        this.tick = tick;
    }

    public MimicParticleOptions() {
        this.setup = setupBuilder.toAssigner().build();
        this.tick = tickBuilder.toAssigner().build();
    }

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
