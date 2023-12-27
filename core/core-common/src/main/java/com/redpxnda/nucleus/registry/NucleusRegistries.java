package com.redpxnda.nucleus.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Supplier;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;

public class NucleusRegistries {
    public static final Supplier<RegistrarManager> regs = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    //public static Registrar<RecipeSerializer<?>> RS = regs.get().get(RegistryKeys.RECIPE_SERIALIZER);
    public static Registrar<ParticleType<?>> particles = regs.get().get(RegistryKeys.PARTICLE_TYPE);
    public static Registrar<StatusEffect> effects = regs.get().get(RegistryKeys.STATUS_EFFECT);
    public static Registrar<Item> items = regs.get().get(RegistryKeys.ITEM);

    //public static RegistrySupplier<TestEffect> testEffect = effects.register(loc("test"), () -> new TestEffect());

    //public static RegistrySupplier<CuriousTrinketItem> testTrinket = items.register(loc("test_trinket"), () -> new CuriousTrinketItem(new Item.Properties()));

    public static void init() {
        var classLoading = NucleusRegistries.class;
    }
}
