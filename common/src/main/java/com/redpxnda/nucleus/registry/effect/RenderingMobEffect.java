package com.redpxnda.nucleus.registry.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

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

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int i) {
        super.removeAttributeModifiers(entity, attributeMap, i);
        if (entity.level() instanceof ServerLevel level) {
            level.getPlayers(player -> true).forEach(player -> {
                player.connection.send(new ClientboundRemoveMobEffectPacket(entity.getId(), this));
            });
        }
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int i) {
        super.applyEffectTick(entity, i);
        if (tickUpdateInterval() < 1) return;
        if (entity.level() instanceof ServerLevel level && level.getGameTime() % tickUpdateInterval() == 0) {
            MobEffectInstance instance = entity.getEffect(this);
            if (instance == null) return;

            level.getPlayers(player -> player.position().distanceTo(entity.position()) > maxTickUpdateDistance()).forEach(player -> {
                player.connection.send(new ClientboundUpdateMobEffectPacket(entity.getId(), instance));
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
        return -1;
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int i) {
        super.addAttributeModifiers(entity, attributeMap, i);
        if (entity.level() instanceof ServerLevel level) {
            MobEffectInstance instance = entity.getEffect(this);
            if (instance == null) return;

            level.getPlayers(player -> true).forEach(player -> {
                player.connection.send(new ClientboundUpdateMobEffectPacket(entity.getId(), instance));
            });
        }
    }
}
