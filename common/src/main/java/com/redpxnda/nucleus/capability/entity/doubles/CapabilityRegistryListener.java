package com.redpxnda.nucleus.capability.entity.doubles;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.redpxnda.nucleus.Nucleus;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CapabilityRegistryListener extends JsonDataLoader {
    public static Map<String, JsonElement> data = new HashMap<>();

    public CapabilityRegistryListener() {
        super(Nucleus.GSON, "capabilities");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> object, ResourceManager resourceManager, Profiler profilerFiller) {
        Map<String, JsonElement> map = object.entrySet().stream()
                .filter(entry -> Nucleus.isNamespaceValid(entry.getKey().getNamespace()))
                .map(entry -> Map.entry(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        fireWith(map);
        data = map;
    }
    public static void fireWith(Map<String, JsonElement> files) {
        DoublesCapability.defaultValues.clear();
        boolean isClient = Platform.getEnvironment() == Env.CLIENT;
        if (isClient) ClientCapabilityListener.renderers.clear();
        files.forEach((path, value) -> {
            List<JsonObject> elements = new ArrayList<>();
            if (value instanceof JsonArray array)
                elements.addAll(array.asList().stream().filter(e -> e instanceof JsonObject).map(JsonElement::getAsJsonObject).toList());
            else if (value instanceof JsonObject obj)
                elements.add(obj);
            else
                Nucleus.LOGGER.warn("Found a JSON element used for a simple capability that isn't a JSON object or array!");

            elements.forEach(element -> {
                if (!(element.get("name") instanceof JsonPrimitive prim) || !prim.isString()) {
                    throw new RuntimeException("Key 'name' for simple capability at '" + path + "' is either missing or not a valid string!");
                }
                String name = new Identifier(element.get("name").getAsString()).toString(); // forcing resource location format for compat reasons

                if (element.get("defaultValue") instanceof JsonPrimitive primitive)
                    DoublesCapability.defaultValues.put(name, primitive.getAsDouble());
                if (element.get("rendering") instanceof JsonObject obj && isClient)
                    ClientCapabilityListener.parseRenderingObject(obj, name);
            });
        });
    }
}
