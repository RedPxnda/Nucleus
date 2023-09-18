package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.codec.ValueAssigner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MimicParticle extends DynamicTextureSheetParticle implements MimicParticleOptions.Manager {
    public Identifier texture;
    public final ValueAssigner<MimicParticleOptions.Manager> tick;

    public MimicParticle(
            ValueAssigner<MimicParticleOptions.Manager> setup, ValueAssigner<MimicParticleOptions.Manager> tick,
            ClientWorld clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        super(null, RenderLayer.getCutout(), clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        setup.assignTo(this);
        this.tick = tick;

        this.sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(texture);
    }

    @Override
    public void tick() {
        super.tick();
        tick.assignTo(this);
    }

    @Override
    public Identifier getTexture() {
        return texture;
    }

    @Override
    public void setTexture(Identifier rl) {
        texture = rl;
    }

    public static class Provider implements ParticleFactory<MimicParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(MimicParticleOptions o, ClientWorld clientLevel, double d, double e, double f, double g, double h, double i) {
            return new MimicParticle(o.setup, o.tick, clientLevel, d, e, f, g, h, i);
        }
    }
}
