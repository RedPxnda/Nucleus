package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.client.Rendering;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;

public abstract class DynamicTextureSheetParticle extends DynamicPoseStackParticle {
    public Sprite sprite;
    protected boolean rotateWithCamera = true;

    protected DynamicTextureSheetParticle(MatrixStack stack, RenderLayer renderType, ClientWorld clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(stack, renderType, clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void setSprite(Sprite textureAtlasSprite) {
        this.sprite = textureAtlasSprite;
    }

    public float getU0() {
        return this.sprite.getMinU();
    }

    public float getU1() {
        return this.sprite.getMaxU();
    }

    public float getV0() {
        return this.sprite.getMinV();
    }

    public float getV1() {
        return this.sprite.getMaxV();
    }

    public void pickSprite(SpriteProvider spriteSet) {
        this.setSprite(spriteSet.getSprite(this.random));
    }

    public void setSpriteFromAge(SpriteProvider spriteSet) {
        if (!this.dead) {
            this.setSprite(spriteSet.getSprite(this.age, this.maxAge));
        }
    }

    @Override
    public void render(VertexConsumer vc, MatrixStack stack, float x, float y, float z, Camera camera, float partialTick) {
        super.render(vc, stack, x, y, z, camera, partialTick);
        if (rotateWithCamera) stack.multiply(camera.getRotation());
        Rendering.addQuad(
                Rendering.QUAD,
                stack, vc,
                red, green, blue, alpha,
                1, 1, 1,
                getU0(), getU1(), getV0(), getV1(),
                LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE
        );
    }
}
