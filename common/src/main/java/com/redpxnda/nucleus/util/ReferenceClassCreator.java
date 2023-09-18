package com.redpxnda.nucleus.util;

import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReferenceClassCreator {
    public static final Map<String, Triple<Function<String, String>, Function<String, String>, Function<String, String>>> replacements = new HashMap<>(){{
        /* First is type:
            "Entity" -> "Something<Entity>"
         */
        /* Second is return replacement:
            "return new X()" -> "return new Something<X>(new X())"
         */
        /* Third is return parameter replacement:
            "return new Y(a, b, c)" -> "return new Y(a.sum, b, c)"
         */
        put("Entity", Triple.of(s -> "EntityReference<?>", s -> "new EntityReference<>(" + s, s -> s + ".instance")); // 🟡
        put("TargetingConditions", Triple.of(s -> "TargetingConditionsReference", s -> "new TargetingConditionsReference(" + s, s -> s + ".instance")); // 🟢
        put("ResourceLocation", Triple.of(s -> "ResourceLocationReference", s -> "new ResourceLocationReference(" + s, s -> s + ".instance")); // 🟢
        put("LivingEntity", Triple.of(s -> "LivingEntityReference<?>", s -> "new LivingEntityReference<>(" + s, s -> s + ".instance")); // 🟡
        put("Player", Triple.of(s -> "PlayerReference", s -> "new PlayerReference(" + s, s -> s + ".instance")); // 🟡
        put("Level", Triple.of(s -> "LevelReference", s -> "new LevelReference(" + s, s -> s + ".instance")); // 🟡
        put("Block", Triple.of(s -> "BlockReference<?>", s -> "new BlockReference<>(" + s, s -> s + ".instance")); // 🟢
        put("GameEvent", Triple.of(s -> "GameEventReference", s -> "new GameEventReference(" + s, s -> s + ".instance")); // 🟢
        put("BlockEntity", Triple.of(s -> "BlockEntityReference<?>", s -> "new BlockEntityReference<>(" + s, s -> s + ".instance")); // 🟢
        put("BlockState", Triple.of(s -> "BlockStateReference", s -> "new BlockStateReference(" + s, s -> s + ".instance")); // 🟢
        put("SlotAccess", Triple.of(s -> "SlotAccessReference", s -> "new SlotAccessReference(" + s, s -> s + ".instance")); // 🟢
        put("CompoundTag", Triple.of(s -> "CompoundTagReference", s -> "new CompoundTagReference(" + s, s -> s + ".instance")); // 🟢
        put("ItemCooldowns", Triple.of(s -> "ItemCooldownsReference", s -> "new ItemCooldownsReference(" + s, s -> s + ".instance")); // 🟢
        put("FoodProperties", Triple.of(s -> "FoodPropertiesReference", s -> "new FoodPropertiesReference(" + s, s -> s + ".instance")); // 🟢
        put("Tag", Triple.of(s -> "TagReference", s -> "new TagReference(" + s, s -> s + ".instance")); // 🟢
        put("ListTag", Triple.of(s -> "ListTagReference", s -> "new ListTagReference(" + s, s -> s + ".instance")); // 🟢
        put("MobEffectInstance", Triple.of(s -> "MobEffectInstanceReference", s -> "new MobEffectInstanceReference(" + s, s -> s + ".instance")); // 🟢
        put("Vec3", Triple.of(s -> "Vec3Reference", s -> "new Vec3Reference(" + s, s -> s + ".instance")); // 🟢
        put("Component", Triple.of(s -> "ComponentReference<?>", s -> "new ComponentReference<>(" + s, s -> s + ".instance")); // 🟢
        put("Inventory", Triple.of(s -> "InventoryReference", s -> "new InventoryReference(" + s, s -> s + ".instance")); // 🟢
        put("Item", Triple.of(s -> "ItemReference<?>", s -> "new ItemReference<>(" + s, s -> s + ".instance")); // 🟡
        put("ItemStack", Triple.of(s -> "ItemStackReference", s -> "new ItemStackReference(" + s, s -> s + ".instance")); // 🟡
        put("AABB", Triple.of(s -> "AABBReference", s -> "new AABBReference(" + s, s -> s + ".instance")); // 🟢
        put("DamageSource", Triple.of(s -> "DamageSourceReference", s -> "new DamageSourceReference(" + s, s -> s + ".instance")); // 🟢
        put("ChunkPos", Triple.of(s -> "ChunkPosReference", s -> "new ChunkPosReference(" + s, s -> s + ".instance")); // 🟢
        put("BlockPos", Triple.of(s -> "BlockPosReference", s -> "new BlockPosReference(" + s, s -> s + ".instance")); // 🟢
        put("Vec2", Triple.of(s -> "Vec2Reference", s -> "new Vec2Reference(" + s, s -> s + ".instance")); // 🟢
        put("MinecraftServer", Triple.of(s -> "MinecraftServerReference", s -> "new MinecraftServerReference(" + s, s -> s + ".instance")); // 🔴
        put("Direction", Triple.of(s -> "Directions", s -> "Directions.get(" + s, s -> s + ".instance")); // 🟢
        put("EquipmentSlot", Triple.of(s -> "EquipmentSlots", s -> "EquipmentSlots.get(" + s, s -> s + ".instance")); // 🟢
        put("Axis", Triple.of(s -> "Axes", s -> "Axes.get(" + s, s -> s + ".instance")); // 🟢
        put("InteractionHand", Triple.of(s -> "InteractionHands", s -> "InteractionHands.get(" + s, s -> s + ".instance")); // 🟢
        put("Rotation", Triple.of(s -> "Rotations", s -> "Rotations.get(" + s, s -> s + ".instance")); // 🟢
        put("Rarity", Triple.of(s -> "Rarities", s -> "Rarities.get(" + s, s -> s + ".instance")); // 🟢
        put("UseAnim", Triple.of(s -> "UseAnims", s -> "UseAnims.get(" + s, s -> s + ".instance")); // 🟢
        put("Pose", Triple.of(s -> "Poses", s -> "Poses.get(" + s, s -> s + ".instance")); // 🟢
    }};

    public static String replicateEnum(Class<? extends Enum<?>> originalClass) {
        StringBuilder replicatedClass = new StringBuilder();

        replicatedClass.append("@SuppressWarnings(\"unused\")\n");

        // Generate the class name for the replicated class
        String replicatedClassName = originalClass.getSimpleName() + (originalClass.getSimpleName().endsWith("x") || originalClass.getSimpleName().endsWith("s") ? "\b\bes" : "s");
        replicatedClass.append("public enum ").append(replicatedClassName).append(" {\n");

        // Generate registration
        //replicatedClass.append("\tstatic { Reference.register(").append(replicatedClassName).append(".class); }\n\n");

        // Generate enum constants
        Field[] fields = Arrays.stream(originalClass.getDeclaredFields()).filter(Field::isEnumConstant).toArray(Field[]::new);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            replicatedClass.append("\t").append(field.getName()).append("(").append(originalClass.getSimpleName()).append(".").append(field.getName()).append(")");
            if (i < fields.length - 1)
                replicatedClass.append(",");
            else
                replicatedClass.append(";");
            replicatedClass.append("\n");
        }

        // Generate fields
        replicatedClass.append("\n\tpublic final ").append(originalClass.getSimpleName()).append(" instance;");
        replicatedClass.append("\n\tprivate static final Map<").append(originalClass.getSimpleName()).append(", ").append(replicatedClassName).append("> map = new HashMap<>(){{\n");
        replicatedClass.append("\t\tfor (").append(replicatedClassName).append(" value : ").append(replicatedClassName).append(".values()) {\n\t\t\tput(value.instance, value);\n\t\t}}};\n\n");

        // Generate constructor
        replicatedClass.append("\t").append(replicatedClassName).append("(").append(originalClass.getSimpleName()).append(" instance) {\n\t\tthis.instance = instance;\n\t}\n\n");

        // Generate obtainment method
        replicatedClass.append("\tpublic static ").append(replicatedClassName).append(" get(").append(originalClass.getSimpleName()).append(" orig) {\n");
        replicatedClass.append("\t\treturn map.get(orig);\n\t}\n");

        replicatedClass.append("}");

        return replicatedClass.toString();
    }

    public static String replicateClass(Class<?> originalClass, boolean createOverridedMethods) {
        StringBuilder replicatedClass = new StringBuilder();

        replicatedClass.append("@SuppressWarnings(\"unused\")\n");

        // Generate the class name for the replicated class
        String replicatedClassName = originalClass.getSimpleName() + "Reference";
        replicatedClass.append("public class ").append(replicatedClassName).append(" extends Reference<").append(originalClass.getSimpleName()).append("> {\n");

        // Generate registration
        replicatedClass.append("\tstatic { Reference.register(").append(replicatedClassName).append(".class); }\n\n");

        // Generate constructor
        replicatedClass.append("\tpublic ").append(replicatedClassName).append("(").append(originalClass.getSimpleName()).append(" instance) {\n\t\tsuper(instance);\n\t}\n\n");

        // Loop through methods and replicate them
        Method[] methods = originalClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()) && (createOverridedMethods || !method.isAnnotationPresent(Override.class))) {
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                String returnTypeName = returnType.getSimpleName();

                // Generate the replicated method
                replicatedClass.append("\t// Generated from ").append(originalClass.getSimpleName()).append("::").append(methodName).append("\n");
                boolean containsReturn = replacements.containsKey(returnTypeName);
                if (containsReturn)
                    returnTypeName = replacements.get(returnTypeName).getLeft().apply(returnTypeName);
                replicatedClass.append("\tpublic ").append(returnTypeName).append(" ").append(methodName).append("(");

                // Add method parameters
                boolean[] booleans = new boolean[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (i > 0) {
                        replicatedClass.append(", ");
                    }
                    String name = parameterTypes[i].getSimpleName();
                    if (replacements.containsKey(name)) {
                        booleans[i] = true;
                        name = replacements.get(name).getLeft().apply(name);
                    }
                    replicatedClass.append(name).append(" param").append(i);
                }

                replicatedClass.append(") {\n");
                String returning = "instance." + methodName + "(";
                if (containsReturn)
                    returning = replacements.get(returnType.getSimpleName()).getMiddle().apply(returning);
                replicatedClass.append("\t\t").append(returnType != void.class ? "return " : "").append(returning);

                // Add method arguments
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (i > 0) {
                        replicatedClass.append(", ");
                    }
                    String str = "param" + i;
                    if (booleans[i])
                        str = replacements.get(parameterTypes[i].getSimpleName()).getRight().apply(str);
                    replicatedClass.append(str);
                }

                replicatedClass.append(containsReturn ? ")" : "").append(");\n");
                replicatedClass.append("\t}\n\n");
            }
        }

        replicatedClass.append("}");

        return replicatedClass.toString();
    }
    public static String replicateModifiersClass(Class<?> originalClass) {
        StringBuilder replicatedClass = new StringBuilder();

        replicatedClass.append("@SuppressWarnings(\"unused\")\n");

        // Generate the class name for the replicated class
        String replicatedClassName = originalClass.getSimpleName() + "Reference";
        replicatedClass.append("public class ").append(replicatedClassName).append(" extends Reference<").append(originalClass.getSimpleName()).append("> {\n");

        // Generate registration
        replicatedClass.append("\tstatic { Reference.register(").append(replicatedClassName).append(".class); }\n\n");

        // Generate constructor
        replicatedClass.append("\tpublic ").append(replicatedClassName).append("(").append(originalClass.getSimpleName()).append(" instance) {\n\t\tsuper(instance);\n\t}\n\n");

        // Loop through methods and replicate them
        Method[] methods = originalClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                String returnTypeName = returnType.getSimpleName();

                // Generate the replicated method
                replicatedClass.append("\t// Generated from ").append(originalClass.getSimpleName()).append("::").append(methodName).append("\n");
                boolean containsReturn = replacements.containsKey(returnTypeName);
                if (containsReturn)
                    returnTypeName = replacements.get(returnTypeName).getLeft().apply(returnTypeName);
                replicatedClass.append("\tpublic ").append(returnTypeName).append(" ").append(methodName).append("(");

                // Add method parameters
                boolean[] booleans = new boolean[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (i > 0) {
                        replicatedClass.append(", ");
                    }
                    String name = parameterTypes[i].getSimpleName();
                    if (replacements.containsKey(name)) {
                        booleans[i] = true;
                        name = replacements.get(name).getLeft().apply(name);
                    }
                    replicatedClass.append(name).append(" param").append(i);
                }

                replicatedClass.append(") {\n");
                boolean equals = returnTypeName.equals(replicatedClassName);
                if (equals)
                    replicatedClass.append("\t\tinstance.").append(methodName).append("(");
                else {
                    String returning = "instance." + methodName + "(";
                    if (containsReturn)
                        returning = replacements.get(returnType.getSimpleName()).getMiddle().apply(returning);
                    replicatedClass.append("\t\t").append(returnType != void.class ? "return " : "").append(returning);
                }

                // Add method arguments
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (i > 0) {
                        replicatedClass.append(", ");
                    }
                    String str = "param" + i;
                    if (booleans[i])
                        str = replacements.get(parameterTypes[i].getSimpleName()).getRight().apply(str);
                    replicatedClass.append(str);
                }

                replicatedClass.append(containsReturn && !equals ? ")" : "").append(");\n").append(equals ?"\t\treturn this;\n" : "");
                replicatedClass.append("\t}\n\n");
            }
        }

        replicatedClass.append("}");

        return replicatedClass.toString();
    }

    /*public static void main(String[] args) {
        String clazz = replicateClass(CraftingContainer.class, true);

        System.out.println(clazz);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(clazz);
        clipboard.setContents(selection, null);
    }*/
}
