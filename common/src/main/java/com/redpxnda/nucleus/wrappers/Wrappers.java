package com.redpxnda.nucleus.wrappers;

import com.redpxnda.nucleus.Nucleus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class Wrappers {
    private static final Map<Class<?>, Wrapper<?>> wrappers = new HashMap<>();
    public static final Wrapper mapWrapper = new Wrapper<Map<?, ?>>() {
        @Override
        public Object customInvoke(@NotNull Map<?, ?> instance, String key) {
            return instance.get(key);
        }

        @Override
        public boolean isEmpty(Map<?, ?> instance) {
            return Wrapper.super.isEmpty(instance) || instance.isEmpty();
        }
    };
    public static final Wrapper listWrapper = new Wrapper<List<?>>() {
        @Override
        public Object customInvoke(@NotNull List<?> list, String key) {
            if (key.equals("size") || key.equals("length")) return list.size();

            try {
                int index = Integer.parseInt(key);
                return list.get(index);
            } catch (Exception e) {
                Nucleus.LOGGER.error("Cannot use key '{}' for list wrapper! Key must be an integer representing object index!", key);
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isEmpty(List<?> instance) {
            return Wrapper.super.isEmpty(instance) || instance.isEmpty();
        }
    };

    public static <T> void register(Class<T> cls, Wrapper<T> wrapper) {
        wrappers.put(cls, wrapper);
    }
    public static <T> @Nullable Wrapper<T> get(Class<T> cls) {
        Wrapper<T> res = (Wrapper<T>) wrappers.get(cls);
        if (res != null) return res;

        Wrapper<? extends T> chosenWrapper = null;
        for (Map.Entry<Class<?>, Wrapper<?>> entry : wrappers.entrySet()) {
            Class<?> clazz = entry.getKey();
            Wrapper<?> wrapper = entry.getValue();

            if (clazz.isAssignableFrom(cls))
                chosenWrapper = (Wrapper<? extends T>) wrapper;
        }

        if (chosenWrapper != null) {
            wrappers.putIfAbsent(cls, chosenWrapper);
            return (Wrapper<T>) chosenWrapper;
        }

        return null;
    }
    public static <T> Wrapper<T> get(Class<T> cls, Wrapper<T> ifFailed) {
        Wrapper<T> result = get(cls);
        return result == null ? ifFailed : result;
    }
    public static <T> Wrapper<T> getOrThrow(Class<T> cls) {
        Wrapper<T> result = get(cls);
        if (result == null) throw new RuntimeException("Could not find a valid registered wrapper for '" + cls.getSimpleName() + "'!");
        return result;
    }

    public static <T> Wrapper<T> createAutoFor(Class<T> cls) {
        return createAutoFor(cls, "");
    }
    public static <T> Wrapper<T> createAutoFor(Class<T> cls, String prefixToRemove) {
        Map<String, Function<T, ?>> map = new HashMap<>();
        Arrays.stream(cls.getMethods()).filter(method -> {
            method.setAccessible(true);
            int mods = method.getModifiers();
            return (method.isAnnotationPresent(WrapperMethod.class) || method.getDeclaringClass().isAnnotationPresent(WrapperMethod.AllInClass.class)) && method.getParameters().length == 0 && !Modifier.isStatic(mods);
        }).forEach(method -> {
            WrapperMethod wm = method.getAnnotation(WrapperMethod.class);
            assert wm != null;

            Function<T, ?> func = t -> {
                try {
                    return method.invoke(t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Nucleus.LOGGER.error("Failed to invoke method for (Auto)Wrapper! Method Class: '{}', Method Name: '{}'", method.getDeclaringClass(), method.getName());
                    throw new RuntimeException(e);
                }
            };

            String prefix = wm.removePrefix().isEmpty() ? prefixToRemove : wm.removePrefix();
            map.put(method.getName().substring(prefix.length()), func);
            for (String alias : wm.alias()) {
                map.put(alias, func);
            }
        });

        return new AutoWrapper<>(map);
    }

    public static void init() {
        register(Map.class, mapWrapper);
        register(HashMap.class, mapWrapper);
        register(List.class, listWrapper);
        register(ArrayList.class, listWrapper);
    }

    private Wrappers() {}
}
