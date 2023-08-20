package com.redpxnda.nucleus.wrappers;

import com.redpxnda.nucleus.Nucleus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wrappers {
    private static final Map<Class<?>, Wrapper<?>> wrappers = new HashMap<>();

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

    public static void init() {
        register(Map.class, Map::get);
        register(HashMap.class, HashMap::get);
        register(List.class, Wrappers::getFromList);
        register(ArrayList.class, Wrappers::getFromList);
    }

    private static Object getFromList(List<?> list, String key) {
        int index;
        try {
            index = Integer.parseInt(key);
        } catch (Exception e) {
            Nucleus.LOGGER.error("Cannot use key '{}' for list wrapper! Key must be an integer representing object index!", key);
            throw new RuntimeException(e);
        }

        return list.get(index);
    }

    private Wrappers() {}
}
