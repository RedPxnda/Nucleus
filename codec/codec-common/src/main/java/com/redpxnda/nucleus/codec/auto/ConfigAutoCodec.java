package com.redpxnda.nucleus.codec.auto;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.ops.JsoncOps;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.json.JsoncElement;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

/**
 * {@link AutoCodec} but with support for {@link Comment comments} when serializing to {@link JsoncOps} (and can also apply to a "default" instance, for merge reasons)
 */
public class ConfigAutoCodec<C> extends AutoCodec<C> {
    private static final Logger LOGGER = Nucleus.getLogger();
    protected final @Nullable Supplier<C> creator;

    public ConfigAutoCodec(Class<C> cls, String errorMsg, Map<String, Field> fieldMap, @Nullable Supplier<C> creator) {
        super(cls, errorMsg, fieldMap);
        this.creator = creator;
    }
    public ConfigAutoCodec(Class<C> cls, String errorMsg, @Nullable Supplier<C> creator) {
        super(cls, errorMsg);
        this.creator = creator;
    }
    public static <T> ConfigAutoCodec<T> of(Class<T> cls) {
        return new ConfigAutoCodec<>(cls, "Field not present for " + cls.getSimpleName() + ".", null);
    }
    public static <T> ConfigAutoCodec<T> of(Class<T> cls, Supplier<T> creator) {
        return new ConfigAutoCodec<>(cls, "Field not present for " + cls.getSimpleName() + ".", creator);
    }
    public static <T> ConfigAutoCodec<T> of(Class<T> cls, String errorMsg) {
        return new ConfigAutoCodec<>(cls, errorMsg, null);
    }
    public static <T> ConfigAutoCodec<T> of(Class<T> cls, String errorMsg, Supplier<T> creator) {
        return new ConfigAutoCodec<>(cls, errorMsg, creator);
    }

    @Override
    public <T> RecordBuilder<T> encode(C input, DynamicOps<T> ops, RecordBuilder<T> map) {
        RecordBuilder<T> result = super.encode(input, ops, map);
        T obj = result.build(ops.emptyMap()).getOrThrow(false, s -> LOGGER.error("Failed to build RecordBuilder in ConfigAutoCodec! -> {}", s));

        RecordBuilder<T> withComments = ops.mapBuilder();
        ops.getMapValues(obj).getOrThrow(false, s -> LOGGER.error("Failed to create map in ConfigAutoCodec! -> {}", s)).forEach(pair -> {
            T key = pair.getFirst();
            T val = pair.getSecond();
            if (val == null) val = ops.empty();

            if (val instanceof JsoncElement element) {
                DataResult<String> stringResult = ops.getStringValue(key);
                if (stringResult.result().isPresent()) {
                    String str = stringResult.result().get();
                    AutoCodecField field = fields.get(str);
                    Comment comment = field.field().getAnnotation(Comment.class);
                    if (comment != null) element.setComment(comment.value());
                }
            }

            withComments.add(key, val);
        });
        return withComments;
    }

    @java.lang.Override
    protected C createCInstance(Class<C> cls) {
        if (creator != null) return creator.get();
        return super.createCInstance(cls);
    }

    @java.lang.Override
    protected boolean defaultSetIfNullBehavior(AutoCodecField field, @Nullable Object value, Object classInstance) {
        return false;
    }

    @java.lang.Override
    public String toString() {
        return "Config" + super.toString();
    }

    // marks that this class should be treated as a config (used for GUI support for inner configs)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ConfigClassMarker {
    }
}
