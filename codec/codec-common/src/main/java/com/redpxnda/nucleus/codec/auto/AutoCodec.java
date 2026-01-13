package com.redpxnda.nucleus.codec.auto;

import com.google.gson.Gson;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.util.MiscUtil;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess.UNSAFE;

/**
 * An {@code AutoCodec} automatically derives a {@link Codec} for a class by inspecting its fields.
 * It is intended for simple, data-oriented objects where writing a manual {@link Codec} would be
 * unnecessary boilerplate.
 *
 * <h2>Typical Usage</h2>
 * <pre>{@code
 * Codec<MyClass> codec = AutoCodec.of(MyClass.class).codec();
 * }</pre>
 * see {@link AutoCodec#of(Class)} for more usage information.
 * {@link AutoCodec}s are comparable to {@link Gson}. They automatically generate a codec from some class,
 * scanning fields to add as parameters. Inevitably, as auto-generation tends to be, {@link AutoCodec}s can
 * be very unstable. They require classes to either: <br>
 * 1. {@code Override} {@link AutoCodec} reading by either being present within {@link CodecBehavior}
 * or by using the {@link CodecBehavior.Override} annotation. <br>
 * 2. Have an available nullary constructor (no arguments) for use in {@link AutoCodec}-ception. ({@link AutoCodec}s
 * generate more {@link AutoCodec}s, if needed.) This is the case because creating object instances without
 * constructors is incredibly dangerous. (See {@link sun.misc.Unsafe}) <br>
 * If neither of these conditions are met, errors will be thrown. So be wary. If you want to use a class in
 * your {@link AutoCodec} that doesn't meet these conditions, you can either add the class to {@link CodecBehavior}
 * or you can use {@link CodecBehavior.Override} on a field to specify a separate static codec field to use for
 * that field. <br>
 * Try to avoid using Wildcards and Type parameters with {@link AutoCodec}s, especially for {@link Collection}s and {@link Map}s.
 * Be extremely careful when using {@link Map}s of {@link Map}s. As long as your keys and values are directly {@link Map}s,
 * you should be fine. (For example: {@code Map<Map<String>, Map<Integer>>} will work. However, {@code Map<HashMap<String>, HashMap<Integer>>}
 * will not work since you are enforcing HashMap rather than allowing general Maps.) This is hard to fix, since performance becomes
 * an issue incredibly quickly when trying to translate maps into the form you desire.
 * Use {@link AutoCodec#of(Class)} in order to create a new {@link AutoCodec}. Then use {@link AutoCodec#codec()} to
 * obtain the Codec object for encoding and decoding. <br>
 * IMPORTANT NOTE: {@link AutoCodec}s should not be used in favor of the {@link RecordCodecBuilder} for complex structures.
 * {@link RecordCodecBuilder}s were made for a reason, and that reason still persists even with the existence of the
 * {@link AutoCodec}. {@link AutoCodec}s are only intended for simple structures that don't contain deeply nested formats. <br>
 * IMPORTANT NOTE: {@link AutoCodec}s aren't directly codecs, but rather are {@link MapCodec}s. To get the actual codec
 * object, use {@link AutoCodec#codec()}.
 *
 * @param <C> The type this {@link AutoCodec} represents
 * @see CodecBehavior.Override
 * @see Ignored
 * @see CodecBehavior.Optional
 * @see Settings
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AutoCodec<C> extends MapCodec<C> {
    private static final Logger LOGGER = Nucleus.getLogger();

    public static Map<String, Field> performFieldSearch(Class<?> cls) {
        Map<String, Field> result = new LinkedHashMap<>();

        if (cls.isRecord()) {
            for (RecordComponent rc : cls.getRecordComponents()) {
                try {
                    Field field = cls.getDeclaredField(rc.getName());
                    field.setAccessible(true);
                    if (Modifier.isStatic(field.getModifiers())) continue;

                    var annotations = rc.getAnnotations();

                    if (rc.isAnnotationPresent(Ignored.class) || field.isAnnotationPresent(Ignored.class))
                        continue;

                    Name nameAnnotation = rc.getAnnotation(Name.class);
                    if (nameAnnotation == null)
                        nameAnnotation = field.getAnnotation(Name.class);

                    String name = nameAnnotation == null ? rc.getName() : nameAnnotation.value();

                    result.put(name, field);
                } catch (NoSuchFieldException e) {
                    // shouldn't happen, but skip safely
                }
            }
        } else {
            for (Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || field.isAnnotationPresent(Ignored.class)) continue;
                Name nameAnnotation = field.getAnnotation(Name.class);
                String name = nameAnnotation == null ? field.getName() : nameAnnotation.value();
                result.put(name, field);
            }
        }

        return result;
    }

    /**
     * Class of the Codec
     */
    protected final Class<C> cls;
    protected final String errorMsg;
    protected Supplier<C> customConstructor = null;
    public final Map<String, AutoCodecField> fields = new LinkedHashMap<>();
    protected Map<String, Supplier<?>> overWriteDefaultSupplier = new HashMap<>();

    public AutoCodec(Class<C> cls, String errorMsg) {
        this(cls, errorMsg, performFieldSearch(cls));
    }

    public AutoCodec(Class<C> cls, String errorMsg, Map<String, Field> fieldMap) {
        this.cls = cls;
        this.errorMsg = errorMsg;

        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String name = entry.getKey();
            Field field = entry.getValue();

            MapCodec<?> codec = CodecBehavior.getMapCodecOrThrow(field, new ArrayList<>(), name);
            Type fieldType = field.getGenericType();

            fields.put(name, new AutoCodecField(name, codec, fieldType, field.getType(), field));
        }
    }

    /**
     * Create an AutoCodec for a specific class.
     * for default behaviour read {@link AutoCodec#setRecordDefaults(Map)}
     * for nullable behaviour on fields use {@link CodecBehavior.Optional}
     * for default nullable behaviour for a class use {@link Settings}
     * for custom data names use {@link Name}
     * to ignore fields use {@link Ignored}
     * to set custom codecs use {@link com.redpxnda.nucleus.codec.behavior.CodecBehavior.Override}
     * for additional past decode logic implement {@link AdditionalConstructing}
     * @param cls the class or record in question
     */
    public static <T> AutoCodec<T> of(Class<T> cls) {
        return new AutoCodec<>(cls, "Field not present for " + cls.getSimpleName() + ".");
    }

    /**
     * Only for Records.
     * Allows for setting custom default behaviour, so if the data is empty these will be used.
     * for classes simply set the field for the same behaviour.
     * @return itself
     */
    public AutoCodec<C> setRecordDefaults(Map<String, Supplier<?>> recordDefaultGetter) {
        this.overWriteDefaultSupplier = recordDefaultGetter;
        return this;
    }

    /**
     * Creates a custom AutoCodec with a custom error message on fail.
     *
     * @param cls the class in question
     * @param errorMsg custom error message
     */
    public static <T> AutoCodec<T> of(Class<T> cls, String errorMsg) {
        return new AutoCodec<>(cls, errorMsg);
    }

    protected C createCInstance(Class<C> cls) {
        return (C) createClassInstance(cls, customConstructor);
    }

    protected static Object createClassInstance(Class<?> cls, Supplier<?> supplier) {
        if (supplier != null) {
            try {
                return supplier.get();
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to create instance for class '{}' using registered supplier.",
                        cls.getSimpleName(),
                        e
                );
                throw new IllegalArgumentException(
                        "Supplier failed for class " + cls.descriptorString(), e
                );
            }
        }

        if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()) || cls.isAnnotation()) {
            LOGGER.error("Failed to create instance for class '{}' during AutoCodec decoding! Cannot instantiate interfaces/abstract classes.\n" +
                         "Please override the codec for this class.", cls.getSimpleName());
            throw new IllegalArgumentException("Cannot instantiate interfaces/abstract classes.");
        }

        if(cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers())
        ){
            throw new IllegalArgumentException("Cannot instantiate non static inner class!.");
        }

        try {
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException suppressed) {
            LOGGER.error("No available nullary constructor.{} should contain an null constructor!", cls.descriptorString(), suppressed);
        }

        try {
            LOGGER.error("Attempting misc.Unsafe");
            return UNSAFE.allocateInstance(cls);
        } catch (InstantiationException e) {
            LOGGER.error(
                    "Failed to create instance for class '{}' using Unsafe during AutoCodec decoding!",
                    cls.getSimpleName(),
                    e
            );
            throw new IllegalArgumentException(
                    "Unable to instantiate class " + cls.descriptorString() + " via Unsafe", e
            );
        }
    }

    /**
     * Allows to set a custom constructor.
     * Some classes like inner non-static or abstract/interfaces cant work otherwise.
     * Also usefull for custom behaviour
     * If you want to have custom behaviour after the fields are set, look at {@link AdditionalConstructing}
     * @param constructor
     * @return
     */
    @SuppressWarnings("unused")
    public AutoCodec<C> setClassConstructor(Supplier<C> constructor) {
        this.customConstructor = constructor;
        return this;
    }

    @java.lang.Override
    public <T> DataResult<C> decode(DynamicOps<T> ops, MapLike<T> map) {
        if (cls.isRecord()) {
            return decodeRecord(ops, map, cls);
        }
        Object instance = createCInstance(cls);
        for (String key : fields.keySet()) {
            AutoCodecField field = fields.get(key);
            try {
                var optional = field.codec.decode(
                        ops,
                        map
                );
                String message = "Failed to parse field '" + field.field.getName() + "' in AutoCodec decoding of '" + cls.getSimpleName() + "! -> ";
                if (optional.isError()) {
                    return DataResult.error(() -> message + optional.error().get().message());
                }
                Object value = optional.getOrThrow(s -> {
                    LOGGER.error(message + s);
                    return new DecoderException(message + s);
                });
                boolean setIfNull = defaultSetIfNullBehavior(field, value, instance);
                if (field.codec instanceof NullabilityHandler nh) {
                    setIfNull = nh.shouldSetToNull(ops, map);
                }

                setFieldVal(field, value, instance, setIfNull);
            } catch (DecoderException e) {
                return DataResult.error(e::getMessage);
            }
        }
        if (instance instanceof AdditionalConstructing ac) ac.additionalSetup();
        return DataResult.success((C) instance);
    }

    private static Object defaultValue(Class<?> t) {
        if (!t.isPrimitive()) return null;
        if (t == boolean.class) return false;
        if (t == byte.class) return (byte) 0;
        if (t == short.class) return (short) 0;
        if (t == int.class) return 0;
        if (t == long.class) return 0L;
        if (t == float.class) return 0f;
        if (t == double.class) return 0d;
        if (t == char.class) return '\0';
        return null;
    }

    private <T> DataResult<C> decodeRecord(DynamicOps<T> ops, MapLike<T> map, Class<C> cls) {
        try {
            var components = cls.getRecordComponents();
            Object[] args = new Object[components.length];
            Class<?>[] paramTypes = new Class<?>[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent rc = components[i];
                paramTypes[i] = rc.getType();

                AutoCodecField fieldInfo = fields.getOrDefault(rc.getName(), findByJavaName(rc.getName()));

                if (fieldInfo == null) {
                    throw new DecoderException("No codec found for record component '" +
                                               rc.getName() + "' in '" + cls.getSimpleName() + "'");
                }

                Object value = fieldInfo.codec.decode(ops, map)
                        .getOrThrow(s -> new DecoderException(
                                "Failed to parse record component '" + rc.getName() + "' in AutoCodec decoding of '" + cls.getSimpleName() + "'! -> " + s));

                boolean setIfNull = defaultSetIfNullBehavior(fieldInfo, value, null);
                if (fieldInfo.codec instanceof NullabilityHandler nh) {
                    setIfNull = nh.shouldSetToNull(ops, map);
                }
                boolean isOptional =
                        (rc.isAnnotationPresent(CodecBehavior.Optional.class)) ||
                        (fieldInfo.field != null && fieldInfo.field.isAnnotationPresent(CodecBehavior.Optional.class));

                Supplier<?> defaultSupplier = overWriteDefaultSupplier.get(fieldInfo.name);
                if (value == null) {
                    if (defaultSupplier != null) {
                        value = defaultSupplier.get();
                    } else if (!isOptional && !setIfNull) {
                        return DataResult.error(() -> "Record component '" + rc.getName() +
                                                      "' in '" + cls.getSimpleName() + "' is null but not marked optional and has no default");
                    } else {
                        value = defaultValue(rc.getType());
                    }
                }

                args[i] = (value == null) ? defaultValue(rc.getType()) : value;
            }

            var canonicalCtor = cls.getDeclaredConstructor(paramTypes);
            canonicalCtor.setAccessible(true);
            C instance = (C) canonicalCtor.newInstance(args);

            if (instance instanceof AdditionalConstructing ac) ac.additionalSetup();

            return DataResult.success(instance);

        } catch (DecoderException e) {
            return DataResult.error(e::getMessage);
        } catch (Exception e) {
            LOGGER.error("Failed to decode record of type '{}'", cls.getSimpleName(), e);
            return DataResult.error(() -> "Failed to decode record " + cls.getName() + ": " + e.getMessage());
        }
    }

    private AutoCodecField findByJavaName(String javaName) {
        for (AutoCodecField f : fields.values()) {
            if (f.field.getName().equals(javaName)) return f;
        }
        return null;
    }


    protected boolean defaultSetIfNullBehavior(AutoCodecField field, @Nullable Object value, Object classInstance) {
        return true;
    }

    protected void setFieldVal(AutoCodecField field, @Nullable Object value, Object classInstance, boolean setIfNull) {
        Field f = field.field;
        try {
            if (value != null || setIfNull)
                f.set(classInstance, value);
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to set field '{}' in '{}' during AutoCodec decoding!", f.getName(), classInstance.getClass());
            throw new RuntimeException(e);
        }
    }

    @java.lang.Override
    public <T> RecordBuilder<T> encode(C input, DynamicOps<T> ops, RecordBuilder<T> map) {
        for (Map.Entry<String, AutoCodecField> entry : fields.entrySet()) {
            String key = entry.getKey();
            AutoCodecField field = entry.getValue();
            Field f = field.field;
            try {
                Object val = f.get(input);

                map = ((MapCodec) field.codec).encode(val, ops, map);
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to get field '{}' in '{}' during AutoCodec encoding!", f.getName(), input.getClass());
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    @java.lang.Override
    public String toString() {
        return "AutoCodec{" +
               "cls=" + cls +
               ", fields=" + fields +
               '}';
    }

    @java.lang.Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return fields.keySet().stream().map(ops::createString);
    }

    /**
     * Implement this interface in your Target class
     * if you want to run code after the fields set by AutoCodec have been set.
     */
    public interface AdditionalConstructing {
        void additionalSetup();
    }

    /**
     * MapCodecs can implement this interface to prevent null values from *actually* setting the field to null... used for optionals
     */
    public interface NullabilityHandler {
        <T> boolean shouldSetToNull(DynamicOps<T> ops, MapLike<T> map);
    }

    public static Settings getSettings(Class<?> cls) {
        if (!cls.isAnnotationPresent(Settings.class)) return null;
        return cls.getAnnotation(Settings.class);
    }

    /**
     * Marks that this field should have a different name in serialized form
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Name {
        String value();
    }

    /**
     * Defines special settings for this class, used in AutoCodecs.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Settings {
        /**
         * If true, all fields will be optional, unless the field specifically changes it
         */
        CodecBehavior.Optional defaultOptionalBehavior() default @CodecBehavior.Optional(false);
    }

    /**
     * Marks that a field should not be serialized or deserialized, used for AutoCodecs.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Ignored {
    }

    public record AutoCodecField(String name, MapCodec<?> codec, Type type, Class<?> clazz, Field field) {
    }
}
