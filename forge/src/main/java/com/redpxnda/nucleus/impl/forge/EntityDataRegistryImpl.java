package com.redpxnda.nucleus.impl.forge;

import com.redpxnda.nucleus.capability.EntityCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityDataRegistryImpl {
    public static final Map<Class<? extends EntityCapability>, Holder<?>> CAPABILITIES = new HashMap<>();
    public static final Map<ResourceLocation, Class<? extends EntityCapability>> REGISTERED = new HashMap<>();

    public static <T extends EntityCapability> void register(ResourceLocation id, Predicate<Entity> entity, Class<T> cap, Supplier<T> creator) {
        Function<Capability<T>, CapProvider> provider = capability -> new CapProvider() {
            @Override
            public void invalidate() {
                lazy.invalidate();
            }

            @Override
            public CompoundTag serializeNBT() {
                return instance().toNbt();
            }

            @Override
            public void deserializeNBT(CompoundTag tag) {
                instance().loadNbt(tag);
            }

            private T instance = null;
            private final LazyOptional<T> lazy = LazyOptional.of(this::instance);

            private T instance() {
                if (instance == null) instance = creator.get();
                return instance;
            }

            @Override
            public @NotNull <A> LazyOptional<A> getCapability(@NotNull Capability<A> arg1, @Nullable Direction arg) {
                return capability.orEmpty(arg1, lazy);
            }
        };

        CAPABILITIES.put(cap, new Holder<>(id, provider, entity));
        REGISTERED.put(id, cap);
    }

    public static Class<? extends EntityCapability> getFromId(ResourceLocation id) {
        return REGISTERED.get(id);
    }

    public static class Holder<T extends Capability<?>> {
        public final ResourceLocation id;
        public final Function<T, CapProvider> provider;
        public Capability<?> capability;
        public final Predicate<Entity> predicate;

        public Holder(ResourceLocation id, Function<T, CapProvider> provider, Predicate<Entity> predicate) {
            this.id = id;
            this.provider = provider;
            this.predicate = predicate;
        }
    }
    public interface CapProvider extends ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
        void invalidate();
    }
}
