package com.redpxnda.nucleus.datapack.lua;

import com.redpxnda.nucleus.datapack.constants.ConstantsAccess;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashMap;
import java.util.Map;

public class LuaSetupListener extends LuaResourceReloadListener {
    private static final Map<Identifier, LuaFunction> CRAFTING_HANDLERS = new HashMap<>();
    private static final Globals globals = ConstantsAccess.completeSetup(ConstantsAccess.readOnly, MiscUtil.initialize(JsePlatform.standardGlobals(), g -> {
        g.set("setup", CoerceJavaToLua.coerce(LuaSetupListener.class));
    }));

    public static void registerCraftingHandler(String loc, LuaFunction func) {
        CRAFTING_HANDLERS.put(new Identifier(loc), func);
    }
    public static LuaFunction getCraftingHandler(Identifier loc) {
        return CRAFTING_HANDLERS.get(loc);
    }
    public static LuaFunction getCraftingHandler(String loc) {
        return CRAFTING_HANDLERS.get(new Identifier(loc));
    }

    public LuaSetupListener() {
        super(globals, "lua_setup");
    }

    @Override
    protected void apply(Map<Identifier, LuaValue> object, ResourceManager resourceManager, Profiler profilerFiller) {
        object.forEach((rl, v) -> {
            v.call();
        });
    }
}
