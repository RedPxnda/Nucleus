package com.redpxnda.nucleus.datapack.lua;

import com.redpxnda.nucleus.datapack.constants.ConstantsAccess;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;

public class ExampleLuaListener extends LuaResourceReloadListener {
    public static final Map<Identifier, LuaFunction> handlers = new HashMap<>();

    public ExampleLuaListener() {
        super(ConstantsAccess.completeSetup(ConstantsAccess.readOnly), "test");;
    }

    @Override
    protected void apply(Map<Identifier, LuaValue> object, ResourceManager resourceManager, Profiler profilerFiller) {
        object.forEach((k, v) -> {
            v.call();
            handlers.put(k, (LuaFunction) globals.get(k.getPath().substring(directory.length()+1, k.getPath().length()-4).replaceAll("/", "_") + "_coolFunc"));
        });
    }
}
