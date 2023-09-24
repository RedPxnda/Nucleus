package com.redpxnda.nucleus.facet;

public interface FacetHolder {
    FacetInventory getFacets();
    default void setFacet(FacetKey key, Facet val) {
        getFacets().asMap().put(key, val);
    }
    default void setFacetsFromAttacher(FacetAttachmentEvent.FacetAttacher attacher) {
        attacher.facets.forEach(this::setFacet);
    }

    static FacetHolder of(Object holder) {
        return (FacetHolder) holder;
    }
}
