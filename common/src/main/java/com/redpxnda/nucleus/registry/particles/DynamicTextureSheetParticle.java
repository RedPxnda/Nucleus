package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redpxnda.nucleus.client.Rendering;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class DynamicTextureSheetParticle extends DynamicPoseStackParticle {
    public TextureAtlasSprite sprite;
    protected boolean rotateWithCamera = true;

    protected DynamicTextureSheetParticle(PoseStack stack, RenderType renderType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(stack, renderType, clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void setSprite(TextureAtlasSprite textureAtlasSprite) {
        this.sprite = textureAtlasSprite;
    }

    public float getU0() {
        return this.sprite.getU0();
    }

    public float getU1() {
        return this.sprite.getU1();
    }

    public float getV0() {
        return this.sprite.getV0();
    }

    public float getV1() {
        return this.sprite.getV1();
    }

    public void pickSprite(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(this.random));
    }

    public void setSpriteFromAge(SpriteSet spriteSet) {
        if (!this.removed) {
            this.setSprite(spriteSet.get(this.age, this.lifetime));
        }
    }

    @Override
    public void render(VertexConsumer vc, PoseStack stack, float x, float y, float z, Camera camera, float partialTick) {
        super.render(vc, stack, x, y, z, camera, partialTick);
        if (rotateWithCamera) stack.mulPose(camera.rotation());
        Rendering.addQuad(
                Rendering.QUAD,
                stack, vc,
                red, green, blue, alpha,
                1, 1, 1,
                getU0(), getU1(), getV0(), getV1(),
                LightTexture.FULL_SKY
        );
    }
}
