package com.redpxnda.nucleus.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.internal.LinkedTreeMap;
import com.redpxnda.nucleus.mixin.ItemStackAccessor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
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

    public static <I, R> Predicate<R> mapPredicate(Predicate<I> original, Function<R, I> mapper) {
        return r -> original.test(mapper.apply(r));
    }

    public static <T> T initialize(T obj, Consumer<T> setup) {
        setup.accept(obj);
        return obj;
    }

    public static <T> Supplier<T> createSupplier(Supplier<T> supplier) {
        return supplier;
    }

    public static <T> T evaluateSupplier(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> void moveListElementDown(List<T> list, T element) {
        int i = list.indexOf(element);
        if (i != -1 && i != list.size()-1)
            Collections.rotate(list.subList(i, i+2), 1);
    }

    public static <T> void moveListElementUp(List<T> list, T element) {
        int i = list.indexOf(element);
        if (i != -1 && i != 0)
            Collections.rotate(list.subList(i-1, i+1), -1);
    }

    public static <K, V> void moveMapKeyDown(Map<K, V> map, K key) {
        List<K> keys = map.keySet().stream().collect(Collectors.toList());
        moveListElementDown(keys, key);
        for (K k : keys) {
            V val = map.remove(k);
            map.put(k, val);
        }
    }

    public static <K, V> void moveMapKeyUp(Map<K, V> map, K key) {
        List<K> keys = map.keySet().stream().collect(Collectors.toList());
        moveListElementUp(keys, key);
        for (K k : keys) {
            V val = map.remove(k);
            map.put(k, val);
        }
    }

    public static boolean isItemEmptyIgnoringCount(ItemStack stack) {
        return stack == ItemStack.EMPTY || ((ItemStackAccessor) (Object) stack).nucleus$getItemDirect() == Items.AIR;
    }
    public static boolean isItemOfIgnoringCount(ItemStack stack, Item itemToCheck) {
        return ((ItemStackAccessor) (Object) stack).nucleus$getItemDirect() == itemToCheck;
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

    public static <T> Collection<T> createCollection(Class<? extends Collection<T>> raw) {
        //todo make this extendable
        if (SortedSet.class.isAssignableFrom(raw)) {
            return new TreeSet<>();
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
