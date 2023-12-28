package com.redpxnda.nucleus.trinket.impl.forge;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.trinket.curiotrinket.CurioTrinket;
import com.redpxnda.nucleus.trinket.curiotrinket.CurioTrinketRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.common.capability.CurioItemCapability;
import top.theillusivec4.curios.common.capability.ItemizedCurioCapability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrinketItemCreatorImpl {
    public static final Map<Item, ICurioItem> CURIOS = new HashMap<>(); // curio but plural, not the mod name - my variable naming makes sense I promise 😭

    public static void registerCurioTrinket(Item item, CurioTrinket trinket) {
        ICurioItem curio = new ICurioItem() {
            @Override
            public void curioTick(SlotContext slotContext, ItemStack stack) {
                trinket.tick(stack, slotContext.entity(), slotContext.index());
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
                trinket.onEquip(stack, slotContext.entity(), slotContext.index());
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
                trinket.onUnequip(stack, slotContext.entity(), slotContext.index());
            }

            @Override
            public boolean canEquip(SlotContext slotContext, ItemStack stack) {
                return trinket.canEquip(stack, slotContext.entity(), slotContext.index());
            }

            @Override
            public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
                return trinket.canUnequip(stack, slotContext.entity(), slotContext.index());
            }

            @Override
            public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
                Multimap<EntityAttribute, EntityAttributeModifier> map = trinket.useNbtAttributeBehavior(stack, slotContext.entity(), slotContext.index(), uuid) ?
                        ICurioItem.super.getAttributeModifiers(slotContext, uuid, stack) :
                        HashMultimap.create();
                map.putAll(trinket.getAttributeModifiers(stack, slotContext.entity(), slotContext.index(), uuid));
                return map;
            }

            @NotNull
            @Override
            public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
                CurioTrinket.DropRule dropRule = trinket.getDropRule(stack, slotContext.entity(), slotContext.index());
                return switch (dropRule) {
                    case KEEP -> ICurio.DropRule.ALWAYS_KEEP;
                    case DROP -> ICurio.DropRule.ALWAYS_DROP;
                    case DESTROY -> ICurio.DropRule.DESTROY;
                    case DEFAULT -> ICurio.DropRule.DEFAULT;
                };
            }
        };
        CURIOS.put(item, curio);
    }

    public static void attachCuriosCaps(final AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        ICurioItem curio = CURIOS.get(stack.getItem());
        if (curio != null) {
            ItemizedCurioCapability capability = new ItemizedCurioCapability(curio, stack);
            event.addCapability(CuriosCapability.ID_ITEM, CurioItemCapability.createProvider(capability));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerCurioTrinketRenderer(Item item, CurioTrinketRenderer renderer) {
        CuriosRendererRegistry.register(item, () -> new CustomCurioRenderer(renderer));
    }

    @OnlyIn(Dist.CLIENT)
    private record CustomCurioRenderer(CurioTrinketRenderer delegate) implements ICurioRenderer {
        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(
                ItemStack stack, SlotContext slotContext, MatrixStack matrixStack,
                FeatureRendererContext<T, M> renderLayerParent, VertexConsumerProvider renderTypeBuffer, int light,
                float limbSwing, float limbSwingAmount,
                float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            delegate.render(
                    stack, slotContext.entity(), slotContext.index(),
                    matrixStack, renderLayerParent.getModel(), renderTypeBuffer,
                    light, limbSwing, limbSwingAmount, partialTicks,
                    ageInTicks, netHeadYaw, headPitch
            );
        }
    }
}
