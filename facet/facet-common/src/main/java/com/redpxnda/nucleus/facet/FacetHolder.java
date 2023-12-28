package com.redpxnda.nucleus.facet;

import com.redpxnda.nucleus.facet.event.FacetAttachmentEvent;

public interface FacetHolder {
    FacetInventory getFacets();
    default void setFacet(FacetKey key, Facet val) {
        getFacets().asMap().put(key, val);
    }
    default void setFacetsFromAttacher(FacetAttachmentEvent.FacetAttacher attacher) {
        attacher.facets.forEach(this::setFacet);
    }
    default void clearFacets() {
        getFacets().asMap().clear();
    }

    static FacetHolder of(Object holder) {
        return (FacetHolder) holder;
    }
}
