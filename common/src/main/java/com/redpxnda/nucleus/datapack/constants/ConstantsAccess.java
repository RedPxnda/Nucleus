package com.redpxnda.nucleus.datapack.constants;

import net.minecraft.resources.ResourceLocation;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Interface used to control the amount of access to {@link Constants} that {@link Globals}(lua scripts) get.
 */
public interface ConstantsAccess {
    default void init(Globals globals) {}

    static Globals globalsWithConstants(ConstantsAccess access) {
        Globals globals = JsePlatform.standardGlobals();
        access.init(globals);
        globals.set("Constants", CoerceJavaToLua.coerce(access));
        return globals;
    }
    static Globals globalsWithConstants(Globals globals, ConstantsAccess access) {
        access.init(globals);
        globals.set("Constants", CoerceJavaToLua.coerce(access));
        return globals;
    }
    static Globals globalsWithConstants(Globals globals, ConstantsAccess access, String name) {
        access.init(globals);
        globals.set(name, CoerceJavaToLua.coerce(access));
        return globals;
    }

    ConstantsAccess readOnly = new ConstantsAccess() {
        public void init(Globals globals) {
            globals.set("Constant", new TwoArgFunction() {
                @Override
                public LuaValue call(LuaValue arg, LuaValue arg2) {
                    return CoerceJavaToLua.coerce(Constants.get(arg.toString(), arg2.toString()));
                }
            });
        }
        public Object get(String loc) {
            return Constants.get(new ResourceLocation(loc));
        }
    };
    ConstantsAccess writeOnly = new ConstantsAccess() {
        public void set(String loc, Object obj) {
            Constants.set(new ResourceLocation(loc), obj);
        }
    };
    ConstantsAccess readAndWrite = new ConstantsAccess() {
        public void init(Globals globals) {
            globals.set("Constant", new TwoArgFunction() {
                @Override
                public LuaValue call(LuaValue arg, LuaValue arg2) {
                    return CoerceJavaToLua.coerce(Constants.get(arg.toString(), arg2.toString()));
                }
            });
        }
        public Object get(String loc) {
            return Constants.get(new ResourceLocation(loc));
        }
        public void set(String loc, Object obj) {
            Constants.set(new ResourceLocation(loc), obj);
        }
    };
    static ConstantsAccess modSpecificReadOnly(String modId) {
        return new ConstantsAccess() {
            public void init(Globals globals) {
                globals.set("Constant", new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg) {
                        return CoerceJavaToLua.coerce(Constants.get(modId, arg.toString()));
                    }
                });
            }
            public Object get(String key) {
                return Constants.get(modId, key);
            }
        };
    }
}
