package com.redpxnda.nucleus.expression.mappings;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.registry.NucleusNamespaces;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MappingsDataLoader extends SinglePreparationResourceReloader<Map<Identifier, MappingTree>> {
    private static final Logger LOGGER = Nucleus.getLogger();
    public static MappingsDataLoader INSTANCE = new MappingsDataLoader();

    protected final Map<String, MappingFormat> extensionToFormat = new HashMap<>();
    protected Map<Identifier, MappingTree> loadedMappings;

    public MappingsDataLoader() {
        for (MappingFormat format : MappingFormat.values()) {
            extensionToFormat.put(format.fileExt, format);
        }
    }

    public MappingTree getMappings(Identifier id) {
        return loadedMappings.get(id);
    }

    public Map<Identifier, MappingTree> getAllLoadedMappings() {
        return loadedMappings;
    }

    public static String getFileExtension(String file) {
        String[] parts = file.split("\\.");
        if (parts.length == 0) return "";
        return parts[parts.length-1];
    }

    @Override
    protected Map<Identifier, MappingTree> prepare(ResourceManager manager, Profiler profiler) {
        Map<Identifier, MappingTree> result = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : manager.findResources("mappings", id -> NucleusNamespaces.isNamespaceValid(id.getNamespace()) && extensionToFormat.containsKey(getFileExtension(id.getPath()))).entrySet()) {
            Identifier location = entry.getKey();

            String string = location.getPath();
            String fileExtension = getFileExtension(location.getPath());
            Identifier id = location.withPath(string.substring("mappings".length() + 1, string.length() - fileExtension.length() - 1));

            try {
                BufferedReader reader = entry.getValue().getReader();
                try {
                    MemoryMappingTree tree = new MemoryMappingTree();
                    MappingReader.read(reader, tree);

                    result.put(id, tree);
                } finally {
                    if (reader == null) continue;
                    reader.close();
                }
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse mapping data from '" + location + "'", exception);
            }
        }
        return result;
    }

    @Override
    protected void apply(Map<Identifier, MappingTree> prepared, ResourceManager manager, Profiler profiler) {
        loadedMappings = prepared;
        System.out.println("Mappings done .. .. " + loadedMappings);

        TwoStepTreeRemapper remapper = new TwoStepTreeRemapper(
                loadedMappings.get(new Identifier("nucleus:mojmap")),
                loadedMappings.get(new Identifier("nucleus:yarn")),
                "source",
                "target",
                "official",
                "named"
        );

        String result = remapper.mapFieldName("net/minecraft/client/Minecraft", "mouseHandler", "Lnet/minecraft/client/MouseHandler;");
        System.out.println(result);
    }
}
