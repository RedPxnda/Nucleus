package com.redpxnda.nucleus.registry;

import com.redpxnda.nucleus.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

public class NucleusNamespaces {
    private static final List<String> ADDON_NAMESPACES = MiscUtil.initialize(new ArrayList<>(), l -> l.add("nucleus"));

    public static void addAddonNamespace(String namespace) {
        ADDON_NAMESPACES.add(namespace);
    }

    public static boolean isNamespaceValid(String namespace) {
        return ADDON_NAMESPACES.contains(namespace);
    }
}
