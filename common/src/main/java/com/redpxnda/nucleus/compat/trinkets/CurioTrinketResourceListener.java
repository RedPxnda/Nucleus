package com.redpxnda.nucleus.compat.trinkets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.util.JsonUtil;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class CurioTrinketResourceListener extends SimpleJsonResourceReloadListener {
    private static int invalidItemCount = 0;
    public static final Map<String, SlotName> slotNames = new HashMap<>();

    public CurioTrinketResourceListener() {
        super(Nucleus.GSON, "trinket_data");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        boolean curiosLoaded = Platform.isModLoaded("curios");
        if (!Platform.isModLoaded("trinkets") && !curiosLoaded) return;
        slotNames.clear();
        invalidItemCount = 0;
        object.forEach((key, value) -> {
            if (!Nucleus.isNamespaceValid(key.getNamespace())) return;
            if (!(value instanceof JsonObject obj))
                throw new RuntimeException("JSON data used for trinket data at '" + key + "' is not a JSON object! -> " + value);

            JsonUtil.runIfPresent(obj, "slotNames", sn -> { // making name translations
                if (!(sn instanceof JsonObject names))
                    throw new RuntimeException("JSON data used for slot names data at '" + key + "' is not a JSON object! -> " + value);

                names.asMap().forEach((slot, raw) -> {
                    JsonObject element = raw.getAsJsonObject();
                    JsonElement curios = element.get("curios");
                    JsonElement trinkets = element.get("trinkets");
                    if (curios == null || trinkets == null)
                        throw new RuntimeException("JSON data used for slot names translation at '" + key + "' is missing either curios or trinkets key! -> " + value);

                    slotNames.put(slot, new SlotName(trinkets.getAsString(), curios.getAsString()));
                });
            });

            /*JsonUtil.runIfPresent(obj, "entityAttachment", raw -> {
                if (!(raw instanceof JsonArray data))
                    throw new RuntimeException("JSON data used for entity attachments data at '" + key + "' is not a JSON array! -> " + value);

                data.forEach(emnt -> {
                    if (!(emnt instanceof JsonObject element))
                        throw new RuntimeException("JSON data used for an entity attachment at '" + key + "' is not a JSON object! -> " + value);

                    JsonUtil.runIfPresent();
                });
            });*/

            // tags not supported yet
            /*JsonUtil.runIfPresent(obj, "tags", ts -> {
                if (!(ts instanceof JsonObject tags))
                    throw new RuntimeException("JSON data used for tags data at '" + key + "' is not a JSON object! -> " + value);

                tags.asMap().forEach((tag, element) -> {
                    if (!(element instanceof JsonArray array))
                        throw new RuntimeException("JSON data used for a tag at '" + key + "' is not a JSON array! -> " + value);

                    SlotName names = slotNames.get(tag);
                    String slotName = names == null ? tag : curiosLoaded ? names.curios : names.trinkets;

                    String namespace = curiosLoaded ? "curios" : "trinkets";
                    ResourceLocation location = new ResourceLocation(namespace, slotName);

                    TagKey<Item> tagKey = TagKey.create(Registries.ITEM, location);
                    List<Item> items = new ArrayList<>();
                    array.forEach(e -> {
                        Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(e.getAsString())).orElse(null);
                        if (item != null)
                            items.add(item);
                        else
                            invalidItemCount++;
                    });

                    //BuiltInRegistries.ITEM.getOrCreateTag(tagKey)
                });
            });*/
        });
        Nucleus.LOGGER.warn("Found {} invalid item(s) used for Nucleus trinket data. Intentional?", invalidItemCount);
    }

    public record SlotName(String trinkets, String curios) {}
}
