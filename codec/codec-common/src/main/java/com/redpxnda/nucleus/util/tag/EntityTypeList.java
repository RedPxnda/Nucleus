package com.redpxnda.nucleus.util.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.EntityTypeListCodec;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.village.Merchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@AutoCodec.Override()
public class EntityTypeList extends TagList<EntityType<?>> {
    public static final Codec<EntityTypeList> CODEC = EntityTypeListCodec.INSTANCE;
    public static final Map<String, Predicate<Entity>> builtinPredicates = MiscUtil.initialize(new HashMap<>(), m -> {
        m.put("tamables", e -> e instanceof Tameable);
        m.put("animals", e -> e instanceof AnimalEntity);
        m.put("merchants", e -> e instanceof Merchant);
        m.put("mobs", e -> e instanceof MobEntity);
        m.put("chested_horses", e -> e instanceof AbstractDonkeyEntity);
        m.put("horse_likes", e -> e instanceof AbstractHorseEntity);
    });

    public static EntityTypeList of() {
        return new EntityTypeList(List.of(), List.of(), List.of());
    }

    public static EntityTypeList of(EntityType<?>... entities) {
        return new EntityTypeList(List.of(entities), List.of(), List.of());
    }

    @SafeVarargs
    public static EntityTypeList of(TagKey<EntityType<?>>... tags) {
        return new EntityTypeList(List.of(), List.of(tags), List.of());
    }

    protected final List<String> builtins;

    public EntityTypeList(List<EntityType<?>> objects, List<TagKey<EntityType<?>>> tags, List<String> builtins) {
        super(objects, tags, Registries.ENTITY_TYPE, RegistryKeys.ENTITY_TYPE);
        this.builtins = builtins;
    }

    public boolean contains(Entity obj) {
        if (super.contains(obj.getType())) return true;
        for (String builtin : builtins) {
            Predicate<Entity> predicate = builtinPredicates.get(builtin);
            if (predicate != null && predicate.test(obj)) return true;
        }
        return false;
    }

    public List<String> getBuiltins() {
        return builtins;
    }
}
