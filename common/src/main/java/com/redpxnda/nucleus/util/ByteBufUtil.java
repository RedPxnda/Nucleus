package com.redpxnda.nucleus.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public class ByteBufUtil {
    public static <T> void writeWithCodec(FriendlyByteBuf buf, T instance, Codec<T> codec) {
        Tag tag = codec.encodeStart(NbtOps.INSTANCE, instance).getOrThrow(false, s ->
                LOGGER.error("Failed to encode {} into a FriendlyByteBuf using codec -> {}", instance, s)
        );
        writeTag(tag, buf);
    }
    public static <T> T readWithCodec(FriendlyByteBuf buf, Codec<T> codec) {
        Tag tag = readTag(buf);
        return codec.parse(NbtOps.INSTANCE, tag).getOrThrow(false, s ->
                LOGGER.error("Failed to decode tag '{}' from FriendlyByteBuf using codec -> {}", tag, s)
        );
    }

    public static void writeTag(Tag tag, FriendlyByteBuf buf) {
        try {
            NbtIo.writeUnnamedTag(tag, new ByteBufOutputStream(buf));
        } catch (IOException e) {
            LOGGER.error("Failed to write Nbt tag '{}' to byte buffer!", tag);
            throw new RuntimeException(e);
        }
    }
    public static Tag readTag(FriendlyByteBuf buf) {
        try {
            return NbtIo.readUnnamedTag(new ByteBufInputStream(buf), 0, NbtAccounter.UNLIMITED);
        } catch (IOException e) {
            LOGGER.error("Failed to read Nbt from byte buffer!");
            throw new RuntimeException(e);
        }
    }
}
