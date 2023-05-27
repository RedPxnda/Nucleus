package com.redpxnda.nucleus.datapack.constants;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    private static final Map<String, Map<String, Object>> constants = new HashMap<>();
    public static Map<String, Map<String, Object>> getConstants() {
        return constants;
    }

    public static void set(ResourceLocation loc, Object value) {
        Map<String, Object> map;
        String namespace = loc.getNamespace();
        if (constants.containsKey(namespace))
            map = constants.get(namespace);
        else {
            map = new HashMap<>();
            constants.put(namespace, map);
        }
        map.put(loc.getPath(), value);
    }
    public static Object get(ResourceLocation loc) {
        return constants.get(loc.getNamespace()).get(loc.getPath());
    }
    public static Object get(String namespace, String path) {
        return constants.get(namespace).get(path);
    }


//    public static void add(String modId, String key, String value) {
//        constants.put("$_" + modId + "." + key + "_", value);
//    }
//    public static void addStringList(String modId, String key, List<String> value) {
//        constants.put("$_" + modId + "." + key + "_", value);
//    }
//    public static <T extends Number> void add(String modId, String key, T value) {
//        constants.put("$_" + modId + "." + key + "_", value);
//    }
//    public static <T extends Number> void addNumberList(String modId, String key, List<T> value) {
//        constants.put("$_" + modId + "." + key + "_", value);
//    }
//    public static void add(String modId, String key, ResourceLocation value) {
//        constants.put("$_" + modId + "." + key + "_", value.toString());
//    }
//    public static void add(String modId, String key, boolean value) {
//        constants.put("$_" + modId + "." + key + "_", value);
//    }
//    public static void addBooleanList(String modId, String key, List<Boolean> value) {
//        constants.put("$_" + modId + "." + key + "_", value);
//    }
}
