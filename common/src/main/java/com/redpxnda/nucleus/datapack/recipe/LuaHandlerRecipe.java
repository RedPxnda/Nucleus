package com.redpxnda.nucleus.datapack.recipe;

import com.google.gson.JsonObject;
import com.redpxnda.nucleus.datapack.lua.LuaSetupListener;
import com.redpxnda.nucleus.datapack.references.item.CraftingContainerReference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import static org.luaj.vm2.lib.jse.CoerceJavaToLua.coerce;

public class LuaHandlerRecipe extends ShapedRecipe {
    private final ShapedRecipe compose;
    private final @Nullable Identifier handler;
    private final @Nullable HandlingType type;

    public LuaHandlerRecipe(ShapedRecipe c, @Nullable Identifier handler, @Nullable HandlingType type) {
        super(c.getId(), c.getGroup(), c.getCategory(), c.getWidth(), c.getHeight(), c.getIngredients(), c.getOutput(null));
        this.compose = c;
        this.handler = handler;
        this.type = type;
    }

    /*@Override
    public boolean isSpecial() {
        return false;
    }*/

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryAccess) {
        ItemStack result = super.craft(inv, registryAccess);
        if (handler == null || type == null) return result;
        LuaValue value = LuaSetupListener.getCraftingHandler(handler).call(coerce(new ItemStackReference(result)), coerce(new CraftingContainerReference(inv)));
        switch (type) {
            case DEFINE -> { result = (ItemStack) CoerceLuaToJava.coerce(value, ItemStack.class); }
            case REQUIRE -> { result = value.toboolean() ? result : ItemStack.EMPTY; }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NucleusRegistries.luaHandlingRecipe.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {
        @Override
        public LuaHandlerRecipe read(Identifier rl, JsonObject jsonObject) {
            HandlingType type = HandlingType.MODIFY;
            if (jsonObject.has("action"))
                type = HandlingType.valueOf(JsonHelper.getString(jsonObject, "action").toUpperCase());
            return new LuaHandlerRecipe(super.read(rl, jsonObject), new Identifier(JsonHelper.getString(jsonObject, "lua_handler")), type);
        }

        @Override
        public LuaHandlerRecipe read(Identifier rl, PacketByteBuf buf) {
            return new LuaHandlerRecipe(super.read(rl, buf), null, null);
        }

        @Override
        public void write(PacketByteBuf buf, ShapedRecipe recipe) {
            super.write(buf, recipe);
            if (recipe instanceof LuaHandlerRecipe lua) {
                buf.writeString(lua.handler.toString());
                buf.writeString(lua.type.name());
            } else {
                buf.writeString("");
                buf.writeString("MODIFY");
            }
        }
    }

    public enum HandlingType {
        MODIFY, // modify expects no return value. Consuming operation.
        DEFINE, // define expects an ItemStack returned, setting the recipe output to it.
        REQUIRE // require expects a boolean returned, and only produces an output if this boolean is true.
    }
}
