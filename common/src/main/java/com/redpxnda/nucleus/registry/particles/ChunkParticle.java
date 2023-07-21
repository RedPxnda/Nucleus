package com.redpxnda.nucleus.registry.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.jetbrains.annotations.Nullable;

public class ChunkParticle extends DynamicTextureSheetParticle {
    public float uo, vo;

    protected ChunkParticle(ChunkParticleOptions options, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(null, RenderType.solid(), clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        applyOptions(options);
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2f;
        this.setPhysicsEnabled(true);
        this.gravity = 1f;
    }

    @Override
    public void applyOptions(DynamicParticleOptions options) {
        super.applyOptions(options);
        if (options instanceof ChunkParticleOptions o) {
            this.sprite = o.block == null ?
                    Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(o.texture) :
                    Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(o.block.defaultBlockState());
        }
    }

    @Override
    public float getU0() {
        return this.sprite.getU((this.uo + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    public float getU1() {
        return this.sprite.getU(this.uo / 4.0f * 16.0f);
    }

    @Override
    public float getV0() {
        return this.sprite.getV(this.vo / 4.0f * 16.0f);
    }

    @Override
    public float getV1() {
        return this.sprite.getV((this.vo + 1.0f) / 4.0f * 16.0f);
    }

    public static class Provider implements ParticleProvider<ChunkParticleOptions> {

        @Nullable
        @Override
        public Particle createParticle(ChunkParticleOptions particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new ChunkParticle(particleOptions, clientLevel, d, e, f, g, h, i);
        }
    }
}
