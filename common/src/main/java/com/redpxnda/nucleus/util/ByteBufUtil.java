package com.redpxnda.nucleus.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public class ByteBufUtil {
    public static <T> void writeWithCodec(PacketByteBuf buf, T instance, Codec<T> codec) {
        NbtElement tag = codec.encodeStart(NbtOps.INSTANCE, instance).getOrThrow(false, s ->
                LOGGER.error("Failed to encode {} into a FriendlyByteBuf using codec -> {}", instance, s)
        );
        writeTag(tag, buf);
    }
    public static <T> T readWithCodec(PacketByteBuf buf, Codec<T> codec) {
        NbtElement tag = readTag(buf);
        return codec.parse(NbtOps.INSTANCE, tag).getOrThrow(false, s ->
                LOGGER.error("Failed to decode tag '{}' from FriendlyByteBuf using codec -> {}", tag, s)
        );
    }

    public static void writeTag(NbtElement tag, PacketByteBuf buf) {
        try {
            NbtIo.write(tag, new ByteBufOutputStream(buf));
        } catch (IOException e) {
            LOGGER.error("Failed to write Nbt tag '{}' to byte buffer!", tag);
            throw new RuntimeException(e);
        }
    }
    public static NbtElement readTag(PacketByteBuf buf) {
        try {
            return NbtIo.read(new ByteBufInputStream(buf), 0, NbtTagSizeTracker.EMPTY);
        } catch (IOException e) {
            LOGGER.error("Failed to read Nbt from byte buffer!");
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Long> readLongMap(Supplier<Map<String, Long>> creator, PacketByteBuf buf) {
        Map<String, Long> map = creator.get();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(buf.readString(), buf.readLong());
        }
        return map;
    }
    public static void writeLongMap(Map<String, Long> map, PacketByteBuf buf) {
        buf.writeInt(map.size());
        map.forEach((key, value) -> {
            assert value != null : "No null values allowed for long map FriendlyByteBuf writing.";
            buf.writeString(key);
            buf.writeLong(value);
        });
    }
}
