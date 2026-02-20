package com.redpxnda.nucleus.registration.forge;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.redpxnda.nucleus.registration.RegistryAnalyzer;
import com.redpxnda.nucleus.registration.RegistryId;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RegistryAnalyzerImpl {
    public static final Multimap<String, Tuple<Supplier<Map<ResourceKey<?>, Map<ResourceLocation, Object>>>, Consumer<Object>>> registrationListeners = Multimaps.newMultimap(new ConcurrentHashMap<>(), HashSet::new);

    public static void register(String modId, Supplier<Class<?>> holderClass, Consumer<Object> finishListener) {
        registrationListeners.put(modId, new Tuple<>(() -> {
            Map<ResourceKey<?>, Map<ResourceLocation, Object>> map = new HashMap<>();
            Class<?> cls = holderClass.get();
            for (Field field : cls.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);

                RegistryId id = field.getAnnotation(RegistryId.class);
                if (id == null) continue;

                Class<?> type = field.getType();
                ResourceKey<?> reg = null;
                if (id.registry().equals("default")) {
                    for (Map.Entry<Class<?>, Registry<?>> entry : MiscUtil.objectsToRegistries.entrySet()) {
                        if (entry.getKey().isAssignableFrom(type)) {
                            reg = entry.getValue().key();
                            break;
                        }
                    }
                } else {
                    try {
                        Field override = cls.getField(id.registry());
                        override.setAccessible(true);
                        if (Modifier.isStatic(override.getModifiers())) {
                            Type overrideType = override.getGenericType();
                            if (overrideType instanceof ParameterizedType pt && pt.getRawType().equals(Supplier.class)) {
                                Type[] subs = pt.getActualTypeArguments();
                                if (subs.length > 0 && subs[0] instanceof ParameterizedType inner && inner.getRawType().equals(Registry.class)) {
                                    reg = ((Supplier<Registry>)override.get(null)).get().key();
                                } else {
                                    RegistryAnalyzer.LOGGER.warn("Field '{}' used as an override for id '{}' in registry class '{}' is not a Supplier<Registry<...>>!", id.registry(), id.value(), cls.getSimpleName());
                                }
                            } else {
                                RegistryAnalyzer.LOGGER.warn("Field '{}' used as an override for id '{}' in registry class '{}' is not a Supplier!", id.registry(), id.value(), cls.getSimpleName());
                            }
                        } else {
                            RegistryAnalyzer.LOGGER.warn("Field '{}' used as an override for id '{}' in registry class '{}' is not static!", id.registry(), id.value(), cls.getSimpleName());
                        }
                    } catch (Exception e) {
                        RegistryAnalyzer.LOGGER.warn("Failed to get field '" + id.registry() + "' used as a registry override for id '" + id.value() + "' of registry class '" + cls.getSimpleName() + "'! -> ", e);
                    }
                }

                if (reg == null) {
                    RegistryAnalyzer.LOGGER.warn("Failed to find registry type for type '{}' in '{}'!", type.getSimpleName(), cls.getSimpleName());
                    continue;
                }

                Map<ResourceLocation, Object> objects = map.computeIfAbsent(reg, k -> new HashMap<>());
                ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(modId, id.value());
                try {
                    if (objects.containsKey(identifier)) RegistryAnalyzer.LOGGER.warn("Identifier '{}' has been used for multiple entries of the same type for registry class '{}'!", identifier, cls.getSimpleName());
                    objects.put(identifier, field.get(null));
                } catch (Exception e) {
                    RegistryAnalyzer.LOGGER.warn("Failed to register key '" + identifier + "' for registry class '" + cls.getSimpleName() + "'! -> ", e);
                }
            }
            return map;
        }, finishListener));
    }

    public static void register(String modId, Supplier<Class<?>> holderClass) {
        register(modId, holderClass, obj -> {});
    }
}
