package com.redpxnda.nucleus.util;

import java.util.ArrayList;

public class LimitedArrayList<E> extends ArrayList<E> {
    protected int maxSize;

    public LimitedArrayList(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E e) {
        if (size() >= maxSize) remove(0);
        return super.add(e);
    }
}
