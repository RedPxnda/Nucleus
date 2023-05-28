package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.datapack.references.Reference;

import java.lang.reflect.InvocationTargetException;

public class LuaUtil {
    public static Object Null() {
        return null;
    }

    private static Reference<?> nullReferenceByClass(Class<? extends Reference<?>> clazz) {
        try {
            return (Reference<?>) clazz.getDeclaredConstructors()[0].newInstance((Object) null);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Reference<?> nullReference(String clazz, boolean addPackage, boolean addReference) {
        if (addPackage) clazz = Reference.class.getPackageName() + "." + clazz;
        if (addReference) clazz = clazz + "Reference";
        try {
            return nullReferenceByClass((Class<? extends Reference<?>>) Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
