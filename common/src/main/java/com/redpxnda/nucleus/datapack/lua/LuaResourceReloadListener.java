package com.redpxnda.nucleus.datapack.lua;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public abstract class LuaResourceReloadListener extends SinglePreparationResourceReloader<Map<Identifier, LuaValue>> {
    protected final Globals globals;
    protected final String directory;

    protected LuaResourceReloadListener(Globals globals, String directory) {
        this.globals = globals;
        this.directory = directory;
    }

    @Override
    protected Map<Identifier, LuaValue> prepare(ResourceManager rm, Profiler pf) {
        Map<Identifier, LuaValue> files = new HashMap<>();

        for (Map.Entry<Identifier, Resource> entry : rm.findResources(directory, path -> path.toString().endsWith(".lua")).entrySet()) {
            try (InputStream stream = entry.getValue().getInputStream()) {
                files.put(entry.getKey(), globals.load(stream, "@" + entry.getKey().toString(), "bt", globals));
            } catch (IOException e) {
                LOGGER.error("Failed to load lua resource at {}", entry.getKey());
                e.printStackTrace();
            }
        }

        return files;
    }
}
