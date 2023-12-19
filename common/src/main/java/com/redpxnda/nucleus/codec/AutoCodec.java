package com.redpxnda.nucleus.codec;

import com.google.gson.Gson;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

/**
 * {@link AutoCodec}s are comparable to {@link Gson}. They automatically generate a codec from some class,
 * scanning fields to add as parameters. Inevitably, as auto-generation tends to be, {@link AutoCodec}s can
 * be very unstable. They require classes to either: <br>
 * 1. {@code Override} {@link AutoCodec} reading by either being present within the {@link AutoCodec#inheritOverrides}
 * map or by using the {@link AutoCodec.Override} annotation. <br>
 * 2. Have an available nullary constructor (no arguments) for use in {@link AutoCodec}-ception. ({@link AutoCodec}s
 * generate more {@link AutoCodec}s, if needed.) This is the case because creating object instances without
 * constructors is incredibly dangerous. (See {@link sun.misc.Unsafe}) <br>
 * If neither of these conditions are met, errors will be thrown. So be wary. If you want to use a class in
 * your {@link AutoCodec} that doesn't meet these conditions, you can either add the class to the {@link AutoCodec#inheritOverrides},
 * or you can use {@link AutoCodec.Override} on a field to specify a separate static codec field to use for
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
 * @see Override
 * @see Ignored
 * @see Mandatory
 * @see Settings
 * @param <C> The type this {@link AutoCodec} represents
 */
public class AutoCodec<C> extends MapCodec<C> {
    private static final Map<Class<?>, CodecGetter<?>> inheritOverrides = MiscUtil.initialize(new HashMap<>(), map -> {
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
        addInherit(map, Identifier.class, Identifier.CODEC);
        addInherit(map, DoubleSupplier.Instance.class, DoubleSupplier.CODEC);
        addInherit(map, ParticleEffect.class, ParticleTypes.TYPE_CODEC); // comment when testing, cuz minecraft boostraps stuff like this
        addInherit(map, EntityType.class, (Codec) Registries.ENTITY_TYPE.createEntryCodec()); // comment when testing, cuz minecraft boostraps stuff like this
        addInherit(map, Text.class, Codecs.TEXT);
        addInherit(map, Color.class, Color.CODEC);
        addInherit(map, Vector3f.class, MiscCodecs.VECTOR_3F);
    });

    public static <T> Codec<T> getOverride(Class<T> cls, Type[] typeParams) {
        CodecGetter<?> sup = inheritOverrides.get(cls);
        return sup == null ? null : (Codec<T>) sup.getCodec(typeParams);
    }
    public static <T> CodecGetter<T> getOverride(Class<T> cls) {
        return (CodecGetter<T>) inheritOverrides.get(cls);
    }
    public static <T> void addInherit(Class<T> cls, Codec<T> codec) {
        inheritOverrides.put(cls, CodecGetter.of(codec));
    }
    public static <T> void addInherit(Class<T> cls, CodecGetter<T> codec) {
        inheritOverrides.put(cls, codec);
    }
    public static <T> void addInheritIfAbsent(Class<T> cls, CodecGetter<T> codec) {
        inheritOverrides.putIfAbsent(cls, codec);
    }

    // a little unsafe, make sure you use this correctly.
    public static <T> void addInherit(Class<T> cls, Supplier<Codec<?>> codec) {
        inheritOverrides.put(cls, CodecGetter.ofSupplier(codec));
    }

    public static <T> void addInherit(Map<Class<?>, CodecGetter<?>> map, Class<T> cls, Codec<T> codec) {
        map.put(cls, CodecGetter.of(codec));
    }

    // adding registries into auto codec overrides
    public static void init() {
        for (Field field : Registries.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Registry.class.isAssignableFrom(field.getType())) continue;
            if (field.getGenericType() instanceof ParameterizedType pt && pt.getActualTypeArguments()[0] instanceof Class<?> cls) {
                try {
                    Registry<?> reg = (Registry<?>) field.get(null);
                    inheritOverrides.putIfAbsent(cls, CodecGetter.ofSupplier(reg::getCodec));
                } catch (IllegalAccessException e) {
                    LOGGER.error("Failed to add '{}' from BuiltInRegistries as an inherit override.", field.getName());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private final Class<C> cls;
    private final String errorMsg;
    private final Map<String, AutoCodecField> fields = new HashMap<>();

    public AutoCodec(Class<C> cls, String errorMsg) {
        this.cls = cls;
        this.errorMsg = errorMsg;

        for (Field field : cls.getFields()) {
            field.setAccessible(true);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || field.isAnnotationPresent(Ignored.class)) continue;
            Codec<?> codec;
            Type fieldType = field.getGenericType();
            FieldCallback callback = new FieldCallback(field);
            if (field.isAnnotationPresent(Override.class))
                codec = getFieldOverride(field);
            else
                codec = getCodec(callback, fieldType, true);

            Name name = field.getAnnotation(Name.class);
            fields.put(name == null ? field.getName() : name.value(), new AutoCodecField(field.getName(), codec, fieldType, field.getType(), callback.makePrimitive, callback.collectionLevel, callback.mapLevel, callback.makeDefaulted));
        }
    }
    public static <T> AutoCodec<T> of(Class<T> cls) {
        return new AutoCodec<>(cls, "Field not present for " + cls.getSimpleName() + ".");
    }
    public static <T> AutoCodec<T> of(Class<T> cls, String errorMsg) {
        return new AutoCodec<>(cls, errorMsg);
    }

    protected Codec<?> getCodec(FieldCallback callback, Type fieldType, boolean allowFieldSearching) {
        if (fieldType instanceof TypeVariable<?>) {
            LOGGER.error("Field's Type for AutoCodec cannot contain or be a TypeVariable!");
            throw new IllegalArgumentException("Field's Type for AutoCodec cannot contain or be a TypeVariable!");
        }
        if (fieldType instanceof WildcardType) {
            LOGGER.error("Field's Type for AutoCodec cannot contain or be a Wildcard!");
            throw new IllegalArgumentException("Field's Type for AutoCodec cannot contain or be a Wildcard!");
        }

        Class<?> cls = null;
        Type[] params = null;

        if (fieldType instanceof Class<?>) cls = (Class<?>) fieldType;
        if (fieldType instanceof ParameterizedType pt) {
            cls = (Class<?>) pt.getRawType();
            params = pt.getActualTypeArguments();
        }

        assert cls != null : "Class type for Nucleus AutoCodec found null.";
        boolean paramsSet = params != null;

        Settings settings = getSettings(this.cls);

        // arrays
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

        // collections
        if (Collection.class.isAssignableFrom(cls)) {
            if (paramsSet) {
                Type type = MiscUtil.collectionElement(fieldType);
                callback.collectionLevel++;
                return CollectionCodec.of(getCodec(callback, type, allowFieldSearching));
            }
        }

        // maps
        if (Map.class.isAssignableFrom(cls)) {
            if (paramsSet) {
                Type[] types = MiscUtil.mapElements(fieldType);
                callback.mapLevel++;
                return Codec.unboundedMap(getCodec(callback, types[0], allowFieldSearching), getCodec(callback, types[1], allowFieldSearching));
            }
        }

        // hardcoding Supplier<Double> because yes
        if (cls.equals(Supplier.class) && paramsSet && params[0].equals(Double.class))
            return DoubleSupplier.CODEC;

        // hardcoding optionals because yes
        if (
                callback.field.isAnnotationPresent(Optional.class) ||
                (settings != null && settings.optionalByDefault() && !callback.field.isAnnotationPresent(Mandatory.class))
        )
            callback.makeDefaulted = true;

        // hardcoding Enums because yes
        if (cls.isEnum())
            return MiscCodecs.ofEnum((Class<? extends Enum>) cls);

        // global override
        Codec<?> c = getOverride(cls, params);
        if (c != null) return c;

        // class override
        if (cls.isAnnotationPresent(Override.class)) {
            Override override = cls.getAnnotation(Override.class);
            if (override.auto()) {
                Codec<?> codec = createAutoCodecWith(cls, errorMsg).codec();
                inheritOverrides.putIfAbsent(cls, CodecGetter.of(codec));
                return codec;
            }
            try {
                Field field = cls.getField(override.value());
                field.setAccessible(true);
                int mods = field.getModifiers();
                if (!Modifier.isStatic(mods))
                    LOGGER.error("Field mentioned in AutoCodec.Override annotation for class '{}' must be static!", cls.getSimpleName());
                Object obj = field.get(null);
                if (obj instanceof Codec<?> codec) {
                    inheritOverrides.putIfAbsent(cls, CodecGetter.of(codec));
                    return codec;
                }

                CodecGetter<?> codec = ((CodecGetter<?>) obj);
                inheritOverrides.putIfAbsent(cls, codec);
                return codec.getCodec(params);
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                LOGGER.error("Field mentioned in AutoCodec.Override annotation for class '{}' is either non-existent, inaccessible, or not a valid Codec(or CodecGetter)! -> {}", cls.getSimpleName(), e);
            }
        }

        // class searching to find suitable codec (unsafe?)
        if (allowFieldSearching)
            for (Field field : cls.getDeclaredFields()) {
                int mods = field.getModifiers();
                field.setAccessible(true);
                if (
                        !Modifier.isStatic(mods) ||
                        !field.getType().equals(Codec.class) ||
                        !(field.getGenericType() instanceof ParameterizedType type) ||
                        (paramsSet && !type.getActualTypeArguments()[0].equals(fieldType)) ||
                        (!paramsSet && !type.getActualTypeArguments()[0].equals(cls))
                ) continue;
                try {
                    Codec<?> codec = (Codec<?>) field.get(null);
                    inheritOverrides.put(cls, CodecGetter.of(codec));
                    return codec;
                } catch (IllegalAccessException | ClassCastException ignored) {}
            }

        // autocodec inception
        Codec<?> codec = createAutoCodecWith(cls, errorMsg).codec();
        inheritOverrides.putIfAbsent(cls, CodecGetter.of(codec));
        return codec;
    }

    protected <T> AutoCodec<T> createAutoCodecWith(Class<T> cls, String errorMsg) {
        return new AutoCodec<>(cls, errorMsg);
    }

    protected static Codec<?> getFieldOverride(Field field) {
        Override override = field.getAnnotation(Override.class);
        try {
            Field codecField = field.getDeclaringClass().getField(override.value());
            return (Codec<?>) codecField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            LOGGER.error("Field mentioned in AutoCodec.Override annotation for field '{}' in class '{}' is either non-existent, inaccessible, or not a valid Codec! -> {}", field.getName(), field.getDeclaringClass().getSimpleName(), e);
            throw new RuntimeException(e);
        }
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
    protected static Collection<?> createCollection(Type type, Class<?> cls) {
        Collection<?> collection = MiscUtil.createCollection(type, (Class) cls);
        if (!cls.isAssignableFrom(collection.getClass())) {
            LOGGER.error("Failed to create Collection instance for class '{}' during AutoCodec deserialization! Unsupported collection type?", cls);
            throw new IllegalArgumentException("Unsupported collection type?");
        }

        return collection;
    }
    protected static Map<?, ?> createMap(Type type, Class<?> cls) {
        Map<?, ?> map = MiscUtil.createMap(type, (Class) cls);
        if (!cls.isAssignableFrom(map.getClass())) {
            LOGGER.error("Failed to create Map instance for class '{}' during AutoCodec deserialization! Unsupported map type?", cls);
            throw new IllegalArgumentException("Unsupported map type?");
        }

        return map;
    }

    protected boolean isFieldOptional(String key, AutoCodecField field, Object instance, boolean encoding) {
        return field.isDefaulted;
    }

    @java.lang.Override
    public <T> DataResult<C> decode(DynamicOps<T> ops, MapLike<T> map) {
        Object instance = createCInstance(cls);
        fields.forEach((key, field) -> {
            T entry = map.get(key);
            Object value;
            if (isFieldOptional(key, field, instance, false) && (entry == null || entry == ops.empty()))
                value = null;
            else if (entry == null) {
                LOGGER.error("Failed to find field '{}' in AutoCodec decoding! -> {}", key, errorMsg);
                throw new NoSuchElementException();
            } else {
                value = field.codec.parse(
                        ops,
                        entry
                ).getOrThrow(false, s -> LOGGER.error("Failed to parse field '{}' in AutoCodec decoding! -> {}", field.fieldName, s));
            }
            if (field.isPrimitiveArray) value = MiscUtil.arrayToPrimitive(value);
            if (field.collectionLevel > 0) {
                Collection<?> toSet = createCollection(field.type, field.clazz);

                Collection<?> toAdd = (Collection<?>) field.codec.parse(
                        ops,
                        entry
                ).getOrThrow(false, s -> LOGGER.error("Failed to parse field '{}' in AutoCodec decoding! -> {}", field.fieldName, s));

                Stream<?> stream = toAdd.stream();
                ParameterizedType pt = (ParameterizedType) field.type;
                for (int i = 0; i < field.collectionLevel - 1; i++) {
                    pt = (ParameterizedType) pt.getActualTypeArguments()[0];

                    final ParameterizedType finalPt = pt;
                    stream = MiscUtil.recursiveStreamMap(stream, i, l -> {
                        Collection<?> toReplace = createCollection(finalPt, (Class<?>) finalPt.getRawType());
                        toReplace.addAll((Collection) l);
                        return toReplace;
                    });
                }

                toSet.addAll((Collection) stream.toList()); // idk java is weird don't blame me

                value = toSet;
            }
            if (field.mapLevel > 0) {
                Map<?, ?> toSet = createMap(field.type, field.clazz);

                Map<?, ?> toAdd = (Map<?, ?>) field.codec.parse(
                        ops,
                        entry
                ).getOrThrow(false, s -> LOGGER.error("Failed to parse field '{}' in AutoCodec decoding! -> {}", field.fieldName, s));

                //todo maps of maps?

                toSet.putAll((Map) toAdd); // idk java is weird don't blame me

                value = toSet;
            }
            setFieldVal(field.fieldName, value, instance);
        });
        if (instance instanceof AdditionalConstructing ac) ac.additionalSetup();
        return DataResult.success((C) instance);
    }

    protected void setFieldVal(String key, @Nullable Object value, Object classInstance) {
        try {
            Field f = classInstance.getClass().getField(key);
            f.setAccessible(true);
            if (value != null)
                f.set(classInstance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Failed to set field '{}' in '{}' during AutoCodec decoding!", key, classInstance.getClass());
            throw new RuntimeException(e);
        }
    }

    @java.lang.Override
    public <T> RecordBuilder<T> encode(C input, DynamicOps<T> ops, RecordBuilder<T> map) {
        fields.forEach((key, field) -> {
            try {
                Field f = input.getClass().getField(field.fieldName);
                f.setAccessible(true);
                Object val = f.get(input);

                if (field.isPrimitiveArray) val = MiscUtil.arrayToNonPrimitive(val);

                // collections inherently supported because of my collection codec
                // maps inherently supported because of Mojang's unbounded map codec

                T serialized;
                if (isFieldOptional(key, field, input, true) && val == null)
                    serialized = ops.empty();
                else
                    serialized = ((Codec<Object>) field.codec).encodeStart(ops, val)
                            .getOrThrow(false, s -> LOGGER.error("Failed to encode field '{}' in AutoCodec encoding! -> {}", key, s));
                adjustSerializedObject(key, serialized, val, f, input);
                map.add(key, serialized);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                LOGGER.error("Failed to get field '{}' in '{}' during AutoCodec encoding!", key, input.getClass());
                throw new RuntimeException(e);
            }
        });
        return map;
    }

    protected <T> void adjustSerializedObject(String key, T serialized, Object original, Field field, C holder) {

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

    public interface CodecGetter<T> {
        static <T> CodecGetter<T> of(Codec<T> codec) {
            return p -> codec;
        }
        static CodecGetter<?> ofSupplier(Supplier<Codec<?>> codec) {
            return p -> (Codec<Object>) codec.get();
        }

        Codec<T> getCodec(@Nullable Type[] typeParams);
    }

    /**
     * Implement this interface if you want to run code after the fields set by AutoCodec have been set.
     */
    public interface AdditionalConstructing {
        void additionalSetup();
    }

    /**
     * Marks that this field should not be serialized or deserialized for AutoCodecs.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Ignored {}

    /**
     * Marks that this field is optional for AutoCodecs.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Optional {}

    /**
     * Marks that this field is mandatory for AutoCodecs.
     * Functionally, this field does nothing unless {@link Settings#optionalByDefault()} is true.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Mandatory {}

    /**
     * Marks that this field or class should use a separate specific codec for AutoCodecs.
     * The {@code value} is the static field containing the separated codec
     * {@code auto} determines whether the codec for this object should be another autocodec
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Override { // todo create 'global' overrides
        String value() default "CODEC";
        boolean auto() default false;
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
     * Defines special settings for this class for use in AutoCodecs.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Settings {
        /**
         * If true, all fields will be interpreted as {@link Optional} unless the field is marked with {@link Mandatory}
         */
        boolean optionalByDefault() default false;
    }
    public static Settings getSettings(Class<?> cls) {
        if (!cls.isAnnotationPresent(Settings.class)) return null;
        return cls.getAnnotation(Settings.class);
    }

    private static final class FieldCallback {
        private int collectionLevel = 0;
        private int mapLevel = 0;
        private boolean makePrimitive = false;
        private boolean makeDefaulted = false;
        private final Field field;

        private FieldCallback(Field field) {
            this.field = field;
        }
    }
    protected record AutoCodecField(String fieldName, Codec<?> codec, Type type, Class<?> clazz, boolean isPrimitiveArray, int collectionLevel, int mapLevel, boolean isDefaulted) {}

    /*private static class TestVessel {
        public int i; public double d; public String s; public ArrayList<List<Set<String>>> strings; public @Optional Map<String, Integer> map;

        @java.lang.Override
        public String toString() {
            return "TestVessel{" +
                    "i=" + i +
                    ", d=" + d +
                    ", s='" + s + '\'' +
                    ", strings=" + strings +
                    ", map=" + map +
                    '}';
        }
    }

    public static void main(String[] args) {
        String json = """
                {
                    "i": 1,
                    "d": 2.4,
                    "s": "coolness",
                    "strings": [ [["a and b"], ["yz"]], [["c and d"]] ],
                    "map": {
                        "key1": 3,
                        "key2": 2
                    }
                }
                """;
        Gson g = new Gson();
        JsonElement el = g.fromJson(json, JsonElement.class);

        Codec<TestVessel> codec = AutoCodec.of(TestVessel.class).codec();
        java.util.Optional<TestVessel> v = codec.parse(JsonOps.INSTANCE, el).result();
        System.out.println(v);
        v.ifPresent(testVessel -> System.out.println(testVessel.strings.get(1).get(0).getClass()));

        TestVessel vessel = new TestVessel();
        vessel.i=4;
        vessel.d=23.5;
        vessel.s="now this, is epic";
        vessel.strings=new ArrayList<>(List.of(List.of(Set.of("a", "b, and", "c"))));
//        vessel.map=new HashMap<>(){{
//            put("key", 5);
//            put("key2", 3);
//        }};

        System.out.println(codec.encodeStart(JsonOps.INSTANCE, vessel));
    }*/
}
