package com.redpxnda.nucleus.codec.auto;

import com.google.gson.Gson;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
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
 * @see CodecBehavior.Override
 * @see Ignored
 * @see CodecBehavior.Optional
 * @see Settings
 * @param <C> The type this {@link AutoCodec} represents
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AutoCodec<C> extends MapCodec<C> {
    private static final Logger LOGGER = Nucleus.getLogger();

    public static Map<String, Field> performFieldSearch(Class<?> cls) {
        Map<String, Field> result = new LinkedHashMap<>();

        for (Field field : cls.getFields()) {
            field.setAccessible(true);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || field.isAnnotationPresent(Ignored.class)) continue;
            Name nameAnnotation = field.getAnnotation(Name.class);
            String name = nameAnnotation == null ? field.getName() : nameAnnotation.value();
            result.put(name, field);
        }

        return result;
    }

    protected final Class<C> cls;
    protected final String errorMsg;
    public final Map<String, AutoCodecField> fields = new LinkedHashMap<>();

    public AutoCodec(Class<C> cls, String errorMsg) {
        this(cls, errorMsg, performFieldSearch(cls));
    }

    public AutoCodec(Class<C> cls, String errorMsg, Map<String, Field> fieldMap) {
        this.cls = cls;
        this.errorMsg = errorMsg;

        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String name = entry.getKey();
            Field field = entry.getValue();

            MapCodec<?> codec = CodecBehavior.getMapCodecOrThrow(field, true, name);
            Type fieldType = field.getGenericType();

            fields.put(name, new AutoCodecField(name, codec, fieldType, field.getType(), field));
        }
    }
    public static <T> AutoCodec<T> of(Class<T> cls) {
        return new AutoCodec<>(cls, "Field not present for " + cls.getSimpleName() + ".");
    }
    public static <T> AutoCodec<T> of(Class<T> cls, String errorMsg) {
        return new AutoCodec<>(cls, errorMsg);
    }

    protected C createCInstance(Class<C> cls) {
        return (C) createClassInstance(cls);
    }
    protected static Object createClassInstance(Class<?> cls) {
        if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()) || cls.isAnnotation()) {
            LOGGER.error("Failed to create instance for class '{}' during AutoCodec decoding! Cannot instantiate interfaces/abstract classes.\n" +
                    "Please override the codec for this class.", cls.getSimpleName());
            throw new IllegalArgumentException("Cannot instantiate interfaces/abstract classes.");
        }

        try {
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ignored) {}
        //sun.misc.Unsafe? for now, no.
        LOGGER.error("Failed to create instance for class '{}' during AutoCodec decoding! No available nullary constructor.", cls.getSimpleName());
        throw new IllegalArgumentException("No available nullary constructor!");
    }

    @java.lang.Override
    public <T> DataResult<C> decode(DynamicOps<T> ops, MapLike<T> map) {
        Object instance = createCInstance(cls);
        fields.forEach((key, field) -> {
            Object value = field.codec.decode(
                    ops,
                    map
            ).getOrThrow(false, s -> LOGGER.error("Failed to parse field '{}' in AutoCodec decoding of '{}'! -> {}", field.field.getName(), cls.getSimpleName(), s));

            boolean setIfNull = defaultSetIfNullBehavior(field, value, instance);
            if (field.codec instanceof NullabilityHandler nh) setIfNull = nh.shouldSetToNull(ops, map);

            setFieldVal(field, value, instance, setIfNull);
        });
        if (instance instanceof AdditionalConstructing ac) ac.additionalSetup();
        return DataResult.success((C) instance);
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
     * Implement this interface if you want to run code after the fields set by AutoCodec have been set.
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
    public @interface Ignored {}

    public record AutoCodecField(String name, MapCodec<?> codec, Type type, Class<?> clazz, Field field) {}

    /*private static class TestVessel {
        @IntegerRange(min = 0, max = 10)
        public int i = 5;

        public String str = "gah";

        public int[] array = {1, 2, 3};
    }

    public static void main(String[] args) {
        String json = """
                {
                    "i": 15,
                    "str": "hi, actually",
                    "array": [4, 5, 6]
                }
                """;
        Gson g = new Gson();
        JsonElement el = g.fromJson(json, JsonElement.class);

        Codec<TestVessel> codec = AutoCodec.of(TestVessel.class).codec();
        java.util.Optional<TestVessel> v = codec.parse(JsonOps.INSTANCE, el).result();
        System.out.println(v);
        v.ifPresent(testVessel -> {
            System.out.println(testVessel.i);
            System.out.println(testVessel.str);
            System.out.println(Arrays.toString(testVessel.array));
        });

        TestVessel vessel = new TestVessel();
        vessel.i=-1;
        vessel.array = new int[] {95, 96, 96};
        System.out.println(codec.encodeStart(JsonOps.INSTANCE, vessel));
    }*/
}
