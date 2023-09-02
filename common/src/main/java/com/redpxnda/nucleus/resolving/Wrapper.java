package com.redpxnda.nucleus.resolving;

import com.redpxnda.nucleus.util.MiscUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public interface Wrapper<T> {
    Wrapper emptyWrapper = (instance, key) -> null;
    Map<String, BiFunction<Wrapper, Object, Object>> defaultMethods = MiscUtil.initialize(new HashMap<>(), map -> {
        map.put("is_null", (w, i) -> i == null);
        map.put("isNull", (w, i) -> i == null);
        map.put("is_empty", (w, i) -> w.isEmpty(i));
        map.put("isEmpty", (w, i) -> w.isEmpty(i));
        map.put("or_0", (w, i) -> i == null ? 0 : i);
        map.put("or_empty", (w, i) -> i == null ? "" : i);
    });

    Object customInvoke(@NotNull T instance, String key);

    default Object invoke(T instance, String key) {
        var method = defaultMethods.get(key);
        return method == null ?
                instance == null ? null : customInvoke(instance, key) :
                method.apply(this, instance);
    }

    default boolean isEmpty(T instance) {
        return instance == null;
    }
}
