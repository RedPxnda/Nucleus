package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.datapack.references.GameEventReference;
import com.redpxnda.nucleus.datapack.references.LevelReference;
import com.redpxnda.nucleus.datapack.references.Statics;
import com.redpxnda.nucleus.datapack.references.block.BlockEntityReference;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.block.BlockReference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.effect.MobEffectInstanceReference;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.entity.LivingEntityReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.item.*;
import com.redpxnda.nucleus.datapack.references.storage.*;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import com.redpxnda.nucleus.datapack.references.tag.ListTagReference;
import com.redpxnda.nucleus.datapack.references.tag.TagReference;
import org.luaj.vm2.LuaFunction;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class LuaReferenceDocumentor {
    /*public static void main(String[] args) {
        try {
            Class<?>[] classes = new Class[]{
                    EntityReference.class,
                    TargetingConditionsReference.class,
                    ResourceLocationReference.class,
                    LivingEntityReference.class,
                    PlayerReference.class,
                    LevelReference.class,
                    BlockReference.class,
                    GameEventReference.class,
                    GameEventReference.Context.class,
                    GameEventReference.Info.class,
                    BlockEntityReference.class,
                    BlockStateReference.class,
                    SlotAccessReference.class,
                    CompoundTagReference.class,
                    ItemCooldownsReference.class,
                    FoodPropertiesReference.class,
                    TagReference.class,
                    ListTagReference.class,
                    MobEffectInstanceReference.class,
                    Vec3Reference.class,
                    Vec2Reference.class,
                    ComponentReference.class,
                    ComponentReference.Mutable.class,
                    InventoryReference.class,
                    ItemReference.class,
                    ItemStackReference.class,
                    AABBReference.class,
                    DamageSourceReference.class,
                    ChunkPosReference.class,
                    BlockPosReference.class,
                    Statics.Directions.class,
                    Statics.EquipmentSlots.class,
                    Statics.Axes.class,
                    Statics.InteractionHands.class,
                    Statics.Rotations.class,
                    Statics.Rarities.class,
                    Statics.UseAnims.class,
                    Statics.Poses.class,
                    Statics.ChatFormattings.class,
                    CraftingContainerReference.class
            };
            for (Class<?> cls : classes) {
                FileWriter writer = new FileWriter("z_lua_docs/" + cls.getSimpleName() + ".lua");
                writer.write(generate(cls));
                writer.close();
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }*/
    public static void main(String[] args) {
        try {
            Class<?> cls = Statics.class;
            FileWriter writer = new FileWriter("z_lua_docs/" + cls.getSimpleName() + ".lua");
            writer.write(generateStatic(cls));
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String generateStatic(Class<?> clazz) {
        StringBuilder builder = new StringBuilder();
        builder.append("---@class ").append(clazz.getSimpleName()).append("\n").append(clazz.getSimpleName()).append(" = {}\n");
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers())) continue;
            builder.append("\n");

            StringBuilder params = new StringBuilder();
            for (Parameter param : method.getParameters()) {
                String paramType;
                Class<?> typeClass = param.getType();
                if (typeClass.isArray()) typeClass = typeClass.getComponentType();
                if (boolean.class.isAssignableFrom(typeClass))
                    paramType = "boolean";
                else if (typeClass.isPrimitive())
                    paramType = "number";
                else if (String.class.isAssignableFrom(typeClass))
                    paramType = "string";
                else if (LuaFunction.class.isAssignableFrom(typeClass))
                    paramType = "function";
                else
                    paramType = typeClass.getSimpleName().replaceAll("Reference", "");

                if (param.getType().isArray()) paramType+="[]";

                builder.append("---@param ").append(param.getName()).append(" ").append(paramType).append("\n");
                params.append(param.getName()).append(", ");
            }
            String paramsDecl = params.toString();
            if (paramsDecl.endsWith(", ")) paramsDecl = paramsDecl.substring(0, paramsDecl.length()-2);

            Class<?> retClass = method.getReturnType();
            if (retClass.isArray()) retClass = retClass.getComponentType();
            String returnType;
            if (boolean.class.isAssignableFrom(retClass))
                returnType = "boolean";
            else if (void.class.isAssignableFrom(retClass))
                returnType = "void";
            else if (retClass.isPrimitive())
                returnType = "number";
            else if (String.class.isAssignableFrom(retClass))
                returnType = "string";
            else if (LuaFunction.class.isAssignableFrom(retClass))
                returnType = "function";
            else
                returnType = retClass.getSimpleName().replaceAll("Reference", "");
            if (method.getReturnType().isArray()) returnType+="[]";
            builder.append("---@return ").append(returnType).append("\n");

            builder.append("function ").append(clazz.getSimpleName()).append(":").append(method.getName()).append("(").append(paramsDecl).append(") return nil end\n");
        }

        return builder.toString();
    }
    public static String generate(Class<?> clazz) {
        StringBuilder builder = new StringBuilder();
        String luaClassName = clazz.getSimpleName().replaceAll("Reference", "");
        String instanceName = luaClassName.toLowerCase();
        Class<?> sup = clazz.getSuperclass();
        String superClass = sup == null ? "" : sup.getSimpleName().equals("Object") ? "" : " : " + sup.getSimpleName().replaceAll("Reference", "");
        builder.append("---@class ").append(luaClassName).append(superClass).append("\n").append("local ").append(instanceName).append(" = {}\n");

        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) continue;
            builder.append("\n");

            StringBuilder params = new StringBuilder();
            for (Parameter param : method.getParameters()) {
                String paramType;
                Class<?> typeClass = param.getType();
                if (typeClass.isArray()) typeClass = typeClass.getComponentType();
                if (boolean.class.isAssignableFrom(typeClass))
                    paramType = "boolean";
                else if (typeClass.isPrimitive())
                    paramType = "number";
                else if (String.class.isAssignableFrom(typeClass))
                    paramType = "string";
                else if (LuaFunction.class.isAssignableFrom(typeClass))
                    paramType = "function";
                else
                    paramType = typeClass.getSimpleName().replaceAll("Reference", "");

                if (param.getType().isArray()) paramType+="[]";

                builder.append("---@param ").append(param.getName()).append(" ").append(paramType).append("\n");
                params.append(param.getName()).append(", ");
            }
            String paramsDecl = params.toString();
            if (paramsDecl.endsWith(", ")) paramsDecl = paramsDecl.substring(0, paramsDecl.length()-2);

            Class<?> retClass = method.getReturnType();
            if (retClass.isArray()) retClass = retClass.getComponentType();
            String returnType;
            if (boolean.class.isAssignableFrom(retClass))
                returnType = "boolean";
            else if (void.class.isAssignableFrom(retClass))
                returnType = "void";
            else if (retClass.isPrimitive())
                returnType = "number";
            else if (String.class.isAssignableFrom(retClass))
                returnType = "string";
            else if (LuaFunction.class.isAssignableFrom(retClass))
                returnType = "function";
            else
                returnType = retClass.getSimpleName().replaceAll("Reference", "");
            if (method.getReturnType().isArray()) returnType+="[]";
            builder.append("---@return ").append(returnType).append("\n");

            builder.append("function ").append(instanceName).append(":").append(method.getName()).append("(").append(paramsDecl).append(") return nil end\n");
        }

        return builder.toString();
    }
}
