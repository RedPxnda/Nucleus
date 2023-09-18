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
import dev.architectury.platform.Platform;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static com.redpxnda.nucleus.util.MiscUtil.getMobEffect;

@SuppressWarnings("unused")
public class Statics {
    public static ItemStackReference EMPTY_ITEM = new ItemStackReference(ItemStack.EMPTY);

    public static boolean isModLoaded(String modId) {
        return Platform.isModLoaded(modId);
    }
    public static boolean isOnFabric() {
        return Platform.isFabric();
    }
    public static boolean isOnForge() {
        return Platform.isForge();
    }
    public static BlockReference<?> blockOf(String block) {
        return new BlockReference<>(Registries.BLOCK.get(new Identifier(block)));
    }
    public static ItemReference<?> itemOf(String item) {
        return new ItemReference<>(Registries.ITEM.get(new Identifier(item)));
    }
    public static TargetingConditionsReference createTargetingConditions(boolean forCombat) {
        return new TargetingConditionsReference(forCombat ? TargetPredicate.createAttackable() : TargetPredicate.createNonAttackable());
    }
    public static AABBReference createAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AABBReference(new Box(x1, y1, z1, x2, y2, z2));
    }
    public static BlockPosReference createBlockPos(int x, int y, int z) {
        return new BlockPosReference(new BlockPos(x, y, z));
    }
    public static Vec3Reference createVec3(double x, double y, double z) {
        return new Vec3Reference(new Vec3d(x, y, z));
    }
    public static MobEffectInstanceReference createMEI(ResourceLocationReference reference) {
        return new MobEffectInstanceReference(new StatusEffectInstance(getMobEffect(reference)));
    }
    public static MobEffectInstanceReference createMEI(ResourceLocationReference reference, int duration, int amplifier) {
        return new MobEffectInstanceReference(new StatusEffectInstance(getMobEffect(reference), duration, amplifier));
    }
    public static MobEffectInstanceReference createMEI(ResourceLocationReference reference, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        // ambient defaults to false, visible defaults to true, showIcon defaults to true
        return new MobEffectInstanceReference(new StatusEffectInstance(getMobEffect(reference), duration, amplifier, ambient, visible, showIcon));
    }
    public static Vec2Reference createVec2(float x, float y) {
        return new Vec2Reference(new Vec2f(x, y));
    }
    public static ChunkPosReference createChunkPos(int x, int z) {
        return new ChunkPosReference(new ChunkPos(x, z));
    }
    public static CompoundTagReference createCompoundTag() {
        return new CompoundTagReference(new NbtCompound());
    }
    public static ListTagReference createListTag() {
        return new ListTagReference(new NbtList());
    }
    public static ResourceLocationReference createResourceLocation(String namespace, String path) {
        return new ResourceLocationReference(new Identifier(namespace, path));
    }
    public static ResourceLocationReference createResourceLocation(String str) {
        return new ResourceLocationReference(new Identifier(str));
    }

    public static class Entities {
        public static EntityReference<TntEntity> createTNT(LevelReference ref, double x, double y, double z) {
            return new EntityReference<>(new TntEntity(ref.instance, x, y, z, null));
        }
        public static EntityReference<TntEntity> createTNT(LevelReference ref, double x, double y, double z, LivingEntityReference<?> ent) {
            return new EntityReference<>(new TntEntity(ref.instance, x, y, z, ent.instance));
        }
        public static EntityReference<TntEntity> createTNT(LevelReference ref, double x, double y, double z, LivingEntityReference<?> ent, int fuse) {
            TntEntity tnt = new TntEntity(ref.instance, x, y, z, ent.instance);
            tnt.setFuse(fuse);
            return new EntityReference<>(tnt);
        }
    }

    public static class Components {
        public static ComponentReference.Mutable literal(String str) {
            return new ComponentReference.Mutable(Text.literal(str));
        }
        public static ComponentReference.Mutable translatable(String str) {
            return new ComponentReference.Mutable(Text.translatable(str));
        }
        public static ComponentReference.Mutable keybind(String str) {
            return new ComponentReference.Mutable(Text.keybind(str));
        }
    }

    public enum ChatFormattings {
        BLACK(Formatting.BLACK),
        DARK_BLUE(Formatting.DARK_BLUE),
        DARK_GREEN(Formatting.DARK_GREEN),
        DARK_AQUA(Formatting.DARK_AQUA),
        DARK_RED(Formatting.DARK_RED),
        DARK_PURPLE(Formatting.DARK_PURPLE),
        GOLD(Formatting.GOLD),
        GRAY(Formatting.GRAY),
        DARK_GRAY(Formatting.DARK_GRAY),
        BLUE(Formatting.BLUE),
        GREEN(Formatting.GREEN),
        AQUA(Formatting.AQUA),
        RED(Formatting.RED),
        LIGHT_PURPLE(Formatting.LIGHT_PURPLE),
        YELLOW(Formatting.YELLOW),
        WHITE(Formatting.WHITE),
        OBFUSCATED(Formatting.OBFUSCATED),
        BOLD(Formatting.BOLD),
        STRIKETHROUGH(Formatting.STRIKETHROUGH),
        UNDERLINE(Formatting.UNDERLINE),
        ITALIC(Formatting.ITALIC),
        RESET(Formatting.RESET);

        public final Formatting instance;
        private static final Map<Formatting, ChatFormattings> map = new HashMap<>(){{
            for (ChatFormattings value : ChatFormattings.values()) {
                put(value.instance, value);
            }}};

        ChatFormattings(Formatting instance) {
            this.instance = instance;
        }

        public static ChatFormattings get(Formatting orig) {
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
        MAIN_HAND(Hand.MAIN_HAND),
        OFF_HAND(Hand.OFF_HAND);

        public final Hand instance;
        private static final Map<Hand, InteractionHands> map = new HashMap<>(){{
            for (InteractionHands value : InteractionHands.values()) {
                put(value.instance, value);
            }}};

        InteractionHands(Hand instance) {
            this.instance = instance;
        }

        public static InteractionHands get(Hand orig) {
            return map.get(orig);
        }
    }

    public enum Rotations {
        NONE(BlockRotation.NONE),
        CLOCKWISE_90(BlockRotation.CLOCKWISE_90),
        CLOCKWISE_180(BlockRotation.CLOCKWISE_180),
        COUNTERCLOCKWISE_90(BlockRotation.COUNTERCLOCKWISE_90);

        public final BlockRotation instance;
        private static final Map<BlockRotation, Rotations> map = new HashMap<>(){{
            for (Rotations value : Rotations.values()) {
                put(value.instance, value);
            }}};

        Rotations(BlockRotation instance) {
            this.instance = instance;
        }

        public static Rotations get(BlockRotation orig) {
            return map.get(orig);
        }
    }

    public enum UseAnims {
        NONE(UseAction.NONE),
        EAT(UseAction.EAT),
        DRINK(UseAction.DRINK),
        BLOCK(UseAction.BLOCK),
        BOW(UseAction.BOW),
        SPEAR(UseAction.SPEAR),
        CROSSBOW(UseAction.CROSSBOW),
        SPYGLASS(UseAction.SPYGLASS),
        TOOT_HORN(UseAction.TOOT_HORN);

        public final UseAction instance;
        private static final Map<UseAction, UseAnims> map = new HashMap<>(){{
            for (UseAnims value : UseAnims.values()) {
                put(value.instance, value);
            }}};

        UseAnims(UseAction instance) {
            this.instance = instance;
        }

        public static UseAnims get(UseAction orig) {
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
        STANDING(EntityPose.STANDING),
        FALL_FLYING(EntityPose.FALL_FLYING),
        SLEEPING(EntityPose.SLEEPING),
        SWIMMING(EntityPose.SWIMMING),
        SPIN_ATTACK(EntityPose.SPIN_ATTACK),
        CROUCHING(EntityPose.CROUCHING),
        LONG_JUMPING(EntityPose.LONG_JUMPING),
        DYING(EntityPose.DYING),
        CROAKING(EntityPose.CROAKING),
        USING_TONGUE(EntityPose.USING_TONGUE),
        ROARING(EntityPose.ROARING),
        SNIFFING(EntityPose.SNIFFING),
        EMERGING(EntityPose.EMERGING),
        DIGGING(EntityPose.DIGGING);

        public final EntityPose instance;
        private static final Map<EntityPose, Poses> map = new HashMap<>(){{
            for (Poses value : Poses.values()) {
                put(value.instance, value);
            }}};

        Poses(EntityPose instance) {
            this.instance = instance;
        }

        public static Poses get(EntityPose orig) {
            return map.get(orig);
        }
    }
}
