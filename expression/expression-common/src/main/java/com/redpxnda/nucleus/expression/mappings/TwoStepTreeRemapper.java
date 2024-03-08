package com.redpxnda.nucleus.expression.mappings;

import net.fabricmc.mappingio.extras.MappingTreeRemapper;
import net.fabricmc.mappingio.tree.MappingTree;

public class TwoStepTreeRemapper {
    public final MappingTreeRemapper first;
    public final MappingTreeRemapper second;

    public TwoStepTreeRemapper(MappingTree startTree, MappingTree endTree, String startNamespace, String intermediateNamespace, String endNamespace) {
        first = new MappingTreeRemapper(startTree, startNamespace, intermediateNamespace);
        second = new MappingTreeRemapper(endTree, intermediateNamespace, endNamespace);
    }

    public TwoStepTreeRemapper(MappingTree startTree, MappingTree endTree, String start, String interm, String interm2, String end) {
        first = new MappingTreeRemapper(startTree, start, interm);
        second = new MappingTreeRemapper(endTree, interm2, end);
    }

    protected TwoStepTreeRemapper(MappingTreeRemapper first, MappingTreeRemapper second) {
        this.first = first;
        this.second = second;
    }

    public TwoStepTreeRemapper reverse() {
        return new TwoStepTreeRemapper(second, first);
    }

    public String mapClassName(String internalName) {
        return second.map(first.map(internalName));
    }

    public String mapMethodName(String owner, String name, String descriptor) {
        return second.mapMethodName(first.map(owner), first.mapMethodName(owner, name, descriptor), first.mapMethodDesc(descriptor));
    }

    public String mapFieldName(String owner, String name, String descriptor) {
        return second.mapFieldName(first.map(owner), first.mapFieldName(owner, name, descriptor), first.mapDesc(descriptor));
    }
}
