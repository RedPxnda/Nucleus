package com.redpxnda.nucleus.registry.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.util.RenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class TestEffect extends RenderingMobEffect {
    public TestEffect() {
        super(MobEffectCategory.BENEFICIAL, 16284963);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void render(MobEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight) {
        matrixStack.pushPose();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("nucleus", "item/blank"));
        matrixStack.translate(0, 1, 0);
        RenderUtil.addQuad(
                RenderUtil.QUAD, matrixStack, multiBufferSource.getBuffer(RenderType.translucent()),
                1, 1, 1, 1, 1, 1, 1,
                sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                packedLight
                );
        matrixStack.popPose();
    }
}
