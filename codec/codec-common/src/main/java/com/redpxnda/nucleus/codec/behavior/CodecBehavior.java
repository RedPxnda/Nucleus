package com.redpxnda.nucleus.codec.behavior;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.auto.ConfigAutoCodec;
import com.redpxnda.nucleus.codec.misc.*;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.util.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CodecBehavior {
    private static final Logger LOGGER = Nucleus.getLogger();
    protected static final BiBehaviorOutline<Getter<?>, AnnotationGetter<?>> getters = new BiBehaviorOutline<>(true, true, true, false);

    static {
        registerDynamic(new Getter<>() {
            @java.lang.Override
            public Codec<Object> get(@Nullable Field field, Class<Object> cls, Type raw, @Nullable Type[] params, boolean isRoot) {
                return null;
            }

            @java.lang.Override
            public MapCodec<Object> getSecondary(@Nullable Field field, Class<Object> cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
                if (field != null) {
                    if (!field.isAnnotationPresent(Optional.class)) {
                        AutoCodec.Settings annot = field.getDeclaringClass().getAnnotation(AutoCodec.Settings.class);
                        if (annot != null) {
                            Optional op = annot.defaultOptionalBehavior();
                            if (op.value())
                                return new OptionalMapCodec<>(key, getCodecOrThrow(field, cls, raw, params, isRoot), op);
                        } else if (field.getDeclaringClass().isAnnotationPresent(ConfigAutoCodec.ConfigClassMarker.class)) {
                            return new OptionalMapCodec<>(key, getCodecOrThrow(field, cls, raw, params, isRoot), Optional.DEFAULT);
                        }
                    }
                }
                return null;
            }
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (!getUnsafeOutline().statics.containsKey(cls)) {
                Override annot = cls.getAnnotation(Override.class);
                if (annot != null) {
                    if (annot.auto()) {
                        Codec codec = AutoCodec.of(cls).codec();
                        registerClass(cls, codec);
                        return codec;
                    }
                    try {
                        Field field = cls.getField(annot.value());
                        field.setAccessible(true);
                        int mods = field.getModifiers();
                        if (!Modifier.isStatic(mods))
                            LOGGER.error("Field mentioned in CodecBehavior Override annotation for class '{}' must be static!", cls.getSimpleName());

                        Object obj = field.get(null);
                        if (obj instanceof Codec codec) {
                            registerClass(cls, codec);
                            return codec;
                        }

                        Getter<?> getter = (Getter<?>) obj;
                        registerClass((Class) cls, getter);
                        return getter.get(f, (Class) cls, raw, params, true);
                    } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                        LOGGER.error("Field mentioned in AutoCodec.Override annotation for class '" + cls.getSimpleName() + "' is either non-existent, inaccessible, or not a valid Codec(or CodecGetter)!", e);
                    }
                }
            }
            return null;
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (!getUnsafeOutline().statics.containsKey(cls)) {
                ConfigAutoCodec.ConfigClassMarker annot = cls.getAnnotation(ConfigAutoCodec.ConfigClassMarker.class);
                if (annot != null) {
                    Codec codec = ConfigAutoCodec.of(cls).codec();
                    registerClass(cls, codec);
                    return codec;
                }
            }
            return null;
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (cls.isArray()) {
                Class<?> component = cls.getComponentType();
                Codec<?> codec;
                codec = getCodecOrThrow(component, false);
                if (component.isPrimitive())
                    return MiscCodecs.primitiveArray(
                            codec,
                            component
                    );
                else
                    return MiscCodecs.array(
                            codec,
                            (Class) component
                    );
            }
            return null;
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (cls.isEnum() && !getUnsafeOutline().statics.containsKey(cls))
                return MiscCodecs.ofEnum((Class) cls);
            return null;
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (Collection.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type type = MiscUtil.collectionElement(raw);
                return CollectionCodec.of((Codec) getCodecOrThrow(type, false), () -> MiscUtil.createCollection((Class) cls));
            }
            return null;
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (Map.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type[] types = MiscUtil.mapElements(raw);
                return CustomMapCodec.of(getCodecOrThrow(types[0], false), (Codec) getCodecOrThrow(types[1], false), () -> MiscUtil.createMap(raw, (Class) cls));
            }
            return null;
        });

        registerAnnotator(Override.class, (annot, field, cls, rawFieldType, typeParams, root) -> {
            try {
                Field codecField = field.getDeclaringClass().getField(annot.value());
                return (Codec<?>) codecField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                LOGGER.error("Field mentioned in CodecBehavior Override annotation for field '" + field.getName() + "' in class '" + field.getDeclaringClass().getSimpleName() + "' is either non-existent, inaccessible, or not a valid Codec!", e);
                return null;
            }
        });
        registerAnnotator(Optional.class, new AnnotationGetter<>() {
            @java.lang.Override
            public Codec<?> get(Optional annotation, Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot) {
                return null;
            }

            @java.lang.Override
            public MapCodec<?> getSecondary(Optional annotation, Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
                if (!annotation.value())
                    AnnotationGetter.super.getSecondary(annotation, field, cls, raw, params, isRoot, key);
                return new OptionalMapCodec<>(key, getCodecOrThrow(field, cls, raw, params, isRoot), annotation);
            }
        });
        registerAnnotator(IntegerRange.class, (annot, field, cls, rawFieldType, typeParams, root) -> {
            if (cls.equals(int.class) || cls.equals(Integer.class)) {
                if (annot.failHard()) return Codec.intRange(annot.min(), annot.max());
                else return Codec.INT.xmap(i -> MathUtil.clamp(i, annot.min(), annot.max()), i -> MathUtil.clamp(i, annot.min(), annot.max()));
            }
            return null;
        });
        registerAnnotator(DoubleRange.class, (annot, field, cls, rawFieldType, typeParams, root) -> {
            if (cls.equals(double.class) || cls.equals(Double.class)) {
                if (annot.failHard()) return Codec.doubleRange(annot.min(), annot.max());
                else return Codec.DOUBLE.xmap(i -> MathUtil.clamp(i, annot.min(), annot.max()), i -> MathUtil.clamp(i, annot.min(), annot.max()));
            }
            return null;
        });
        registerAnnotator(FloatRange.class, (annot, field, cls, rawFieldType, typeParams, root) -> {
            if (cls.equals(float.class) || cls.equals(Float.class)) {
                if (annot.failHard()) return Codec.floatRange(annot.min(), annot.max());
                else return Codec.FLOAT.xmap(i -> MathUtil.clamp(i, annot.min(), annot.max()), i -> MathUtil.clamp(i, annot.min(), annot.max()));
            }
            return null;
        });

        registerClass(Integer.class, Codec.INT);
        registerClass(int.class, Codec.INT);
        registerClass(Double.class, Codec.DOUBLE);
        registerClass(double.class, Codec.DOUBLE);
        registerClass(Float.class, Codec.FLOAT);
        registerClass(float.class, Codec.FLOAT);
        registerClass(Boolean.class, Codec.BOOL);
        registerClass(boolean.class, Codec.BOOL);
        registerClass(Byte.class, Codec.BYTE);
        registerClass(byte.class, Codec.BYTE);
        registerClass(Short.class, Codec.SHORT);
        registerClass(short.class, Codec.SHORT);
        registerClass(Long.class, Codec.LONG);
        registerClass(long.class, Codec.LONG);
        registerClass(String.class, Codec.STRING);

        registerClass(Identifier.class, Identifier.CODEC);
        registerClass(DoubleSupplier.Instance.class, DoubleSupplier.CODEC);
        registerClass(Text.class, Codecs.TEXT);
        registerClass(Color.class, MiscCodecs.COLOR);
        registerClass(InterpolateMode.class, InterpolateMode.codec);
        registerClass(Vector3f.class, MiscCodecs.VECTOR_3F);
        registerClass(Pair.class, (field, cls, raw, params, root) -> { // todo replacement for mojank's pair codec?
            if (params == null) return null;
            return (Codec) Codec.pair(getCodecOrThrow(params[0], false), getCodecOrThrow(params[1], false));
        });
        registerClass(Either.class, (field, cls, raw, params, root) -> {
            if (params == null) return null;
            return (Codec) Codec.either(getCodecOrThrow(params[0], false), getCodecOrThrow(params[1], false));
        });
        registerClass(TagKey.class, (field, cls, raw, params, isRoot) -> {
            if (params == null) return null;
            Type t = params[0];
            Class c;
            if (t instanceof Class<?> cs) c = cs;
            else if (t instanceof ParameterizedType pt && pt.getRawType() instanceof Class<?> cs) c = cs;
            else return null;
            Registry reg = MiscUtil.objectsToRegistries.get(c);
            if (reg == null) return null;
            return Identifier.CODEC.xmap(id -> TagKey.of(reg.getKey(), id), TagKey::id);
        });

        try {
            registerClass(ParticleEffect.class, ParticleTypes.TYPE_CODEC);

            for (Field field : Registries.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isPublic(field.getModifiers()) || !Registry.class.isAssignableFrom(field.getType())) continue;
                if (field.getGenericType() instanceof ParameterizedType type) {
                    TypeToken token = TypeToken.of(type);
                    ParameterizedType pt = (ParameterizedType) token.getSupertype(Registry.class).getType();
                    Type firstParam = pt.getActualTypeArguments()[0];

                    Class cls = null;
                    if (firstParam instanceof Class<?> c)
                        cls = c;
                    else if (firstParam instanceof ParameterizedType t && t.getRawType() instanceof Class<?> c)
                        cls = c;
                    if (cls != null) {
                        try {
                            Registry<?> reg = (Registry<?>) field.get(null);
                            registerClassIfAbsent(cls, Getter.fromSupplier(reg::getCodec));
                        } catch (IllegalAccessException e) {
                            LOGGER.error("Failed to add '" + field.getName() + "' from vanilla's Registries as a codec behavior", e);
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
            LOGGER.warn("Failed to setup codecs for vanilla registries. Not yet bootstrapped?");
        }
    }

    public static <T> Codec<T> getCodecOrThrow(Type type, boolean isRoot) {
        Codec<T> result = getCodec(type, isRoot);
        if (result == null) {
            LOGGER.error("Failed to find Codec for type: {}", type);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + type);
        }
        return result;
    }

    public static <T> Codec<T> getCodecOrThrow(Field field, boolean isRoot) {
        Codec<T> result = getCodec(field, isRoot);
        if (result == null) {
            LOGGER.error("Failed to find Codec for field: {}", field);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + field);
        }
        return result;
    }

    public static <T> Codec<T> getCodecOrThrow(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        Codec<T> result = getCodec(field, cls, raw, params, isRoot);
        if (result == null) {
            LOGGER.error("Failed to find Codec for: FIELD: {} ... CLASS: {} ... RAW TYPE: {} ... PARAMS: {}", field, cls, raw, params == null ? null : Arrays.toString(params));
            throw new IllegalArgumentException("No CodecBehavior defined for inputted values. See logger error above.");
        }
        return result;
    }

    public static <T> @Nullable Codec<T> getCodec(Type type, boolean isRoot) {
        return (Codec<T>) getters.get(type, isRoot);
    }

    public static <T> @Nullable Codec<T> getCodec(Field field, boolean isRoot) {
        return (Codec<T>) getters.get(field, isRoot);
    }

    /**
     * @param field the field to be used as reference... used for things like annotations
     * @param cls the class of the field, or the class of the codec if no field
     * @param raw the raw "generic type" of the field, or the class if no field
     * @param params the type params of the field, or null if no field
     * @param isRoot false if recursive call, true otherwise
     * @return a codec for this field/class
     */
    public static <T> @Nullable Codec<T> getCodec(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        return (Codec<T>) getters.get(field, cls, raw, params, isRoot);
    }

    public static <T> MapCodec<T> getMapCodecOrThrow(Type type, boolean isRoot, String key) {
        MapCodec<T> result = getMapCodec(type, isRoot, key);
        if (result == null) {
            LOGGER.error("Failed to find MapCodec for type: {}", type);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + type);
        }
        return result;
    }

    public static <T> MapCodec<T> getMapCodecOrThrow(Field field, boolean isRoot, String key) {
        MapCodec<T> result = getMapCodec(field, isRoot, key);
        if (result == null) {
            LOGGER.error("Failed to find MapCodec for field: {}", field);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + field);
        }
        return result;
    }

    public static <T> MapCodec<T> getMapCodecOrThrow(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
        MapCodec<T> result = getMapCodec(field, cls, raw, params, isRoot, key);
        if (result == null) {
            LOGGER.error("Failed to find MapCodec for: FIELD: {} ... CLASS: {} ... RAW TYPE: {} ... PARAMS: {}", field, cls, raw, params == null ? null : Arrays.toString(params));
            throw new IllegalArgumentException("No CodecBehavior defined for inputted values. See logger error above.");
        }
        return result;
    }

    public static <T> @Nullable MapCodec<T> getMapCodec(Type type, boolean isRoot, String key) {
        return (MapCodec<T>) getters.getSecondary(type, isRoot, key);
    }

    public static <T> @Nullable MapCodec<T> getMapCodec(Field field, boolean isRoot, String key) {
        return (MapCodec<T>) getters.getSecondary(field, isRoot, key);
    }

    /**
     * @param field the field to be used as reference... used for things like annotations
     * @param cls the class of the field, or the class of the codec if no field
     * @param raw the raw "generic type" of the field, or the class if no field
     * @param params the type params of the field, or null if no field
     * @param isRoot false if recursive call, true otherwise
     * @return a codec for this field/class
     */
    public static <T> @Nullable MapCodec<T> getMapCodec(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
        return (MapCodec<T>) getters.getSecondary(field, cls, raw, params, isRoot, key);
    }

    public static BehaviorOutline<Getter<?>, AnnotationGetter<?>> getUnsafeOutline() {
        return getters;
    }

    public static <T> void registerClass(Class<T> cls, Getter<T> getter) {
        getters.statics.put(cls, getter);
    }
    public static <T> void registerClassIfAbsent(Class<T> cls, Getter<T> getter) {
        getters.statics.putIfAbsent(cls, getter);
    }
    public static <T> void registerClass(Class<T> cls, Supplier<Codec<T>> getter) {
        getters.statics.put(cls, Getter.fromSupplier(getter));
    }
    public static <T> void registerClass(Class<T> cls, Codec<T> getter) {
        getters.statics.put(cls, Getter.fromCodec(getter));
    }

    public static <A extends Annotation> void registerAnnotator(Class<A> cls, AnnotationGetter<A> getter) {
        getters.annotators.put(cls, getter);
    }

    public static void registerDynamic(Getter<?> getter) {
        getters.dynamics.add(getter);
    }

    public interface AnnotationGetter<A extends Annotation> extends AnnotationBehaviorGetter.Bi<A, Codec<?>, MapCodec<?>> {
        @java.lang.Override
        default MapCodec<?> getSecondary(A annotation, Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
            return get(annotation, field, cls, raw, params, isRoot).fieldOf(key);
        }
    }

    public interface Getter<T> extends TypeBehaviorGetter.Bi<MapCodec<T>, Codec<T>, T> {
        static <T> Getter<T> fromSupplier(Supplier<Codec<T>> supplier) {
            return (field, cls, raw, params, root) -> supplier.get();
        }

        static <T> Getter<T> fromCodec(Codec<T> codec) {
            return (field, cls, raw, params, root) -> codec;
        }

        @java.lang.Override
        default MapCodec<T> getSecondary(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
            Codec<T> codec = get(field, cls, raw, params, isRoot);
            return codec == null ? null : codec.fieldOf(key);
        }
    }

    /**
     * Marks that this field is optional, mainly useful for AutoCodecs. (can also make field mandatory)
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Optional {
        Optional DEFAULT = new Optional() {
            @java.lang.Override
            public boolean value() {
                return true;
            }

            @java.lang.Override
            public boolean encodeToNull() {
                return true;
            }

            @java.lang.Override
            public Class<? extends Annotation> annotationType() {
                return Optional.class;
            }
        };

        /**
         * @return whether this field is optional
         */
        boolean value() default true;

        /**
         * @return true if you want to encode this field to null when the field's value is null, false if you want to leave it out of the serialized object
         */
        boolean encodeToNull() default true;
    }

    /**
     * Marks that this field or class should use a separate specific codec.
     * The {@code value} is the static field containing the separated codec.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Override {
        String value() default "CODEC";
        boolean auto() default false;
    }
}
