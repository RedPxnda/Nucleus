package com.redpxnda.nucleus.registry.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.server.world.ServerWorld;

public abstract class RenderingMobEffect extends StatusEffect {
    protected RenderingMobEffect(StatusEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    /**
     * @return true if further entity rendering should be interrupted
     */
    @Environment(EnvType.CLIENT)
    public boolean renderPre(StatusEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, MatrixStack matrixStack, VertexConsumerProvider multiBufferSource, int packedLight) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void renderPost(StatusEffectInstance instance, LivingEntity entity, float entityYaw, float partialTick, MatrixStack matrixStack, VertexConsumerProvider multiBufferSource, int packedLight) {}

    /**
     * @return true if further hud rendering should be interrupted
     */
    @Environment(EnvType.CLIENT)
    public boolean renderHud(StatusEffectInstance instance, MinecraftClient minecraft, DrawContext graphics, float partialTick) {
        return false;
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributeMap, int i) {
        super.onRemoved(entity, attributeMap, i);
        if (entity.getWorld() instanceof ServerWorld level) {
            level.getPlayers(player -> true).forEach(player -> {
                player.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(entity.getId(), this));
            });
        }
    }

    /**
     * Make sure to enable ticking for your effect in order for updates to work
     */
    @Override
    public void applyUpdateEffect(LivingEntity entity, int i) {
        super.applyUpdateEffect(entity, i);
        if (tickUpdateInterval() < 1) return;
        if (entity.getWorld() instanceof ServerWorld level && level.getTime() % tickUpdateInterval() == 0) {
            StatusEffectInstance instance = entity.getStatusEffect(this);
            if (instance == null) return;

            level.getPlayers(player -> player.getPos().distanceTo(entity.getPos()) < maxTickUpdateDistance()).forEach(player -> {
                player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(entity.getId(), instance));
            });
        }
    }

    /**
     * The maximum distance players can be from the entity with this effect in order to have this effect synced.
     * See {@link #tickUpdateInterval()}
     */
    public double maxTickUpdateDistance() {
        return 100;
    }

    /**
     * How often updates to this effect should be synced to clients, in ticks.
     * Never sends updates on tick if set to -1.
     */
    public int tickUpdateInterval() {
        return 1;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributeMap, int i) {
        super.onApplied(entity, attributeMap, i);
        if (entity.getWorld() instanceof ServerWorld level) {
            StatusEffectInstance instance = entity.getStatusEffect(this);
            if (instance == null) return;

            level.getPlayers(player -> true).forEach(player -> {
                player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(entity.getId(), instance));
            });
        }
    }
}
