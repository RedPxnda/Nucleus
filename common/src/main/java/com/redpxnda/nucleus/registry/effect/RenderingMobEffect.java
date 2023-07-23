package com.redpxnda.nucleus.registry.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public abstract class RenderingMobEffect extends MobEffect {
    protected RenderingMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    /**
     * @return true if further entity rendering should be interrupted
     */
    @Environment(EnvType.CLIENT)
    public boolean renderPre(MobEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void renderPost(MobEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight) {}

    /**
     * @return true if further hud rendering should be interrupted
     */
    @Environment(EnvType.CLIENT)
    public boolean renderHud(MobEffectInstance instance, Minecraft minecraft, GuiGraphics graphics, float partialTick) {
        return false;
    }
}
