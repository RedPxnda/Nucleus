package com.redpxnda.nucleus.registry.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public abstract class RenderingMobEffect extends MobEffect {
    protected RenderingMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Environment(EnvType.CLIENT)
    public void renderPre(MobEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight) {}

    @Environment(EnvType.CLIENT)
    public abstract void renderPost(MobEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight);
}
