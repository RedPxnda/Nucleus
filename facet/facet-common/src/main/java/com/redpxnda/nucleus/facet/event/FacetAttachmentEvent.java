package com.redpxnda.nucleus.facet.event;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.facet.FacetKey;

import java.util.HashMap;
import java.util.Map;

public interface FacetAttachmentEvent<T> {
    void attach(T object, FacetAttacher attacher);

    class FacetAttacher {
        public final Map<FacetKey<?>, Facet<?>> facets = new HashMap<>();

        public <T extends Facet<?>> void add(FacetKey<T> key, T val) {
            facets.put(key, val);
        }
    }
}
