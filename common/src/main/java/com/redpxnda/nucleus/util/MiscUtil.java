package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.datapack.references.storage.ResourceLocationReference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.function.*;

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

    public static <T> T intialize(T obj, Consumer<T> setup) {
        setup.accept(obj);
        return obj;
    }

    public static TwoArgFunction createTwoArgConsumer(BiConsumer<LuaValue, LuaValue> setup) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                setup.accept(arg1, arg2);
                return LuaValue.NIL;
            }
        };
    }
    public static TwoArgFunction createTwoArgFunc(BiFunction<LuaValue, LuaValue, LuaValue> setup) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                return setup.apply(arg1, arg2);
            }
        };
    }

    public static double[] arrayLerp2(int time, int maxTime, double[][] items) {
        if (items.length < 1) return new double[] { 1, 1 };
        int prog = (time % maxTime*2);
        if (prog >= maxTime) prog = prog-maxTime;

        int index = (int) Math.floor((prog/(float)maxTime)*items.length);
        float progress = ((prog/(float)maxTime)*items.length)-index;

        boolean tooLarge = index+1 >= items.length;
        return new double[] {
                Mth.lerp(progress, items[index][0], items[tooLarge ? 0 : index+1][0]),
                Mth.lerp(progress, items[index][1], items[tooLarge ? 0 : index+1][1])
        };
    }
    public static double[] arrayLerp3(int time, int maxTime, double[][] items) {
        if (items.length < 1) return new double[] { 1, 1, 1 };
        int prog = (time % maxTime*2);
        if (prog >= maxTime) prog = prog-maxTime;

        int index = (int) Math.floor((prog/(float)maxTime)*items.length);
        float progress = ((prog/(float)maxTime)*items.length)-index;

        boolean tooLarge = index+1 >= items.length;
        return new double[] {
                Mth.lerp(progress, items[index][0], items[tooLarge ? 0 : index+1][0]),
                Mth.lerp(progress, items[index][1], items[tooLarge ? 0 : index+1][1]),
                Mth.lerp(progress, items[index][2], items[tooLarge ? 0 : index+1][2])
        };
    }
}
