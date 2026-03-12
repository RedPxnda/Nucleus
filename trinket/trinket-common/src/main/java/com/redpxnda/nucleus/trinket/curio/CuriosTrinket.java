package com.redpxnda.nucleus.trinket.curio;

import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.trinket.Trinket;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class CuriosTrinket implements ICurioItem {
    Trinket trinket;

    public CuriosTrinket(Trinket trinket) {
        this.trinket = trinket;
    }


    public Trinket getTrinket() {
        return trinket;
    }

    public LivingEntity entity(SlotContext ctx) {
        return ctx.entity();
    }

    /* ---------------- tick ---------------- */

    @Override
    public void curioTick(SlotContext ctx, ItemStack stack) {
        LivingEntity entity = entity(ctx);
        if (entity != null)
            getTrinket().tick(stack, entity, CurioApiMirror.convert(ctx));
    }

    /* ---------------- equip / unequip ---------------- */

    @Override
    public void onEquip(SlotContext ctx, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = entity(ctx);
        if (entity != null)
            getTrinket().onEquip(stack, entity, CurioApiMirror.convert(ctx));
    }

    @Override
    public void onUnequip(SlotContext ctx, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = entity(ctx);
        if (entity != null)
            getTrinket().onUnequip(stack, entity, CurioApiMirror.convert(ctx));
    }

    /* ---------------- equip rules ---------------- */

    @Override
    public boolean canEquip(SlotContext ctx, ItemStack stack) {
        LivingEntity entity = entity(ctx);
        return entity != null && getTrinket().canEquip(stack, entity, CurioApiMirror.convert(ctx));
    }

    @Override
    public boolean canUnequip(SlotContext ctx, ItemStack stack) {
        LivingEntity entity = entity(ctx);
        return entity != null && getTrinket().canUnequip(stack, entity, CurioApiMirror.convert(ctx));
    }

    /* ---------------- attributes ---------------- */

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext ctx, ResourceLocation id, ItemStack stack) {
        return trinket.getAttributeModifiers(CurioApiMirror.convert(ctx), id, stack);
    }

    /* ---------------- drop rule ---------------- */

    @Override
    public ICurio.@NotNull DropRule getDropRule(
            SlotContext ctx,
            net.minecraft.world.damagesource.DamageSource source,
            boolean recentlyHit,
            ItemStack stack) {

        LivingEntity entity = entity(ctx);
        if (entity == null)
            return ICurioItem.super.getDropRule(ctx, source, recentlyHit, stack);

        Trinket.DropRule rule =
                getTrinket().getDropRule(stack, entity, ctx.index());

        return switch (rule) {
            case KEEP -> ICurio.DropRule.ALWAYS_KEEP;
            case DROP -> ICurio.DropRule.ALWAYS_DROP;
            case DESTROY -> ICurio.DropRule.DESTROY;
            case DEFAULT -> ICurio.DropRule.DEFAULT;
        };
    }
}