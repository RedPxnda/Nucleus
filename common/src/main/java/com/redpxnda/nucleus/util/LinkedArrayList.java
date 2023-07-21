package com.redpxnda.nucleus.util;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class LinkedArrayList<T, E> extends ArrayList<E> {
    protected final T connection;
    protected final BiConsumer<T, E> onElementAdd;

    public LinkedArrayList(T connection, BiConsumer<T, E> onElementAdd) {
        this.connection = connection;
        this.onElementAdd = onElementAdd;
    }

    @Override
    public boolean add(E e) {
        onElementAdd.accept(connection, e);
        return super.add(e);
    }
}
