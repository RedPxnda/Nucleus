package com.redpxnda.nucleus.config;

import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.util.Comment;
import org.jetbrains.annotations.Nullable;

public class NucleusConfig {
    public static @Nullable NucleusConfig INSTANCE;

    @AutoCodec.Name("watch_changes")
    @Comment("""
            Whether or not changes to configs should be watched and reacted to. (Configs normally auto-update when modified)
            Note: Changing this will require you to restart your game for it to be applied.
            """)
    public boolean watchChanges = true;
}
