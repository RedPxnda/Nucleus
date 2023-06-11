package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.datapack.lua.LuaSetupListener;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import com.redpxnda.nucleus.impl.ShaderRegistry;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.RenderUtil;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

import static com.redpxnda.nucleus.registry.NucleusRegistries.loc;

public class Nucleus {
    public static final ExpressionConfiguration EXPRESSION_CONFIG = ExpressionConfiguration.defaultConfiguration().withAdditionalFunctions(
            Map.entry("LIST_HAS", new ListContains()),
            Map.entry("SWITCH", new Switch())
    );
    public static final String MOD_ID = "nucleus";

    public static void init() {
        reloadListeners();
        NucleusRegistries.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ShaderRegistry.register(loc("rendertype_alpha_animation"), DefaultVertexFormat.BLOCK, i -> RenderUtil.alphaAnimationShader = i);
        });
    }

    public static class TestEntityCap implements EntityCapability<CompoundTag> {
        public static void init() {
            EntityDataRegistry.register(new ResourceLocation(MOD_ID, "test"), e -> e instanceof Player, TestEntityCap.class, TestEntityCap::new);
        }

        private int value = 0;
        public int getValue() {
            return value;
        }

        @Override
        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Int", value);
            return tag;
        }

        @Override
        public void loadNbt(Tag tag) {
            value = ((CompoundTag) tag).getInt("Int");
        }
    }

    private static void reloadListeners() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new LuaSetupListener());
    }
}