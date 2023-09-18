package com.redpxnda.nucleus.registry;

import com.google.common.base.Suppliers;
import com.redpxnda.nucleus.datapack.recipe.LuaHandlerRecipe;
import com.redpxnda.nucleus.registry.particles.*;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Supplier;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;
import static com.redpxnda.nucleus.Nucleus.loc;

public class NucleusRegistries {
    public static final Supplier<RegistrarManager> regs = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static Registrar<RecipeSerializer<?>> RS = regs.get().get(RegistryKeys.RECIPE_SERIALIZER);
    public static Registrar<ParticleType<?>> particles = regs.get().get(RegistryKeys.PARTICLE_TYPE);
    public static Registrar<StatusEffect> effects = regs.get().get(RegistryKeys.STATUS_EFFECT);
    public static Registrar<Item> items = regs.get().get(RegistryKeys.ITEM);

    public static RegistrySupplier<RecipeSerializer<?>> luaHandlingRecipe = RS.register(loc("lua_handling"), LuaHandlerRecipe.Serializer::new);

    public static RegistrySupplier<BasicParticleType<EmitterParticleOptions>> emittingParticle = particles.register(loc("emitter"), () -> new BasicParticleType<>(false, EmitterParticleOptions.codec));
    public static RegistrySupplier<BasicParticleType<MimicParticleOptions>> mimicParticle = particles.register(loc("mimic"), () -> new BasicParticleType<>(false, MimicParticleOptions.codec));
    public static RegistrySupplier<DefaultParticleType> controllerParticle = particles.register(loc("controller"), () -> new DefaultParticleType(false));
    public static RegistrySupplier<BasicParticleType<CubeParticleOptions>> cubeParticle = particles.register(loc("cube"), () -> new BasicParticleType<>(false, CubeParticleOptions.codec));
    public static RegistrySupplier<BasicParticleType<ChunkParticleOptions>> blockChunkParticle = particles.register(loc("block_chunk"), () -> new BasicParticleType<>(false, ChunkParticleOptions.codec));

    //public static RegistrySupplier<TestEffect> testEffect = effects.register(loc("test"), () -> new TestEffect());

    //public static RegistrySupplier<CuriousTrinketItem> testTrinket = items.register(loc("test_trinket"), () -> new CuriousTrinketItem(new Item.Properties()));

    public static void init() {
        var classLoading = NucleusRegistries.class;
    }
}
