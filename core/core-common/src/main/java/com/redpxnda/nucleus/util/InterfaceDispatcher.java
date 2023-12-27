package com.redpxnda.nucleus.util;

import com.google.common.reflect.AbstractInvocationHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class InterfaceDispatcher<T> {
    @SafeVarargs
    public static <T> InterfaceDispatcher<T> of(Map<String, T> handlers, String typeKey, boolean allowSingleString, T... classGetter) {
        return new InterfaceDispatcher<>(handlers, typeKey, allowSingleString, classGetter);
    }
    @SafeVarargs
    public static <T> InterfaceDispatcher<T> of(Map<String, T> handlers, String typeKey, T... classGetter) {
        return new InterfaceDispatcher<>(handlers, typeKey, true, classGetter);
    }

    private final Class<T> cls;
    protected final T dispatcher;

    @SafeVarargs
    protected InterfaceDispatcher(Map<String, T> handlers, String typeKey, boolean allowSingleString, T... classGetter) {
        cls = (Class<T>) classGetter.getClass().getComponentType();
        dispatcher = (T) Proxy.newProxyInstance(InterfaceDispatcher.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                JsonElement element = (JsonElement) args[0];
                String key = "";
                if (allowSingleString && element instanceof JsonPrimitive primitive)
                    key = primitive.getAsString();
                else if (element instanceof JsonObject object)
                    key = object.getAsJsonPrimitive(typeKey).getAsString();
                else
                    throw new JsonParseException("Failed to get key '" + typeKey + "' from JSON! -> Not a JSON object: " + "'" + element + "'");
                return MethodHandles.lookup().unreflect(method).bindTo(handlers.get(key)).invokeWithArguments(args);
            }
        });
    }

    public T dispatcher() {
        return dispatcher;
    }
}
