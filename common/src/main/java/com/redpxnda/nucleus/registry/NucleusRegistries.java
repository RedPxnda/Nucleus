package com.redpxnda.nucleus.registry;

import com.google.common.base.Suppliers;
import com.redpxnda.nucleus.datapack.recipe.LuaHandlerRecipe;
import com.redpxnda.nucleus.registry.effect.TestEffect;
import com.redpxnda.nucleus.registry.particles.*;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;
import static com.redpxnda.nucleus.Nucleus.loc;

public class NucleusRegistries {
    public static final Supplier<RegistrarManager> regs = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static Registrar<RecipeSerializer<?>> RS = regs.get().get(Registries.RECIPE_SERIALIZER);
    public static Registrar<ParticleType<?>> particles = regs.get().get(Registries.PARTICLE_TYPE);
    public static Registrar<MobEffect> effects = regs.get().get(Registries.MOB_EFFECT);

    public static RegistrySupplier<RecipeSerializer<?>> luaHandlingRecipe = RS.register(loc("lua_handling"), LuaHandlerRecipe.Serializer::new);

    public static RegistrySupplier<BasicParticleType<EmitterParticleOptions>> emittingParticle = particles.register(loc("emitter"), () -> new BasicParticleType<>(false, EmitterParticleOptions.codec));
    public static RegistrySupplier<BasicParticleType<MimicParticleOptions>> mimicParticle = particles.register(loc("mimic"), () -> new BasicParticleType<>(false, MimicParticleOptions.codec));
    public static RegistrySupplier<SimpleParticleType> controllerParticle = particles.register(loc("controller"), () -> new SimpleParticleType(false));
    public static RegistrySupplier<BasicParticleType<CubeParticleOptions>> cubeParticle = particles.register(loc("cube"), () -> new BasicParticleType<>(false, CubeParticleOptions.codec));
    public static RegistrySupplier<BasicParticleType<ChunkParticleOptions>> blockChunkParticle = particles.register(loc("block_chunk"), () -> new BasicParticleType<>(false, ChunkParticleOptions.codec));

    //public static RegistrySupplier<TestEffect> testEffect = effects.register(loc("test"), () -> new TestEffect());

    public static void init() {
        var classLoading = NucleusRegistries.class;
    }
}
