package com.redpxnda.nucleus.datapack.references;

import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.storage.Vec3Reference;
import net.minecraft.world.event.GameEvent;

@SuppressWarnings("unused")
public class GameEventReference extends Reference<GameEvent> {
    static { Reference.register(GameEventReference.class); }

    public GameEventReference(GameEvent instance) {
        super(instance);
    }

    // Generated from GameEvent::getName
    public String getName() {
        return instance.getId();
    }

    // Generated from GameEvent::getNotificationRadius
    public int getNotificationRadius() {
        return instance.getRange();
    }

    @SuppressWarnings("unused")
    public static class Info extends Reference<GameEvent.Message> {
        static { Reference.register(Info.class); }

        public Info(GameEvent.Message instance) {
            super(instance);
        }

        // Generated from Message::context
        public Context context() {
            return new Context(instance.getEmitter());
        }

        // Generated from Message::source
        public Vec3Reference source() {
            return new Vec3Reference(instance.getEmitterPos());
        }

        // Generated from Message::gameEvent
        public GameEventReference gameEvent() {
            return new GameEventReference(instance.getEvent());
        }

    }

    @SuppressWarnings("unused")
    public static class Context extends Reference<GameEvent.Emitter> {
        static { Reference.register(Context.class); }

        public Context(GameEvent.Emitter instance) {
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
