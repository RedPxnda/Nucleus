package com.redpxnda.nucleus.datapack.json.listeners;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.json.passive.ContextHolder;
import com.redpxnda.nucleus.datapack.json.passive.RootContext;
import com.redpxnda.nucleus.datapack.json.types.Evaluable;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import java.util.HashMap;
import net.minecraft.entity.player.PlayerEntity;

public class ExampleListenerHandler implements ContextHoldingHandler {
    private static final Codec<ExampleListenerHandler> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Evaluable.Codecs.ALL.fieldOf("test").forGetter(e -> e.evaluable)
    ).apply(instance, ExampleListenerHandler::new));
    public static final ContextBasedListener<ExampleListenerHandler> LISTENER = new ContextBasedListener<>("test", CODEC);

    public static void callAll(PlayerEntity player) {
        LISTENER.deserialized.forEach(handler -> handler.call(player));
    }

    private ContextHolder context;
    private final Evaluable evaluable;

    public ExampleListenerHandler(Evaluable evaluable) {
        this.evaluable = evaluable;
    }

    public void putContext(ContextHolder holder) {
        this.context = holder;
    }

    public void call(PlayerEntity player) {
        System.out.println("Being called!");
        EvaluationValue val = evaluable.evaluate(context.resolve(new RootContext(new HashMap<>(){{
            put("player", new PlayerReference(player));
        }})));
        System.out.println("attempting to print.");
        System.out.println(val.getStringValue());
    }
}
