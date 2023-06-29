package com.redpxnda.nucleus.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static <T> T initialize(T obj, Consumer<T> setup) {
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

    /**
     * Used to easily find out the type of Collection some Collection implementation is.
     * @return The type of Object this collection holds.
     */
    public static <T extends Collection<?>> Type collectionElement(Type type) {
        TypeToken<T> token = (TypeToken<T>) TypeToken.of(type); // a little unsafe, just use this class correctly though. (always pass in types which extend/implement Collection)

        ParameterizedType pt = (ParameterizedType) token.getSupertype(Collection.class).getType();
        return pt.getActualTypeArguments()[0];
    }

    /**
     * Used to easily find out the type of Map some Map implementation is.
     * @return The type of Objects this Map holds.
     */
    public static <T extends Map<?, ?>> Type[] mapElements(Type type) {
        TypeToken<T> token = (TypeToken<T>) TypeToken.of(type); // a little unsafe, just use this class correctly though. (always pass in types which extend/implement Map)

        ParameterizedType pt = (ParameterizedType) token.getSupertype(Map.class).getType();
        return pt.getActualTypeArguments();
    }

    public static <T> Collection<T> createCollection(final Type type, Class<? extends Collection<T>> raw) {
        //todo make this extendable
        if (SortedSet.class.isAssignableFrom(raw)) {
            return new TreeSet<T>();
        } else if (Set.class.isAssignableFrom(raw)) {
            return new LinkedHashSet<>();
        } else if (Queue.class.isAssignableFrom(raw)) {
            return new ArrayDeque<>();
        } else {
            return new ArrayList<>();
        }
    }

    public static <K, V> Map<K, V> createMap(final Type type, Class<? extends Map<K, V>> raw) {
        //todo make this extendable
        if (ConcurrentNavigableMap.class.isAssignableFrom(raw)) {
            return new ConcurrentSkipListMap<>();
        } else if (ConcurrentMap.class.isAssignableFrom(raw)) {
            return new ConcurrentHashMap<>();
        } else if (SortedMap.class.isAssignableFrom(raw)) {
            return new TreeMap<>();
        } else if (type instanceof ParameterizedType && !(String.class.isAssignableFrom(
                com.google.gson.reflect.TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
            return new LinkedHashMap<>();

        } else if (HashMap.class.isAssignableFrom(raw)) { // modification from original Gson code
            return new HashMap<>();

        } else {
            return new LinkedTreeMap<>();
        }
    }

    public static Stream<?> recursiveStreamMap(Stream<?> stream, int lvl, Function<?, ?> mapper) {
        if (lvl == 0) return stream.map((Function) mapper); // I physically cannot guarantee safety, just use this correctly.

        return stream.map(item -> {
            if (item instanceof Collection<?> collection)
                return recursiveStreamMap(collection.stream(), lvl-1, mapper).collect(Collectors.toList());
            return item;
        });
    }
}
