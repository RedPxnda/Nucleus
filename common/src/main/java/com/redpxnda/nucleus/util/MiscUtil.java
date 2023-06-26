package com.redpxnda.nucleus.util;

import com.google.common.collect.ImmutableBiMap;
import com.redpxnda.nucleus.datapack.references.storage.ResourceLocationReference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.apache.commons.lang3.ArrayUtils;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.lang.reflect.Array;
import java.util.function.*;

public class MiscUtil {
    public static final ImmutableBiMap<Class<?>, Class<?>> primitiveToNon = new ImmutableBiMap.Builder<Class<?>, Class<?>>()
            .put(int.class, Integer.class)
            .put(double.class, Double.class)
            .put(float.class, Float.class)
            .put(long.class, Long.class)
            .put(boolean.class, Boolean.class)
            .put(char.class, Character.class)
            .put(byte.class, Byte.class)
            .put(void.class, Void.class)
            .put(short.class, Short.class)
            .build();

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

    //convert non-primitive array to primitive
    public static Object arrayToPrimitive(Object object) {
        if (!object.getClass().getComponentType().isArray())
            return ArrayUtils.toPrimitive(object);
        else {
            // array is actually Object[][], just no reason to cast
            Object[] array = (Object[]) object;
            Class<?> cls = arrayToPrimitive(array[0]).getClass();
            Object[] prims = (Object[]) Array.newInstance(cls, array.length);
            for (int i = 0; i < array.length; i++) {
                prims[i] = arrayToPrimitive(array[i]);
            }

            return prims;
        }
    }
    //convert primitive array to non-primitive
    public static Object arrayToNonPrimitive(Object primitiveArray) {
        return arrayToNonPrimitive(primitiveArray, primitiveToNon.get(primitiveArray.getClass().componentType()));
    }
    public static <T> T[] arrayToNonPrimitive(Object primitiveArray, Class<T> componentType) {
        int length = Array.getLength(primitiveArray);
        T[] nonPrimitiveArray = (T[]) Array.newInstance(componentType, length);

        for (int i = 0; i < length; i++) {
            T element = componentType.cast(Array.get(primitiveArray, i));
            nonPrimitiveArray[i] = element;
        }

        return nonPrimitiveArray;
    }
    //convert primitive array class to non-primitive array class
    public static Class<?> arrayClassConversion(Class<?> prim) {
        if (!prim.isArray()) return primitiveToNon.get(prim);
        int dimensions = getArrayClassDimensions(prim);
        Class<?> ct = prim.componentType();
        ct = getArrayClass(ct);
        if (ct.isPrimitive()) ct = primitiveToNon.get(ct);
        for (int i = 0; i < dimensions; i++) {
            ct = ct.arrayType();
        }
        return ct;
    }

    public static int getArrayClassDimensions(Class<?> arrayClass) {
        int dimensions = 0;
        while (arrayClass.isArray()) {
            dimensions++;
            arrayClass = arrayClass.getComponentType();
        }
        return dimensions;
    }
    public static Class<?> getArrayClass(Class<?> arrayClass) {
        while (arrayClass.isArray()) {
            arrayClass = arrayClass.getComponentType();
        }
        return arrayClass;
    }
}
