package com.redpxnda.nucleus.forge;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.function.Function;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;

public class Shaders {
    public static final Function<ResourceProvider, ShaderInstance> TEST_SHADER_FUNC = rp -> {
        try {
            return new ShaderInstance(rp, new ResourceLocation(MOD_ID, "test"), DefaultVertexFormat.PARTICLE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public static ShaderInstance TEST_SHADER;

    public static ParticleRenderType TEST_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShader(() -> TEST_SHADER);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        public String toString() {
            return "NUCLEUS_TEST";
        }
    };
}
