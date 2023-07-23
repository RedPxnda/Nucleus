package com.redpxnda.nucleus.registry.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class TestEffect extends RenderingMobEffect {
    public TestEffect() {
        super(MobEffectCategory.BENEFICIAL, 16284963);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderPre(MobEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight) {
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
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void renderPost(MobEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight) {
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderHud(MobEffectInstance instance, Minecraft minecraft, GuiGraphics graphics, float partialTick) {
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
