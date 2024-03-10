package com.redpxnda.nucleus.codec.behavior;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.auto.ConfigAutoCodec;
import com.redpxnda.nucleus.codec.misc.*;
import com.redpxnda.nucleus.codec.tag.TagList;
import com.redpxnda.nucleus.codec.tag.TagListCodec;
import com.redpxnda.nucleus.codec.tag.TaggableEntry;
import com.redpxnda.nucleus.codec.tag.TaggableEntryCodec;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.util.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
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
import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CodecBehavior {
    private static final Logger LOGGER = Nucleus.getLogger();
    protected static final BiBehaviorOutline<Getter<?>> getters = new BiBehaviorOutline<>(true, true, true, false);

    static {
        registerDynamic(-8, new Getter<>() {
            @java.lang.Override
            public Codec<Object> get(@Nullable Field field, Class<Object> cls, Type raw, @Nullable Type[] params, List<String> passes) {
                return null;
            }

            @java.lang.Override
            public MapCodec<Object> getSecondary(@Nullable Field field, Class<Object> cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
                if (field != null) {
                    if (!field.isAnnotationPresent(Optional.class)) {
                        AutoCodec.Settings annot = field.getDeclaringClass().getAnnotation(AutoCodec.Settings.class);
                        if (annot != null) {
                            Optional op = annot.defaultOptionalBehavior();
                            if (op.value())
                                return new OptionalMapCodec<>(key, getCodecOrThrow(field, cls, raw, params, passes), op);
                        } else if (field.getDeclaringClass().isAnnotationPresent(ConfigAutoCodec.ConfigClassMarker.class)) {
                            return new OptionalMapCodec<>(key, getCodecOrThrow(field, cls, raw, params, passes), Optional.DEFAULT);
                        }
                    }
                }
                return null;
            }
        });
        registerDynamic(1, (f, cls, raw, params, passes) -> {
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
                        return getter.get(f, (Class) cls, raw, params, new ArrayList<>());
                    } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                        LOGGER.error("Field mentioned in CodecBehavior Override annotation for class '" + cls.getSimpleName() + "' is either non-existent, inaccessible, or not a valid Codec(or CodecGetter)!", e);
                    }
                }
            }
            return null;
        });
        registerDynamic(1, (f, cls, raw, params, passes) -> {
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
        registerDynamic((f, cls, raw, params, passes) -> {
            if (cls.isArray()) {
                Class<?> component = cls.getComponentType();
                Codec<?> codec;
                codec = getCodecOrThrow(component, new ArrayList<>());
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
        registerDynamic((f, cls, raw, params, passes) -> {
            if (cls.isEnum() && !getUnsafeOutline().statics.containsKey(cls))
                return MiscCodecs.ofEnum((Class) cls);
            return null;
        });
        registerDynamic((f, cls, raw, params, passes) -> {
            if (Collection.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type type = MiscUtil.collectionElement(raw);
                return CollectionCodec.of((Codec) getCodecOrThrow(type, new ArrayList<>()), () -> MiscUtil.createCollection((Class) cls));
            }
            return null;
        });
        registerDynamic((f, cls, raw, params, passes) -> {
            if (Map.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type[] types = MiscUtil.mapElements(raw);
                return CustomMapCodec.of(getCodecOrThrow(types[0], new ArrayList<>()), (Codec) getCodecOrThrow(types[1], new ArrayList<>()), () -> MiscUtil.createMap(raw, (Class) cls));
            }
            return null;
        });

        registerAnnotator(Override.class, -10, (annot, field, cls, rawFieldType, typeParams, passes) -> {
            try {
                Field codecField = field.getDeclaringClass().getField(annot.value());
                return (Codec<?>) codecField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                LOGGER.error("Field mentioned in CodecBehavior Override annotation for field '" + field.getName() + "' in class '" + field.getDeclaringClass().getSimpleName() + "' is either non-existent, inaccessible, or not a valid Codec!", e);
                return null;
            }
        });
        registerAnnotator(Optional.class, -9, new AnnotationGetter<>() {
            @java.lang.Override
            public Codec<?> get(Optional annotation, Field field, Class cls, Type raw, @Nullable Type[] params, List<String> passes) {
                return null;
            }

            @java.lang.Override
            public MapCodec<?> getSecondary(Optional annotation, Field field, Class cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
                if (/*passes.contains("optional") || */!annotation.value())
                    return null;
                //passes.add("optional");
                return new OptionalMapCodec<>(key, getCodecOrThrow(field, cls, raw, params, passes), annotation);
            }
        });
        registerAnnotator(IntegerRange.class, (annot, field, cls, rawFieldType, typeParams, passes) -> {
            if (cls.equals(int.class) || cls.equals(Integer.class)) {
                if (annot.failHard()) return Codec.intRange(annot.min(), annot.max());
                else return Codec.INT.xmap(i -> MathUtil.clamp(i, annot.min(), annot.max()), i -> MathUtil.clamp(i, annot.min(), annot.max()));
            }
            return null;
        });
        registerAnnotator(DoubleRange.class, (annot, field, cls, rawFieldType, typeParams, passes) -> {
            if (cls.equals(double.class) || cls.equals(Double.class)) {
                if (annot.failHard()) return Codec.doubleRange(annot.min(), annot.max());
                else return Codec.DOUBLE.xmap(i -> MathUtil.clamp(i, annot.min(), annot.max()), i -> MathUtil.clamp(i, annot.min(), annot.max()));
            }
            return null;
        });
        registerAnnotator(FloatRange.class, (annot, field, cls, rawFieldType, typeParams, passes) -> {
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
        registerClass(Pair.class, (field, cls, raw, params, passes) -> { // todo replacement for mojank's pair codec?
            if (params == null) return null;
            return (Codec) Codec.pair(getCodecOrThrow(params[0], new ArrayList<>()), getCodecOrThrow(params[1], new ArrayList<>()));
        });
        registerClass(Either.class, (field, cls, raw, params, passes) -> {
            if (params == null) return null;
            return (Codec) Codec.either(getCodecOrThrow(params[0], new ArrayList<>()), getCodecOrThrow(params[1], new ArrayList<>()));
        });
        registerClass(TagKey.class, (field, cls, raw, params, passes) -> {
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
        registerClass(TaggableEntry.class, (field, cls, raw, params, passes) -> {
            if (params == null) return null;
            Type t = params[0];
            Class c;
            if (t instanceof Class<?> cs) c = cs;
            else if (t instanceof ParameterizedType pt && pt.getRawType() instanceof Class<?> cs) c = cs;
            else return null;
            Registry reg = MiscUtil.objectsToRegistries.get(c);
            if (reg == null) return null;
            return new TaggableEntryCodec(tag -> new TaggableEntry<>(tag, reg, reg.getKey()), obj -> new TaggableEntry<>(obj, reg, reg.getKey()), reg, reg.getKey());
        });
        registerClass(TagList.class, (field, cls, raw, params, passes) -> {
            if (params == null) return null;
            Type t = params[0];
            Class c;
            if (t instanceof Class<?> cs) c = cs;
            else if (t instanceof ParameterizedType pt && pt.getRawType() instanceof Class<?> cs) c = cs;
            else return null;
            Registry reg = MiscUtil.objectsToRegistries.get(c);
            if (reg == null) return null;
            return new TagListCodec<>((objs, tags) -> new TagList<>(objs, tags, reg, reg.getKey()), reg, reg.getKey());
        });

        try {
            registerClass(ParticleEffect.class, ParticleTypes.TYPE_CODEC);
            MiscUtil.objectsToRegistries.forEach((k, v) -> registerClassIfAbsent((Class) k, Getter.fromSupplier(v::getCodec)));
        } catch (Throwable ignored) {
            LOGGER.warn("Failed to setup codecs for vanilla registries. Not yet bootstrapped?");
        }
    }

    public static <T> Codec<T> getCodecOrThrow(Type type, List<String> passes) {
        Codec<T> result = getCodec(type, passes);
        if (result == null) {
            LOGGER.error("Failed to find Codec for type: {}", type);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + type);
        }
        return result;
    }

    public static <T> Codec<T> getCodecOrThrow(Field field, List<String> passes) {
        Codec<T> result = getCodec(field, passes);
        if (result == null) {
            LOGGER.error("Failed to find Codec for field: {}", field);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + field);
        }
        return result;
    }

    public static <T> Codec<T> getCodecOrThrow(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, List<String> passes) {
        Codec<T> result = getCodec(field, cls, raw, params, passes);
        if (result == null) {
            LOGGER.error("Failed to find Codec for: FIELD: {} ... CLASS: {} ... RAW TYPE: {} ... PARAMS: {}", field, cls, raw, params == null ? null : Arrays.toString(params));
            throw new IllegalArgumentException("No CodecBehavior defined for inputted values. See logger error above.");
        }
        return result;
    }

    public static <T> @Nullable Codec<T> getCodec(Type type, List<String> passes) {
        return (Codec<T>) getters.get(type, passes);
    }

    public static <T> @Nullable Codec<T> getCodec(Field field, List<String> passes) {
        return (Codec<T>) getters.get(field, passes);
    }

    /**
     * @param field the field to be used as reference... used for things like annotations
     * @param cls the class of the field, or the class of the codec if no field
     * @param raw the raw "generic type" of the field, or the class if no field
     * @param params the type params of the field, or null if no field
     * @param passes false if recursive call, true otherwise
     * @return a codec for this field/class
     */
    public static <T> @Nullable Codec<T> getCodec(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, List<String> passes) {
        return (Codec<T>) getters.get(field, cls, raw, params, passes);
    }

    public static <T> MapCodec<T> getMapCodecOrThrow(Type type, List<String> passes, String key) {
        MapCodec<T> result = getMapCodec(type, passes, key);
        if (result == null) {
            LOGGER.error("Failed to find MapCodec for type: {}", type);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + type);
        }
        return result;
    }

    public static <T> MapCodec<T> getMapCodecOrThrow(Field field, List<String> passes, String key) {
        MapCodec<T> result = getMapCodec(field, passes, key);
        if (result == null) {
            LOGGER.error("Failed to find MapCodec for field: {}", field);
            throw new IllegalArgumentException("No CodecBehavior defined for type: " + field);
        }
        return result;
    }

    public static <T> MapCodec<T> getMapCodecOrThrow(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
        MapCodec<T> result = getMapCodec(field, cls, raw, params, passes, key);
        if (result == null) {
            LOGGER.error("Failed to find MapCodec for: FIELD: {} ... CLASS: {} ... RAW TYPE: {} ... PARAMS: {}", field, cls, raw, params == null ? null : Arrays.toString(params));
            throw new IllegalArgumentException("No CodecBehavior defined for inputted values. See logger error above.");
        }
        return result;
    }

    public static <T> @Nullable MapCodec<T> getMapCodec(Type type, List<String> passes, String key) {
        return (MapCodec<T>) getters.getSecondary(type, passes, key);
    }

    public static <T> @Nullable MapCodec<T> getMapCodec(Field field, List<String> passes, String key) {
        return (MapCodec<T>) getters.getSecondary(field, passes, key);
    }

    /**
     * @param field the field to be used as reference... used for things like annotations
     * @param cls the class of the field, or the class of the codec if no field
     * @param raw the raw "generic type" of the field, or the class if no field
     * @param params the type params of the field, or null if no field
     * @param passes false if recursive call, true otherwise
     * @return a codec for this field/class
     */
    public static <T> @Nullable MapCodec<T> getMapCodec(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
        return (MapCodec<T>) getters.getSecondary(field, cls, raw, params, passes, key);
    }

    public static BehaviorOutline<Getter<?>> getUnsafeOutline() {
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

    public static <A extends Annotation> void registerAnnotator(Class<A> cls, float prio, AnnotationGetter<A> getter) {
        synchronized (getters.dynamics) {
            getters.dynamics.put(new Getter() {
                @java.lang.Override
                public Codec get(@Nullable Field field, Class c, Type raw, @Nullable Type[] params, List passes) {
                    if (field != null) {
                        A annot = field.getAnnotation(cls);
                        if (annot != null) return getter.get(annot, field, c, raw, params, passes);
                    }
                    return null;
                }

                @java.lang.Override
                public MapCodec getSecondary(@Nullable Field field, Class c, Type raw, @Nullable Type[] params, List passes, String key) {
                    if (field != null) {
                        A annot = field.getAnnotation(cls);
                        if (annot != null) return getter.getSecondary(annot, field, c, raw, params, passes, key);
                    }
                    return null;
                }
            }, prio);
        }
    }

    public static <A extends Annotation> void registerAnnotator(Class<A> cls, AnnotationGetter<A> getter) {
        registerAnnotator(cls, 0f, getter);
    }

    public static void registerDynamic(float prio, Getter<?> getter) {
        synchronized (getters.dynamics) {
            getters.dynamics.put(getter, prio);
        }
    }

    public static void registerDynamic(Getter<?> getter) {
        registerDynamic(0f, getter);
    }

    public interface AnnotationGetter<A extends Annotation> extends AnnotationBehaviorGetter.Bi<A, Codec<?>, MapCodec<?>> {
        @java.lang.Override
        default MapCodec<?> getSecondary(A annotation, Field field, Class cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
            Codec<?> codec = get(annotation, field, cls, raw, params, passes);
            return codec == null ? null : codec.fieldOf(key);
        }
    }

    public interface Getter<T> extends TypeBehaviorGetter.Bi<MapCodec<T>, Codec<T>, T> {
        static <T> Getter<T> fromSupplier(Supplier<Codec<T>> supplier) {
            return (field, cls, raw, params, passes) -> supplier.get();
        }

        static <T> Getter<T> fromCodec(Codec<T> codec) {
            return (field, cls, raw, params, passes) -> codec;
        }

        @java.lang.Override
        default MapCodec<T> getSecondary(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
            Codec<T> codec = get(field, cls, raw, params, passes);
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
