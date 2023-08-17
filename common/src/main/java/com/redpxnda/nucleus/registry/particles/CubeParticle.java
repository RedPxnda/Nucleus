package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redpxnda.nucleus.client.Rendering;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

import static com.redpxnda.nucleus.Nucleus.loc;

public class CubeParticle extends DynamicPoseStackParticle {
    public static TextureAtlasSprite sprite = null;

    public boolean invert = false;
    public float xSize = 1;
    public float ySize = 1;
    public float zSize = 1;

    protected CubeParticle(CubeParticleOptions options, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(null, RenderType.translucent(), clientLevel, x, y, z, dx, dy, dz);
        applyOptions(options);

        bbHeight = scale;
        bbWidth = scale;

        if (sprite == null) sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(loc("item/blank"));
    }

    @Override
    public void setScale(float s) {
        super.setScale(s);
        bbHeight = scale;
        bbWidth = scale;
    }

    @Override
    public void applyOptions(DynamicParticleOptions options) {
        super.applyOptions(options);
        if (options instanceof CubeParticleOptions cbop) {
            invert = cbop.invert;
            xSize = cbop.xSize;
            ySize = cbop.ySize;
            zSize = cbop.zSize;
        }
    }

    @Override
    public void render(VertexConsumer vc, PoseStack stack, float x, float y, float z, Camera camera, float partialTick) {
        super.render(vc, stack, x, y, z, camera, partialTick);
        float xSize = invert ? this.xSize : -this.xSize;
        for (int i = 0; i < 6; i++) {
            Rendering.addQuad(
                    Rendering.CUBE[i], stack, vc,
                    red, green, blue, alpha,
                    xSize, ySize, zSize,
                    sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                    LightTexture.FULL_SKY);
        }
    }

    public static class Provider implements ParticleProvider<CubeParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(CubeParticleOptions particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new CubeParticle(particleOptions, clientLevel, d, e, f, g, h, i);
        }
    }
}
