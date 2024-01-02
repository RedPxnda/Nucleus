package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.util.MiscUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

/**
 * Defines a way to create drawables based on the type parameters of a field.
 */
@Environment(EnvType.CLIENT)
public interface ConfigComponentType<T> {
    List<ConfigComponentType> dynamics = MiscUtil.initialize(new ArrayList<>(), l -> {
        l.add((field, cls, raw, params) -> {
            if (Collection.class.isAssignableFrom(cls) && params != null) {
                Type elementType = MiscUtil.collectionElement(raw);
                return () -> new CollectionComponent(() -> {
                    Collection<?> collection = MiscUtil.createCollection(cls);
                    if (!cls.isAssignableFrom(collection.getClass())) {
                        LOGGER.error("Failed to create Collection instance for class '{}' for a CollectionComponent! Unsupported collection type?", cls);
                        throw new IllegalArgumentException("Unsupported collection type?");
                    }
                    return collection;
                }, elementType, 0, 0);
            }
            return null;
        });

        l.add((field, cls, raw, params) -> {
            if (Map.class.isAssignableFrom(cls) && params != null) {
                Type[] types = MiscUtil.mapElements(raw);
                return () -> new MapComponent(() -> {
                    Map<?, ?> map = MiscUtil.createMap(raw, cls);
                    if (!cls.isAssignableFrom(map.getClass())) {
                        LOGGER.error("Failed to create Map instance for class '{}' for a MapComponent! Unsupported map type?", cls);
                        throw new IllegalArgumentException("Unsupported map type?");
                    }
                    return map;
                }, types[0], types[1], 0, 0);
            }
            return null;
        });

        l.add((field, cls, raw, params) -> {
            if (cls.isEnum())
                return () -> new DropdownComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, cls);
            return null;
        });
    });
    Map<Class<?>, ConfigComponentType<?>> registered = MiscUtil.initialize(new HashMap<>(), m -> {
        register(m, String.class, of(() -> new TextFieldComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20)));
        register(m, int.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Integer::parseInt, false)));
        register(m, Integer.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Integer::parseInt, false)));
        register(m, byte.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Byte::parseByte, false)));
        register(m, Byte.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Byte::parseByte, false)));
        register(m, short.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Short::parseShort, false)));
        register(m, Short.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Short::parseShort, false)));
        register(m, long.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Long::parseLong, false)));
        register(m, Long.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Long::parseLong, false)));
        register(m, double.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Double::parseDouble, true)));
        register(m, Double.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Double::parseDouble, true)));
        register(m, float.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Float::parseFloat, true)));
        register(m, Float.class, of(() -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Float::parseFloat, true)));
        register(m, boolean.class, of(() -> new BooleanComponent(0, 0, 100, 20)));
        register(m, Boolean.class, of(() -> new BooleanComponent(0, 0, 100, 20)));

        register(m, ConfigPreset.class, (field, cls, raw, params) -> {
            if (params == null || !(params[1] instanceof Class<?> clazz)) return null;
            return () -> new PresetComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, clazz);
        });
    }); // todo do the rest (all autocodec ones, maps, enums, registries, colors, ranges)

    static Supplier<ConfigComponent> getFor(Type type) {
        Type[] params = null;
        Class cls;

        if (type instanceof ParameterizedType pt) {
            params = pt.getActualTypeArguments();
            cls = (Class) pt.getRawType();
        } else if (type instanceof Class c) cls = c;
        else {
            Nucleus.LOGGER.error("Config entries(when used for the config editing GUI) cannot Wildcards or TypeParameters! (Or anything other than a ParameterizedType or Class). Type: {}", type);
            throw new IllegalStateException("Config entry attempted to use Wildcard/TypeParameter. See logger error above.");
        }

        return getFor(null, cls, type, params);
    }

    static <T> Supplier<ConfigComponent<T>> getFor(Field field) {
        Type[] params = field.getGenericType() instanceof ParameterizedType pt ? pt.getActualTypeArguments() : null;
        Class cls = field.getType();
        Type raw = field.getGenericType();
        return getFor(field, cls, raw, params);
    }

    static <T> Supplier<ConfigComponent<T>> getFor(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params) {
        ConfigComponentType type = ConfigComponentType.get(cls); // todo default value
        if (type != null)
            return type.getComponentCreator(field, cls, raw, params);

        for (ConfigComponentType dynamic : dynamics) {
            Supplier result = dynamic.getComponentCreator(field, cls, raw, params);
            if (result != null)
                return result;
        }

        return null; // TODO default entry
    }

    static <T> void register(Map<Class<?>, ConfigComponentType<?>> map, Class<T> cls, ConfigComponentType<T> componentType) {
        map.put(cls, componentType);
    }

    static <T> ConfigComponentType<T> get(Class<T> cls) {
        return (ConfigComponentType<T>) registered.get(cls);
    }

    static <T> void register(Class<T> cls, ConfigComponentType<T> componentType) {
        registered.put(cls, componentType);
    }

    static <T> void register(Class<T> cls, Supplier<ConfigComponent<T>> supplier) {
        registered.put(cls, ConfigComponentType.of(supplier));
    }

    static <T> ConfigComponentType<T> of(Supplier<ConfigComponent<T>> supplier) {
        return (f, c, r, p) -> supplier;
    }

    /**
     * @param params the type parameters of the field (null if none)
     */
    Supplier<ConfigComponent<T>> getComponentCreator(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params);
}
