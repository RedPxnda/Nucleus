package com.redpxnda.nucleus.datapack.references;

import com.mojang.logging.LogUtils;
import com.redpxnda.nucleus.datapack.json.annotation.ContextProvider;
import com.redpxnda.nucleus.datapack.json.annotation.ContextSettings;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import jdk.jfr.Label;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Reference<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Class<?>, Map<String, ContextSettings<?, ?>>> CONTEXT_PROVIDERS = new HashMap<>();
    public static ContextSettings<?, ?> getContextSettings(Class<? extends Reference<?>> clazz, String str) {
        return CONTEXT_PROVIDERS.get(clazz).get(str);
    }
    private static void putProvider(Class<? extends Reference<?>> clazz, String str, ContextSettings<?, ?> settings) {
        CONTEXT_PROVIDERS.get(clazz).put(str, settings);
    }

    @Label("Disabled until classes refactor to support JSON context. (prob never lol)")
    public static void register(Class<?> clazz) {
//        CONTEXT_PROVIDERS.put(clazz, new HashMap<>());
//        loadClassMethods(clazz);
    }
    private static void loadClassMethods(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(ContextProvider.class)) {
                String value = method.getAnnotation(ContextProvider.class).value();
                try {
                    Method referenced = PlayerReference.class.getMethod(value);
                    referenced.setAccessible(true);
                    Reference.putProvider(PlayerReference.class, method.getName(), (ContextSettings<?, ?>) referenced.invoke(null));
                } catch (NoSuchMethodException e) {
                    LOGGER.error("Context providing method '{}' references an invalid method: '{}'", method.getName(), value);
                    e.printStackTrace();
                } catch (InvocationTargetException | IllegalAccessException e) {
                    LOGGER.error("Failed to invoke method '{}' when accessing context providing method '{}'.", value, method.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public final T instance;

    public Reference(T instance) {
        this.instance = instance;
    }

    public T instance() {
        return instance;
    }
}
