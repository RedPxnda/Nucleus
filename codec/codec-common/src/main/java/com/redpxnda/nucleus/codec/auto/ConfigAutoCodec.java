package com.redpxnda.nucleus.codec.auto;

import com.redpxnda.nucleus.codec.ops.JsoncOps;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.json.JsoncElement;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

/**
 * {@link AutoCodec} but with support for {@link Comment comments} when serializing to {@link JsoncOps} (and can also apply to a "default" instance, for merge reasons)
 */
public class ConfigAutoCodec<C> extends AutoCodec<C> {
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

    @java.lang.Override
    public <T> void adjustSerializedObject(String key, T serialized, Object original, Field field, C holder) {
        Comment comment = field.getAnnotation(Comment.class);
        if (serialized instanceof JsoncElement element && comment != null) {
            element.setComment(comment.value());
        }
    }

    @java.lang.Override
    protected C createCInstance(Class<C> cls) {
        if (creator != null) return creator.get();
        return super.createCInstance(cls);
    }

    @java.lang.Override
    protected boolean isFieldOptional(String key, AutoCodecField field, Object instance, boolean encoding) {
        if (!encoding)
            try {
                if (instance.getClass().getField(field.fieldName()).get(instance) != null) return true;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        return super.isFieldOptional(key, field, instance, encoding);
    }

    @java.lang.Override
    protected <T> AutoCodec<T> createAutoCodecWith(Class<T> cls, String errorMsg) {
        return new ConfigAutoCodec<>(cls, errorMsg, null);
    }

    @java.lang.Override
    public String toString() {
        return "Config" + super.toString();
    }

    // marks that this class should be treated as a config (used for GUI support for inner configs)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigClassMarker {
    }
}
