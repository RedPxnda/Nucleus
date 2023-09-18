package com.redpxnda.nucleus.registry.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;

public class TestEffect extends RenderingMobEffect {
    public TestEffect() {
        super(StatusEffectCategory.BENEFICIAL, 16284963);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderPre(StatusEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, MatrixStack matrixStack, VertexConsumerProvider multiBufferSource, int packedLight) {
        float jumpAmount = 0.25f;
        if (entity.getWorld().getTime() % 20 == 0)
            jumpAmount = MathHelper.lerp(partialTick, 0.25f, 1);
        matrixStack.scale(jumpAmount, jumpAmount, jumpAmount);
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void renderPost(StatusEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, MatrixStack matrixStack, VertexConsumerProvider multiBufferSource, int packedLight) {
        /*matrixStack.pushPose();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("nucleus", "item/blank"));
        matrixStack.translate(0, 1, 0);
        RenderUtil.addQuad(
                RenderUtil.QUAD, matrixStack, multiBufferSource.getBuffer(RenderType.translucent()),
                1, 1, 1, 1, 1, 1, 1,
                sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                packedLight
                );
        matrixStack.popPose();*/
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderHud(StatusEffectInstance instance, MinecraftClient minecraft, DrawContext graphics, float partialTick) {
        /*graphics.pose().pushPose();
        int i = graphics.guiWidth();
        int j = graphics.guiHeight();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        graphics.setColor(0.85f, 0f, 0f, 1.0f);
        graphics.blit(new ResourceLocation("textures/misc/nausea.png"), 0, 0, -90, 0.0f, 0.0f, i, j, i, j);
        graphics.setColor(1f, 1f, 1f, 1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        graphics.pose().popPose();*/
        return false;
    }
}
