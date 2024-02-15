package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.ConfigAutoCodec;
import com.redpxnda.nucleus.codec.behavior.AnnotationBehaviorGetter;
import com.redpxnda.nucleus.codec.behavior.BehaviorOutline;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.codec.behavior.TypeBehaviorGetter;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigComponentBehavior {
    private static final Logger LOGGER = Nucleus.getLogger();
    protected static final BehaviorOutline<Getter<?>, AnnotationGetter<?>> getters = new BehaviorOutline<>(false);

    static {
        registerDynamic((field, cls, raw, params, root) -> {
            if (Collection.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type elementType = MiscUtil.collectionElement(raw);
                return new CollectionComponent(() -> MiscUtil.createCollection((Class) cls), elementType, 0, 0);
            }
            return null;
        });

        registerDynamic((field, cls, raw, params, root) -> {
            if (Map.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type[] types = MiscUtil.mapElements(raw);
                return new MapComponent(() -> MiscUtil.createMap(raw, (Class) cls), types[0], types[1], 0, 0);
            }
            return null;
        });

        registerDynamic((field, cls, raw, params, root) -> {
            if (cls.isEnum() && !getUnsafeOutline().statics.containsKey(cls))
                return new DropdownComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, cls);
            return null;
        });

        registerDynamic((field, cls, raw, params, root) -> {
            if (cls.isAnnotationPresent(ConfigAutoCodec.ConfigClassMarker.class)) {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                return new ConfigEntriesComponent(ConfigAutoCodec.performFieldSearch(cls).entrySet().stream()
                        .map(entry -> Map.entry(entry.getKey(), new Pair<>(entry.getValue(), getComponent(entry.getValue(), true))))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                        MinecraftClient.getInstance().textRenderer, 0, 0, screen == null ? 200 : screen.width, 20); // height updated later
            }
            return null;
        });

        registerAnnotator(CodecBehavior.Optional.class, (annotation, field, cls, raw, params, isRoot) -> {
            if (isRoot) {
                ConfigComponent<?> child = getComponent(field, cls, raw, params, false);
                return new OptionalComponent(MinecraftClient.getInstance().textRenderer, 0, 0, child.getWidth(), child.getHeight(), child, c -> {});
            }
            return null;
        });

        registerClass(String.class, () -> new TextFieldComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20));
        registerClass(int.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Integer::parseInt, false));
        registerClass(Integer.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Integer::parseInt, false));
        registerClass(byte.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Byte::parseByte, false));
        registerClass(Byte.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Byte::parseByte, false));
        registerClass(short.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Short::parseShort, false));
        registerClass(Short.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Short::parseShort, false));
        registerClass(long.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Long::parseLong, false));
        registerClass(Long.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Long::parseLong, false));
        registerClass(double.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Double::parseDouble, true));
        registerClass(Double.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Double::parseDouble, true));
        registerClass(float.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Float::parseFloat, true));
        registerClass(Float.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Float::parseFloat, true));
        registerClass(boolean.class, () -> new BooleanComponent(0, 0, 100, 20));
        registerClass(Boolean.class, () -> new BooleanComponent(0, 0, 100, 20));

        registerClass(Color.class, () -> new ColorComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20));

        registerClass(ConfigPreset.class, (field, cls, raw, params, root) -> {
            if (params == null || !(params[1] instanceof Class<?> clazz)) return null;
            return new PresetComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, clazz);
        }); // todo do the rest (all autocodec ones, registries, colors, ranges, arrays)
    }

    public static <T> ConfigComponent<T> getComponent(Type type, boolean isRoot) {
        return (ConfigComponent<T>) getters.get(type, isRoot); // todo default raw editor !!!!!!!!!!!!!
    }

    public static <T> ConfigComponent<T> getComponent(Field field, boolean isRoot) {
        return (ConfigComponent<T>) getters.get(field, isRoot);
    }

    /**
     * @param field the field to be used as reference... used for things like annotations
     * @param cls the class of the field, or the class of the codec if no field
     * @param raw the raw "generic type" of the field, or the class if no field
     * @param params the type params of the field, or null if no field
     * @return a codec for this field/class
     */
    public static <T> ConfigComponent<T> getComponent(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        return (ConfigComponent<T>) getters.get(field, cls, raw, params, isRoot);
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
    public static <T> void registerClass(Class<T> cls, Supplier<ConfigComponent<T>> getter) {
        getters.statics.put(cls, Getter.fromSupplier(getter));
    }

    public static <A extends Annotation> void registerAnnotator(Class<A> cls, AnnotationGetter<A> getter) {
        getters.annotators.put(cls, getter);
    }

    public static void registerDynamic(Getter<?> getter) {
        getters.dynamics.add(getter);
    }

    public interface AnnotationGetter<A extends Annotation> extends AnnotationBehaviorGetter<A, ConfigComponent<?>> {}

    public interface Getter<T> extends TypeBehaviorGetter<ConfigComponent<T>, T> {
        static <T> Getter<T> fromSupplier(Supplier<ConfigComponent<T>> supplier) {
            return (field, cls, raw, params, root) -> supplier.get();
        }
    }
}
