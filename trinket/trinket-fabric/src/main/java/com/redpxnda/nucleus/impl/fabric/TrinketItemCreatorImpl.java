package com.redpxnda.nucleus.impl.fabric;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.compat.trinkets.CurioTrinket;
import com.redpxnda.nucleus.compat.trinkets.CurioTrinketRenderer;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.UUID;

public class TrinketItemCreatorImpl {
    public static void registerCurioTrinket(Item item, CurioTrinket trinket) {
        Trinket t = new Trinket() {
            @Override
            public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
                trinket.tick(stack, entity, slot.index());
            }

            @Override
            public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                trinket.onEquip(stack, entity, slot.index());
            }

            @Override
            public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                trinket.onUnequip(stack, entity, slot.index());
            }

            @Override
            public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                return trinket.canEquip(stack, entity, slot.index());
            }

            @Override
            public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                return trinket.canUnequip(stack, entity, slot.index());
            }

            @Override
            public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
                Multimap<EntityAttribute, EntityAttributeModifier> map = trinket.useNbtAttributeBehavior(stack, entity, slot.index(), uuid) ?
                        Trinket.super.getModifiers(stack, slot, entity, uuid) :
                        HashMultimap.create();
                map.putAll(trinket.getAttributeModifiers(stack, entity, slot.index(), uuid));
                return map;
            }

            @Override
            public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
                CurioTrinket.DropRule dropRule = trinket.getDropRule(stack, entity, slot.index());
                return switch (dropRule) {
                    case KEEP -> TrinketEnums.DropRule.KEEP;
                    case DROP -> TrinketEnums.DropRule.DROP;
                    case DESTROY -> TrinketEnums.DropRule.DESTROY;
                    case DEFAULT -> TrinketEnums.DropRule.DEFAULT;
                };
            }
        };
        TrinketsApi.registerTrinket(item, t);
    }

    @Environment(EnvType.CLIENT)
    public static void registerCurioTrinketRenderer(Item item, CurioTrinketRenderer renderer) {
        TrinketRendererRegistry.registerRenderer(item, new CustomTrinketRenderer(renderer));
    }

    @Environment(EnvType.CLIENT)
    private record CustomTrinketRenderer(CurioTrinketRenderer delegate) implements TrinketRenderer {
        @Override
        public void render(ItemStack itemStack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel,
                           MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int light,
                           LivingEntity livingEntity, float limbAngle, float limbDistance,
                           float tickDelta, float animationProgress, float headYaw,
                           float headPitch) {
            delegate.render(
                    itemStack, livingEntity, slotReference.index(), poseStack, entityModel, multiBufferSource, light, limbAngle, limbDistance,
                    tickDelta, animationProgress, headYaw, headPitch
            );
        }
    }
}
