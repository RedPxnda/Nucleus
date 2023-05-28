package com.redpxnda.nucleus.datapack.references;

import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.storage.Vec3Reference;
import net.minecraft.world.level.gameevent.GameEvent;

@SuppressWarnings("unused")
public class GameEventReference extends Reference<GameEvent> {
    static { Reference.register(GameEventReference.class); }

    public GameEventReference(GameEvent instance) {
        super(instance);
    }

    // Generated from GameEvent::getName
    public String getName() {
        return instance.getName();
    }

    // Generated from GameEvent::getNotificationRadius
    public int getNotificationRadius() {
        return instance.getNotificationRadius();
    }

    @SuppressWarnings("unused")
    public static class Message extends Reference<GameEvent.Message> {
        static { Reference.register(Message.class); }

        public Message(GameEvent.Message instance) {
            super(instance);
        }

        // Generated from Message::context
        public Context context() {
            return new Context(instance.context());
        }

        // Generated from Message::source
        public Vec3Reference source() {
            return new Vec3Reference(instance.source());
        }

        // Generated from Message::gameEvent
        public GameEventReference gameEvent() {
            return new GameEventReference(instance.gameEvent());
        }

    }

    @SuppressWarnings("unused")
    public static class Context extends Reference<GameEvent.Context> {
        static { Reference.register(Context.class); }

        public Context(GameEvent.Context instance) {
            super(instance);
        }

        // Generated from Context::affectedState
        public BlockStateReference affectedState() {
            return new BlockStateReference(instance.affectedState());
        }

        // Generated from Context::sourceEntity
        public EntityReference<?> sourceEntity() {
            return new EntityReference<>(instance.sourceEntity());
        }

    }
}
