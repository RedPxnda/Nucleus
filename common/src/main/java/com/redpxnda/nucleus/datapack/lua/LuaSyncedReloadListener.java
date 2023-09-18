package com.redpxnda.nucleus.datapack.lua;

import com.mojang.datafixers.util.Pair;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.network.clientbound.SyncLuaFilePacket;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public abstract class LuaSyncedReloadListener extends SinglePreparationResourceReloader<Map<Identifier, byte[]>> {
    public static final Map<String, Consumer<Map<Identifier, LuaValue>>> clientHandlers = new HashMap<>();
    public static final Map<String, Globals> clientGlobals = new HashMap<>();

    protected final Globals serverGlobals;
    protected final String directory;
    protected final String syncId;

    protected LuaSyncedReloadListener(Globals globals, String directory, String syncId) {
        this.serverGlobals = globals;
        this.directory = directory;
        this.syncId = syncId;

        clientHandlers.put(syncId, this::handleForClient);
        clientGlobals.put(syncId, new Globals());
    }

    @Override
    protected Map<Identifier, byte[]> prepare(ResourceManager rm, Profiler pf) {
        Map<Identifier, byte[]> rawData = new HashMap<>();

        for (Map.Entry<Identifier, Resource> entry : rm.findResources(directory, path -> path.toString().endsWith(".lua")).entrySet()) {
            try (InputStream stream = entry.getValue().getInputStream()) {
                rawData.put(entry.getKey(), stream.readAllBytes());
            } catch (IOException e) {
                LOGGER.error("Failed to load lua resource at {}", entry.getKey());
                e.printStackTrace();
            }
        }

        return rawData;
    }


    @Override
    protected void apply(Map<Identifier, byte[]> object, ResourceManager resourceManager, Profiler profilerFiller) {
        Map<Identifier, LuaValue> values = object.entrySet().stream().map(entry -> {
            try {
                return Pair.of(entry.getKey(), serverGlobals.load(new String(entry.getValue(), StandardCharsets.UTF_8)));
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Failed to load LuaScript at '{}' from sync id '{}' on the server! -> {}", entry.getKey(), syncId, e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        handleForServer(values);

        new SyncLuaFilePacket(object, syncId).send(Nucleus.SERVER);
    }

    protected abstract void handleForClient(Map<Identifier, LuaValue> values);
    protected abstract void handleForServer(Map<Identifier, LuaValue> values);
}
