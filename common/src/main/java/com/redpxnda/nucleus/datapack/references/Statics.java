package com.redpxnda.nucleus.datapack.references;

import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.block.BlockReference;
import com.redpxnda.nucleus.datapack.references.effect.MobEffectInstanceReference;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.entity.LivingEntityReference;
import com.redpxnda.nucleus.datapack.references.item.ItemReference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import com.redpxnda.nucleus.datapack.references.storage.*;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import com.redpxnda.nucleus.datapack.references.tag.ListTagReference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

import static com.redpxnda.nucleus.util.MiscUtil.getMobEffect;

@SuppressWarnings("unused")
public class Statics {
    public static ItemStackReference EMPTY_ITEM = new ItemStackReference(ItemStack.EMPTY);

    public static BlockReference<?> blockOf(String block) {
        return new BlockReference<>(BuiltInRegistries.BLOCK.get(new ResourceLocation(block)));
    }
    public static ItemReference<?> itemOf(String item) {
        return new ItemReference<>(BuiltInRegistries.ITEM.get(new ResourceLocation(item)));
    }
    public static TargetingConditionsReference createTargetingConditions(boolean forCombat) {
        return new TargetingConditionsReference(forCombat ? TargetingConditions.forCombat() : TargetingConditions.forNonCombat());
    }
    public static AABBReference createAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AABBReference(new AABB(x1, y1, z1, x2, y2, z2));
    }
    public static BlockPosReference createBlockPos(int x, int y, int z) {
        return new BlockPosReference(new BlockPos(x, y, z));
    }
    public static Vec3Reference createVec3(double x, double y, double z) {
        return new Vec3Reference(new Vec3(x, y, z));
    }
    public static MobEffectInstanceReference createMEI(ResourceLocationReference reference) {
        return new MobEffectInstanceReference(new MobEffectInstance(getMobEffect(reference)));
    }
    public static MobEffectInstanceReference createMEI(ResourceLocationReference reference, int duration, int amplifier) {
        return new MobEffectInstanceReference(new MobEffectInstance(getMobEffect(reference), duration, amplifier));
    }
    public static MobEffectInstanceReference createMEI(ResourceLocationReference reference, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        // ambient defaults to false, visible defaults to true, showIcon defaults to true
        return new MobEffectInstanceReference(new MobEffectInstance(getMobEffect(reference), duration, amplifier, ambient, visible, showIcon));
    }
    public static Vec2Reference createVec2(float x, float y) {
        return new Vec2Reference(new Vec2(x, y));
    }
    public static ChunkPosReference createChunkPos(int x, int z) {
        return new ChunkPosReference(new ChunkPos(x, z));
    }
    public static CompoundTagReference createCompoundTag() {
        return new CompoundTagReference(new CompoundTag());
    }
    public static ListTagReference createListTag() {
        return new ListTagReference(new ListTag());
    }
    public static ResourceLocationReference createResourceLocation(String namespace, String path) {
        return new ResourceLocationReference(new ResourceLocation(namespace, path));
    }
    public static ResourceLocationReference createResourceLocation(String str) {
        return new ResourceLocationReference(new ResourceLocation(str));
    }

    public static class Entities {
        public static EntityReference<PrimedTnt> createTNT(LevelReference ref, double x, double y, double z) {
            return new EntityReference<>(new PrimedTnt(ref.instance, x, y, z, null));
        }
        public static EntityReference<PrimedTnt> createTNT(LevelReference ref, double x, double y, double z, LivingEntityReference<?> ent) {
            return new EntityReference<>(new PrimedTnt(ref.instance, x, y, z, ent.instance));
        }
        public static EntityReference<PrimedTnt> createTNT(LevelReference ref, double x, double y, double z, LivingEntityReference<?> ent, int fuse) {
            PrimedTnt tnt = new PrimedTnt(ref.instance, x, y, z, ent.instance);
            tnt.setFuse(fuse);
            return new EntityReference<>(tnt);
        }
    }

    public static class Components {
        public static ComponentReference.Mutable literal(String str) {
            return new ComponentReference.Mutable(Component.literal(str));
        }
        public static ComponentReference.Mutable translatable(String str) {
            return new ComponentReference.Mutable(Component.translatable(str));
        }
        public static ComponentReference.Mutable keybind(String str) {
            return new ComponentReference.Mutable(Component.keybind(str));
        }
    }

    public enum ChatFormattings {
        BLACK(ChatFormatting.BLACK),
        DARK_BLUE(ChatFormatting.DARK_BLUE),
        DARK_GREEN(ChatFormatting.DARK_GREEN),
        DARK_AQUA(ChatFormatting.DARK_AQUA),
        DARK_RED(ChatFormatting.DARK_RED),
        DARK_PURPLE(ChatFormatting.DARK_PURPLE),
        GOLD(ChatFormatting.GOLD),
        GRAY(ChatFormatting.GRAY),
        DARK_GRAY(ChatFormatting.DARK_GRAY),
        BLUE(ChatFormatting.BLUE),
        GREEN(ChatFormatting.GREEN),
        AQUA(ChatFormatting.AQUA),
        RED(ChatFormatting.RED),
        LIGHT_PURPLE(ChatFormatting.LIGHT_PURPLE),
        YELLOW(ChatFormatting.YELLOW),
        WHITE(ChatFormatting.WHITE),
        OBFUSCATED(ChatFormatting.OBFUSCATED),
        BOLD(ChatFormatting.BOLD),
        STRIKETHROUGH(ChatFormatting.STRIKETHROUGH),
        UNDERLINE(ChatFormatting.UNDERLINE),
        ITALIC(ChatFormatting.ITALIC),
        RESET(ChatFormatting.RESET);

        public final ChatFormatting instance;
        private static final Map<ChatFormatting, ChatFormattings> map = new HashMap<>(){{
            for (ChatFormattings value : ChatFormattings.values()) {
                put(value.instance, value);
            }}};

        ChatFormattings(ChatFormatting instance) {
            this.instance = instance;
        }

        public static ChatFormattings get(ChatFormatting orig) {
            return map.get(orig);
        }
    }

    public enum EquipmentSlots {
        MAINHAND(EquipmentSlot.MAINHAND),
        OFFHAND(EquipmentSlot.OFFHAND),
        FEET(EquipmentSlot.FEET),
        LEGS(EquipmentSlot.LEGS),
        CHEST(EquipmentSlot.CHEST),
        HEAD(EquipmentSlot.HEAD);

        public final EquipmentSlot instance;
        private static final Map<EquipmentSlot, EquipmentSlots> map = new HashMap<>(){{
            for (EquipmentSlots value : EquipmentSlots.values()) {
                put(value.instance, value);
            }}};

        EquipmentSlots(EquipmentSlot instance) {
            this.instance = instance;
        }

        public static EquipmentSlots get(EquipmentSlot orig) {
            return map.get(orig);
        }
    }


    public enum Directions {
        DOWN(Direction.DOWN),
        UP(Direction.UP),
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        WEST(Direction.WEST),
        EAST(Direction.EAST);

        public final Direction instance;
        private static final Map<Direction, Directions> map = new HashMap<>(){{
            for (Directions value : Directions.values()) {
                put(value.instance, value);
            }}};

        Directions(Direction instance) {
            this.instance = instance;
        }

        public static Directions get(Direction orig) {
            return map.get(orig);
        }
    }

    public enum Axes {
        X(Direction.Axis.X),
        Y(Direction.Axis.Y),
        Z(Direction.Axis.Z);

        public final Direction.Axis instance;
        private static final Map<Direction.Axis, Axes> map = new HashMap<>(){{
            for (Axes value : Axes.values()) {
                put(value.instance, value);
            }}};

        Axes(Direction.Axis instance) {
            this.instance = instance;
        }

        public static Axes get(Direction.Axis orig) {
            return map.get(orig);
        }
    }

    public enum InteractionHands {
        MAIN_HAND(InteractionHand.MAIN_HAND),
        OFF_HAND(InteractionHand.OFF_HAND);

        public final InteractionHand instance;
        private static final Map<InteractionHand, InteractionHands> map = new HashMap<>(){{
            for (InteractionHands value : InteractionHands.values()) {
                put(value.instance, value);
            }}};

        InteractionHands(InteractionHand instance) {
            this.instance = instance;
        }

        public static InteractionHands get(InteractionHand orig) {
            return map.get(orig);
        }
    }

    public enum Rotations {
        NONE(Rotation.NONE),
        CLOCKWISE_90(Rotation.CLOCKWISE_90),
        CLOCKWISE_180(Rotation.CLOCKWISE_180),
        COUNTERCLOCKWISE_90(Rotation.COUNTERCLOCKWISE_90);

        public final Rotation instance;
        private static final Map<Rotation, Rotations> map = new HashMap<>(){{
            for (Rotations value : Rotations.values()) {
                put(value.instance, value);
            }}};

        Rotations(Rotation instance) {
            this.instance = instance;
        }

        public static Rotations get(Rotation orig) {
            return map.get(orig);
        }
    }

    public enum UseAnims {
        NONE(UseAnim.NONE),
        EAT(UseAnim.EAT),
        DRINK(UseAnim.DRINK),
        BLOCK(UseAnim.BLOCK),
        BOW(UseAnim.BOW),
        SPEAR(UseAnim.SPEAR),
        CROSSBOW(UseAnim.CROSSBOW),
        SPYGLASS(UseAnim.SPYGLASS),
        TOOT_HORN(UseAnim.TOOT_HORN);

        public final UseAnim instance;
        private static final Map<UseAnim, UseAnims> map = new HashMap<>(){{
            for (UseAnims value : UseAnims.values()) {
                put(value.instance, value);
            }}};

        UseAnims(UseAnim instance) {
            this.instance = instance;
        }

        public static UseAnims get(UseAnim orig) {
            return map.get(orig);
        }
    }

    public enum Rarities {
        COMMON(Rarity.COMMON),
        UNCOMMON(Rarity.UNCOMMON),
        RARE(Rarity.RARE),
        EPIC(Rarity.EPIC);

        public final Rarity instance;
        private static final Map<Rarity, Rarities> map = new HashMap<>(){{
            for (Rarities value : Rarities.values()) {
                put(value.instance, value);
            }}};

        Rarities(Rarity instance) {
            this.instance = instance;
        }

        public static Rarities get(Rarity orig) {
            return map.get(orig);
        }
    }

    public enum Poses {
        STANDING(Pose.STANDING),
        FALL_FLYING(Pose.FALL_FLYING),
        SLEEPING(Pose.SLEEPING),
        SWIMMING(Pose.SWIMMING),
        SPIN_ATTACK(Pose.SPIN_ATTACK),
        CROUCHING(Pose.CROUCHING),
        LONG_JUMPING(Pose.LONG_JUMPING),
        DYING(Pose.DYING),
        CROAKING(Pose.CROAKING),
        USING_TONGUE(Pose.USING_TONGUE),
        ROARING(Pose.ROARING),
        SNIFFING(Pose.SNIFFING),
        EMERGING(Pose.EMERGING),
        DIGGING(Pose.DIGGING);

        public final Pose instance;
        private static final Map<Pose, Poses> map = new HashMap<>(){{
            for (Poses value : Poses.values()) {
                put(value.instance, value);
            }}};

        Poses(Pose instance) {
            this.instance = instance;
        }

        public static Poses get(Pose orig) {
            return map.get(orig);
        }
    }
}
