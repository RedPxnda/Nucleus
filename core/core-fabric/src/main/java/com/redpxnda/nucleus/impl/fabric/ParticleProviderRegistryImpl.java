package com.redpxnda.nucleus.impl.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParticleProviderRegistryImpl {
    /*
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
        //ParticleFactoryRegistry.getInstance().register(type, provider);
    }

    public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProviderRegistry.DeferredParticleProvider<T> provider) {
        //ParticleFactoryRegistry.getInstance().register(type, sprites ->
        //        provider.create(new ExtendedSpriteSetImpl(sprites)));
    }
         */
}
