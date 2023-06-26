package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.datapack.codec.ValueAssigner;
import com.redpxnda.nucleus.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class MimicParticle extends DynamicPoseStackParticle {
    public ResourceLocation texture;

    public MimicParticle(
            ValueAssigner<MimicParticle> setup, ValueAssigner<MimicParticle> tick,
            ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        super(
                s -> setup.assignTo((MimicParticle) s), t -> tick.assignTo((MimicParticle) t), (p, ps, c) -> ps.mulPose(c.rotation()),
                null, RenderType.translucent(), clientLevel, x, y, z, xSpeed, ySpeed, zSpeed
        );
        this.sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<MimicParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(MimicParticleOptions o, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new MimicParticle(o.setup(), o.tick(), clientLevel, d, e, f, g, h, i);
        }
    }
}
