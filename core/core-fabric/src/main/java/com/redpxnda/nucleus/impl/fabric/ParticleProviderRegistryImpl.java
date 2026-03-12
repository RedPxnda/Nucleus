package com.redpxnda.nucleus.impl.fabric;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ParticleProviderRegistryImpl {
    public record ExtendedSpriteSetImpl(
            FabricSpriteProvider delegate
    ) implements ParticleProviderRegistry.ExtendedSpriteSet {
        @Override
        public TextureAtlas getAtlas() {
            return delegate.getAtlas();
        }

        @Override
        public List<TextureAtlasSprite> getSprites() {
            return delegate.getSprites();
        }

        @Override
        public TextureAtlasSprite get(int i, int j) {
            return delegate.get(i, j);
        }

        @Override
        public TextureAtlasSprite get(RandomSource random) {
            return delegate.get(random);
        }
    }

    public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
        ParticleFactoryRegistry.getInstance().register(type, provider);
    }

    public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProviderRegistry.DeferredParticleProvider<T> provider) {
        ParticleFactoryRegistry.getInstance().register(type, sprites ->
                provider.create(new ExtendedSpriteSetImpl(sprites)));
    }
}
