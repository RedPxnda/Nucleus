package com.redpxnda.nucleus.datapack.codec;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

/**
 * {@link AutoCodec}s are comparable to {@link Gson}. They automatically generate a codec from some class,
 * scanning fields to add as parameters. Inevitably, as auto-generation tends to be, {@link AutoCodec}s can
 * be very unstable. They require classes to either:
 * 1. {@code Override} {@link AutoCodec} reading by either being present within the {@link AutoCodec#inheritOverrides}
 * map or by using the {@link AutoCodec.Override} annotation.
 * 2. An available nullary constructor (no arguments) for use in {@link AutoCodec}-ception. ({@link AutoCodec}s
 * generate more {@link AutoCodec}s, if needed.) This is the case because creating object instances without
 * constructors is incredibly dangerous. (See {@link sun.misc.Unsafe})
 * If neither of these conditions are met, errors will be thrown. So be wary. If you want to use a class in
 * your {@link AutoCodec} that doesn't meet these conditions, you can either add the class to the {@link AutoCodec#inheritOverrides},
 * or you can use {@link AutoCodec.Override} on a field to specify a separate static codec field to use for
 * that field.
 * In the current state, {@link List}s work, but not perfectly. I recommend using arrays more. (Only fields
 * whose type is directly {@link List} will work. Setting an implementation of list, like {@link ArrayList},
 * will not work. This will be changed in the future.)
 * General {@link Collection}s and {@link Map}s are not yet implemented.
 * Use {@link AutoCodec#of(Class)} in order to create a new {@link AutoCodec}. Then use {@link AutoCodec#parse(DynamicOps, Object)}
 * and {@link AutoCodec#encodeStart(DynamicOps, Object)} to decode/encode.
 *
 * @see Override
 * @see Ignored
 * @param <C> The type this {@link AutoCodec} represents
 */
@ApiStatus.Experimental
public class AutoCodec<C> implements Codec<C> {
    private static final Map<Class<?>, Codec<?>> inheritOverrides = MiscUtil.intialize(new HashMap<>(), map -> {
        addInherit(map, Integer.class, Codec.INT);
        addInherit(map, int.class, Codec.INT);
        addInherit(map, Double.class, Codec.DOUBLE);
        addInherit(map, double.class, Codec.DOUBLE);
        addInherit(map, Float.class, Codec.FLOAT);
        addInherit(map, float.class, Codec.FLOAT);
        addInherit(map, Boolean.class, Codec.BOOL);
        addInherit(map, boolean.class, Codec.BOOL);
        addInherit(map, Byte.class, Codec.BYTE);
        addInherit(map, byte.class, Codec.BYTE);
        addInherit(map, Short.class, Codec.SHORT);
        addInherit(map, short.class, Codec.SHORT);
        addInherit(map, Long.class, Codec.LONG);
        addInherit(map, long.class, Codec.LONG);
        addInherit(map, String.class, Codec.STRING);
    });

    public static <T> void addInherit(Class<T> cls, Codec<T> codec) {
        inheritOverrides.put(cls, codec);
    }
    public static <T> void addInherit(Map<Class<?>, Codec<?>> map, Class<T> cls, Codec<T> codec) {
        map.put(cls, codec);
    }

    // adding registries into auto codec overrides
    public static void init() {
        for (Field field : BuiltInRegistries.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Registry.class.isAssignableFrom(field.getType())) continue;
            if (field.getGenericType() instanceof ParameterizedType pt && pt.getActualTypeArguments()[0] instanceof Class<?> cls) {
                try {
                    inheritOverrides.put(cls, ((Registry<?>) field.get(null)).byNameCodec());
                } catch (IllegalAccessException e) {
                    LOGGER.error("Failed to add '{}' from BuiltInRegistries as an inherit override.", field.getName());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private final Class<C> cls;
    private final Map<String, AutoCodecField> fields = new HashMap<>();

    public AutoCodec(Class<C> cls) {
        this.cls = cls;

        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || field.isAnnotationPresent(Ignored.class)) continue;
            Codec<?> codec;
            Class<?> fieldType = field.getType();
            FieldCallback callback = new FieldCallback(field);
            if (field.isAnnotationPresent(Override.class))
                codec = getFieldOverride(cls, field);
            else
                codec = getCodec(callback, fieldType, true);

            fields.put(field.getName(), new AutoCodecField(codec, callback.makePrimitive, callback.addToList));
        }
    }
    public static <T> AutoCodec<T> of(Class<T> cls) {
        return new AutoCodec<>(cls);
    }

    private static Codec<?> getCodec(FieldCallback callback, Class<?> cls, boolean allowFieldSearching) {
        if (cls.isArray()) {
            Class<?> component = cls.getComponentType();
            Codec<?> codec;
            if (component.isPrimitive()) callback.makePrimitive = true;
            if (component.isArray()) codec = getCodec(callback, cls.getComponentType(), allowFieldSearching);
            else codec = getCodec(callback, component, true);
            if (callback.makePrimitive) component = MiscUtil.arrayClassConversion(component);
            return MiscCodecs.array(
                    (Codec<Object>) codec,
                    (Class<Object>) component
            );
        }
        if (List.class.equals(cls)) { // todo alright ngl this whole fucking if statement section is complete dogshit code but i'll make it better later
            Type type = callback.field.getGenericType();
            if (type instanceof ParameterizedType generic) {
                String genericString = generic.toString();
                int dimensions = StringUtils.countMatches(genericString, "java.util.List");
                Class<?> clazz;
                String clazzName = genericString.substring("java.util.List<".repeat(dimensions).length(), genericString.length()-dimensions);
                try {
                    clazz = Class.forName(clazzName);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Failed to find class '{}' for AutoCodec!", clazzName);
                    throw new RuntimeException(e);
                }
                callback.addToList = true;
                Codec<?> codec = getCodec(callback, clazz, allowFieldSearching);
                for (int i = 0; i < dimensions; i++) {
                    codec = codec.listOf();
                }
                return codec;
            }
        }
        Codec<?> c = inheritOverrides.get(cls);
        if (c != null) return c;
        if (cls.isAnnotationPresent(Override.class)) {
            Override override = cls.getAnnotation(Override.class);
            try {
                Field field = cls.getField(override.value());
                field.setAccessible(true);
                int mods = field.getModifiers();
                if (!Modifier.isStatic(mods))
                    LOGGER.error("Field mentioned in AutoCodec.Override annotation for class '{}' must be static!", cls.getSimpleName());
                return (Codec<?>) field.get(null);
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                LOGGER.error("Field mentioned in @AutoCodec.Override annotation for class '{}' is either non-existent, private, protected, or not a valid Codec! -> {}", cls.getSimpleName(), e);
            }
        }
        if (allowFieldSearching)
            for (Field field : cls.getDeclaredFields()) {
                int mods = field.getModifiers();
                field.setAccessible(true);
                if (
                        !Modifier.isStatic(mods) ||
                        !field.getType().isAssignableFrom(Codec.class) ||
                        !(field.getGenericType() instanceof ParameterizedType type) ||
                        !type.getActualTypeArguments()[0].equals(cls)
                ) continue;
                try {
                    Codec<?> codec = (Codec<?>) field.get(null);
                    inheritOverrides.put(cls, codec);
                    return codec;
                } catch (IllegalAccessException | ClassCastException ignored) {}
            }
        return new AutoCodec<>(cls);
    }
    private static Codec<?> getFieldOverride(Class<?> cls, Field field) {
        Override override = field.getAnnotation(Override.class);
        try {
            Field codecField = cls.getField(override.value());
            return (Codec<?>) codecField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            LOGGER.error("Field mentioned in @AutoCodec.Override annotation for field '{}' in class '{}' is either non-existent, private, protected, or not a valid Codec! -> {}", field.getName(), cls.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }
    private static Object createClassInstance(Class<?> cls) {
        try {
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ignored) {}
        //sun.misc.Unsafe?
        LOGGER.error("Failed to create instance for class '{}' during AutoCodec decoding! No nullary constructor.", cls.getSimpleName());
        throw new IllegalArgumentException();
    }

    @java.lang.Override
    public <T> DataResult<Pair<C, T>> decode(DynamicOps<T> ops, T input) {
        Object instance = createClassInstance(cls);
        fields.forEach((key, field) -> {
            Object value = field.codec.parse(
                    ops,
                    ops.get(input, key).getOrThrow(false, s -> LOGGER.error("Failed to find field '{}' in AutoCodec decoding! -> {}", key, s))
            ).getOrThrow(false, s -> LOGGER.error("Failed to parse field '{}' in AutoCodec decoding! -> {}", key, s));
            if (field.isPrimitive) value = MiscUtil.arrayToPrimitive(value);
            try {
                Field f = instance.getClass().getDeclaredField(key);
                f.setAccessible(true);
                /*if (field.isCollection) {
                    Class<?> colCls = f.getType();
                    Collection<?> collection;
                    if (colCls.equals(Collection.class) || colCls.equals(List.class) || colCls.equals(ArrayList.class)) collection = new ArrayList<>();
                    else collection = (Collection<?>) createClassInstance(colCls);
                    collection.addAll((Collection) value);
                    value = collection;
                }*/
                f.set(instance, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LOGGER.error("Failed to set field '{}' in '{}' during AutoCodec decoding!", key, instance.getClass());
                throw new RuntimeException(e);
            }
        });
        return DataResult.success(Pair.of((C) instance, input));
    }

    @java.lang.Override
    public <T> DataResult<T> encode(C input, DynamicOps<T> ops, T prefix) {
        Map<T, T> map = new HashMap<>();
        fields.forEach((key, field) -> {
            try {
                Field f = input.getClass().getDeclaredField(key);
                f.setAccessible(true);
                Object val = f.get(input);
                if (field.isPrimitive) val = MiscUtil.arrayToNonPrimitive(val);
                T serialized = ((Codec<Object>) field.codec).encodeStart(ops, val)
                        .getOrThrow(false, s -> LOGGER.error("Failed to encode field '{}' in AutoCodec decoding! -> {}", key, s));
                map.put(ops.createString(key), serialized);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                LOGGER.error("Failed to get field '{}' in '{}' during AutoCodec decoding!", key, input.getClass());
                throw new RuntimeException(e);
            }
        });
        return DataResult.success(ops.createMap(map));
    }

    /**
     * Marks that this field should not be serialized or deserialized for AutoCodecs.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Ignored {}

    /**
     * Marks that this field or class should use a separate specific codec for AutoCodecs.
     * The {@code value} is the static field containing the separated codec
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Override {
        String value() default "CODEC";
    }

    private static final class FieldCallback {
        private boolean makePrimitive = false;
        private boolean addToList = false;
        private final Field field;

        private FieldCallback(Field field) {
            this.field = field;
        }
    }
    private record AutoCodecField(Codec<?> codec, boolean isPrimitive, boolean isCollection) {}
}
