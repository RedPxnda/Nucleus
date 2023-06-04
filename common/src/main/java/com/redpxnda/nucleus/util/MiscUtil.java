package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.datapack.references.storage.ResourceLocationReference;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.function.Function;
import java.util.function.Predicate;

public class MiscUtil {
    public static <T> Predicate<T> predicateFromFunc(Class<T> clazz, LuaFunction function) {
        return t -> function.call(CoerceJavaToLua.coerce(t)).toboolean();
    }

    public static <I, R> Predicate<R> mapPredicate(Predicate<I> original, Function<R, I> mapper) {
        return r -> original.test(mapper.apply(r));
    }

    public static MobEffect getMobEffect(ResourceLocationReference ref) {
        return BuiltInRegistries.MOB_EFFECT.getOptional(ref.instance).orElse(MobEffects.LUCK);
    }
}
