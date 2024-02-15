package com.redpxnda.nucleus.codec.behavior;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.util.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class InstantiationBehavior {
    private static final Logger LOGGER = Nucleus.getLogger();
    protected static final BehaviorOutline<Getter<?>, AnnotationGetter<?>> getters = new BehaviorOutline<>(false);

    static {
        registerDynamic((f, cls, raw, params, root) -> {
            if (Collection.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null)
                return MiscUtil.createCollection((Class) cls);
            return null;
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (Map.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null)
                return MiscUtil.createMap(raw, (Class) cls);
            return null;
        });
        registerDynamic((f, cls, raw, params, root) -> {
            if (!getUnsafeOutline().statics.containsKey(cls)) {
                try {
                    var constructor = cls.getConstructor();
                    constructor.setAccessible(true);
                    registerClassSupplier(cls, () -> {
                        try {
                            return constructor.newInstance();
                        } catch (InstantiationException | InvocationTargetException | IllegalAccessException ignored) {
                            return null;
                        }
                    });
                    return constructor.newInstance();
                } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException ignored) {}
            }
            return null;
        });

        registerAnnotator(IntegerRange.class, (annot, field, cls, rawFieldType, typeParams, root) -> {
            if (cls.equals(int.class) || cls.equals(Integer.class))
                return annot.min();
            return null;
        });
        registerAnnotator(DoubleRange.class, (annot, field, cls, rawFieldType, typeParams, root) -> {
            if (cls.equals(double.class) || cls.equals(Double.class))
                return annot.min();
            return null;
        });
        registerAnnotator(FloatRange.class, (annot, field, cls, rawFieldType, typeParams, root) -> {
            if (cls.equals(float.class) || cls.equals(Float.class))
                return annot.min();
            return null;
        });

        registerClass(Integer.class, 0);
        registerClass(int.class, 0);
        registerClass(Double.class, 0d);
        registerClass(double.class, 0d);
        registerClass(Float.class, 0f);
        registerClass(float.class, 0f);
        registerClass(Boolean.class, false);
        registerClass(boolean.class, false);
        registerClass(Byte.class, (byte) 0);
        registerClass(byte.class, (byte) 0);
        registerClass(Short.class, (short) 0);
        registerClass(short.class, (short) 0);
        registerClass(Long.class, 0L);
        registerClass(long.class, 0L);
        registerClass(String.class, "");

        registerClassSupplier(Text.class, Text::empty);
        registerClassSupplier(Color.class, Color.WHITE::copy);
        registerClass(InterpolateMode.class, InterpolateMode.LERP);
    }

    public static <T> T getInstanceOrThrow(Type type, boolean isRoot) {
        T result = getInstance(type, isRoot);
        if (result == null) {
            LOGGER.error("Failed to find instantiator for type: {}", type);
            throw new IllegalArgumentException("No InstantiationBehavior defined for type: " + type);
        }
        return result;
    }

    public static <T> T getInstanceOrThrow(Field field, boolean isRoot) {
        T result = getInstance(field, isRoot);
        if (result == null) {
            LOGGER.error("Failed to find instantiator for field: {}", field);
            throw new IllegalArgumentException("No InstantiationBehavior defined for type: " + field);
        }
        return result;
    }

    public static <T> T getInstanceOrThrow(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        T result = getInstance(field, cls, raw, params, isRoot);
        if (result == null) {
            LOGGER.error("Failed to find instantiator for: FIELD: {} ... CLASS: {} ... RAW TYPE: {} ... PARAMS: {}", field, cls, raw, params == null ? null : Arrays.toString(params));
            throw new IllegalArgumentException("No InstantiationBehavior defined for inputted values. See logger error above.");
        }
        return result;
    }

    public static <T> @Nullable T getInstance(Type type, boolean isRoot) {
        return (T) getters.get(type, isRoot);
    }

    public static <T> @Nullable T getInstance(Field field, boolean isRoot) {
        return (T) getters.get(field, isRoot);
    }

    /**
     * @param field the field to be used as reference... used for things like annotations
     * @param cls the class of the field, or the object class if no field
     * @param raw the raw "generic type" of the field, ^
     * @param params the type params of the field, or null if no field
     * @return a instance of an object for this field/class
     */
    public static <T> @Nullable T getInstance(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        return (T) getters.get(field, cls, raw, params, isRoot);
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
    public static <T> void registerClassSupplier(Class<T> cls, Supplier<T> getter) {
        getters.statics.put(cls, Getter.fromSupplier(getter));
    }
    public static <T> void registerClass(Class<T> cls, T getter) {
        getters.statics.put(cls, Getter.fromInstance(getter));
    }

    public static <A extends Annotation> void registerAnnotator(Class<A> cls, AnnotationGetter<A> getter) {
        getters.annotators.put(cls, getter);
    }

    public static void registerDynamic(Getter<?> getter) {
        getters.dynamics.add(getter);
    }

    public interface AnnotationGetter<A extends Annotation> extends AnnotationBehaviorGetter<A, Object> { }

    public interface Getter<T> extends TypeBehaviorGetter<T, T> {
        static <T> Getter<T> fromSupplier(Supplier<T> supplier) {
            return (field, cls, raw, params, root) -> supplier.get();
        }

        static <T> Getter<T> fromInstance(T instance) {
            return (field, cls, raw, params, root) -> instance;
        }
    }
}
