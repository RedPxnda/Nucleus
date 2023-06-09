package com.redpxnda.nucleus.datapack.lua;

import com.redpxnda.nucleus.datapack.constants.ConstantsAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.*;

public class ExampleLuaListener extends LuaResourceReloadListener {
    public static final Map<ResourceLocation, LuaFunction> handlers = new HashMap<>();

    public ExampleLuaListener() {
        super(ConstantsAccess.completeSetup(ConstantsAccess.readOnly), "test");;
    }

    @Override
    protected void apply(Map<ResourceLocation, LuaValue> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        object.forEach((k, v) -> {
            v.call();
            handlers.put(k, (LuaFunction) globals.get(k.getPath().substring(directory.length()+1, k.getPath().length()-4).replaceAll("/", "_") + "_coolFunc"));
        });
    }
}
