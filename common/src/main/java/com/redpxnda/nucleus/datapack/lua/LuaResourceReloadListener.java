package com.redpxnda.nucleus.datapack.lua;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class LuaResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, LuaValue>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected final Globals globals;
    protected final String directory;

    protected LuaResourceReloadListener(Globals globals, String directory) {
        this.globals = globals;
        this.directory = directory;
    }

    @Override
    protected Map<ResourceLocation, LuaValue> prepare(ResourceManager rm, ProfilerFiller pf) {
        Map<ResourceLocation, LuaValue> files = new HashMap<>();

        for (Map.Entry<ResourceLocation, Resource> entry : rm.listResources(directory, path -> path.toString().endsWith(".lua")).entrySet()) {
            try (InputStream stream = entry.getValue().open()) {
                files.put(entry.getKey(), globals.load(stream, "@" + entry.getKey().toString(), "bt", globals));
            } catch (IOException e) {
                LOGGER.error("Nucleus failed to load lua resource at {}", entry.getKey());
                e.printStackTrace();
            }
        }

        return files;
    }
}
