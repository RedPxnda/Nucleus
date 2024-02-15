package com.redpxnda.nucleus.codec.misc;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CollectionCodec<E, C extends Collection<E>> implements Codec<C> {
    protected final Codec<E> elementCodec;
    protected final Function<List<E>, C> collectionCreator;

    protected CollectionCodec(Codec<E> elementCodec, Function<List<E>, C> collectionCreator) {
        this.elementCodec = elementCodec;
        this.collectionCreator = collectionCreator;
    }
    public static <E, C extends Collection<E>> CollectionCodec<E, C> of(Codec<E> codec, Function<List<E>, C> creator) {
        return new CollectionCodec<>(codec, creator);
    }
    public static <E, C extends Collection<E>> CollectionCodec<E, C> of(Codec<E> codec, Supplier<C> creator) {
        return new CollectionCodec<>(codec, list -> {
            C c = creator.get();
            c.addAll(list);
            return c;
        });
    }

    @Override
    public <T> DataResult<T> encode(C input, DynamicOps<T> ops, T prefix) {
        ListBuilder<T> builder = ops.listBuilder();

        for (E e : input) {
            builder.add(elementCodec.encodeStart(ops, e));
        }

        return builder.build(prefix);
    }

    @Override
    public <T> DataResult<Pair<C, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
            ImmutableList.Builder<E> read = ImmutableList.builder();
            Stream.Builder<T> failed = Stream.builder();
            MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            stream.accept(t -> {
                DataResult<Pair<E, T>> element = elementCodec.decode(ops, t);
                element.error().ifPresent(e -> failed.add(t));
                result.setValue(result.getValue().apply2stable((r, v) -> {
                    read.add(v.getFirst());
                    return r;
                }, element));
            });

            ImmutableList<E> elements = read.build();
            T errors = ops.createList(failed.build());

            Pair<C, T> pair = Pair.of(collectionCreator.apply(elements), errors);

            return result.getValue().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public String toString() {
        return "CollectionCodec[" + elementCodec + ']';
    }
}
