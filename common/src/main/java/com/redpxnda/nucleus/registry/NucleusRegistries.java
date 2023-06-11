package com.redpxnda.nucleus.registry;

import com.google.common.base.Suppliers;
import com.redpxnda.nucleus.datapack.recipe.LuaHandlerRecipe;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;

public class NucleusRegistries {
    public static ResourceLocation loc(String str) {
        return new ResourceLocation(MOD_ID, str);
    }
    public static final Supplier<RegistrarManager> regs = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static Registrar<RecipeSerializer<?>> RS = regs.get().get(Registries.RECIPE_SERIALIZER);
    public static Registrar<ParticleType<?>> particles = regs.get().get(Registries.PARTICLE_TYPE);

    public static RegistrySupplier<RecipeSerializer<?>> luaHandlingRecipe = RS.register(loc("lua_handling"), LuaHandlerRecipe.Serializer::new);
    public static RegistrySupplier<SimpleParticleType> testParticle = particles.register(loc("test"), () -> new SimpleParticleType(false));

    public static void init() {
        var classLoading = NucleusRegistries.class;
    }
}
