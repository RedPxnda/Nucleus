package com.redpxnda.nucleus.datapack.recipe;

import com.google.gson.JsonObject;
import com.redpxnda.nucleus.datapack.lua.LuaSetupListener;
import com.redpxnda.nucleus.datapack.references.item.CraftingContainerReference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import static org.luaj.vm2.lib.jse.CoerceJavaToLua.coerce;

public class LuaHandlerRecipe extends ShapedRecipe {
    private final ShapedRecipe compose;
    private final @Nullable ResourceLocation handler;
    private final @Nullable HandlingType type;

    public LuaHandlerRecipe(ShapedRecipe c, @Nullable ResourceLocation handler, @Nullable HandlingType type) {
        super(c.getId(), c.getGroup(), c.category(), c.getWidth(), c.getHeight(), c.getIngredients(), c.getResultItem(null));
        this.compose = c;
        this.handler = handler;
        this.type = type;
    }

    /*@Override
    public boolean isSpecial() {
        return false;
    }*/

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(inv, registryAccess);
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
        public LuaHandlerRecipe fromJson(ResourceLocation rl, JsonObject jsonObject) {
            HandlingType type = HandlingType.MODIFY;
            if (jsonObject.has("action"))
                type = HandlingType.valueOf(GsonHelper.getAsString(jsonObject, "action").toUpperCase());
            return new LuaHandlerRecipe(super.fromJson(rl, jsonObject), new ResourceLocation(GsonHelper.getAsString(jsonObject, "lua_handler")), type);
        }

        @Override
        public LuaHandlerRecipe fromNetwork(ResourceLocation rl, FriendlyByteBuf buf) {
            return new LuaHandlerRecipe(super.fromNetwork(rl, buf), null, null);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ShapedRecipe recipe) {
            super.toNetwork(buf, recipe);
            if (recipe instanceof LuaHandlerRecipe lua) {
                buf.writeUtf(lua.handler.toString());
                buf.writeUtf(lua.type.name());
            } else {
                buf.writeUtf("");
                buf.writeUtf("MODIFY");
            }
        }
    }

    public enum HandlingType {
        MODIFY, // modify expects no return value. Consuming operation.
        DEFINE, // define expects an ItemStack returned, setting the recipe output to it.
        REQUIRE // require expects a boolean returned, and only produces an output if this boolean is true.
    }
}
