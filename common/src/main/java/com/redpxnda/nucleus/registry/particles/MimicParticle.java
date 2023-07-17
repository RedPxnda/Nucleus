package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redpxnda.nucleus.datapack.codec.ValueAssigner;
import com.redpxnda.nucleus.util.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class MimicParticle extends DynamicTextureSheetParticle implements MimicParticleOptions.Manager {
    public ResourceLocation texture;
    public final ValueAssigner<MimicParticleOptions.Manager> tick;

    public MimicParticle(
            ValueAssigner<MimicParticleOptions.Manager> setup, ValueAssigner<MimicParticleOptions.Manager> tick,
            ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        super(null, RenderType.cutout(), clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        setup.assignTo(this);
        this.tick = tick;

        this.sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
    }

    @Override
    public void tick() {
        super.tick();
        tick.assignTo(this);
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void setTexture(ResourceLocation rl) {
        texture = rl;
    }

    public static class Provider implements ParticleProvider<MimicParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(MimicParticleOptions o, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new MimicParticle(o.setup, o.tick, clientLevel, d, e, f, g, h, i);
        }
    }
}
