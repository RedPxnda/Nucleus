package com.redpxnda.nucleus.datapack.json.passive;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.datapack.json.annotation.ContextSettings;
import com.redpxnda.nucleus.datapack.references.Reference;

public class Operation<C extends Reference<?>, A> {
    private Operation<?,?> parent = null;
    private Class<C> reference = null;
    private ContextSettings<C, A> context = null;
    private final String methodName;
    private A args;

    public Operation(Operation<?,?> parent, String name, JsonObject object) {
        this.parent = parent;
        if (parent.context.getRefClass() == null)
            throw new IllegalArgumentException("Parent of Operation doesn't return a valid Reference.");
        this.reference = (Class<C>) parent.context.getRefClass();
        this.methodName = name;
        this.context = (ContextSettings<C, A>) Reference.getContextSettings(reference, name);

        this.withParams(object);
    }
    public Operation(String name) {
        this.methodName = name;
    }

    public Operation<C, A> withParams(JsonObject object) {
        this.args = context.getCodec() == null ? null :
                context.getCodec().parse(JsonOps.INSTANCE, object).get().left().orElseThrow(() ->
                        new IllegalArgumentException("Failed to parse Operation's parameters with json object of " + object)
                );
        return this;
    }

    public Object call(RootContext root) throws EvaluationException, ParseException {
        if (parent == null || reference == null || context == null)
            return root.references().get(methodName);

        Object obj = parent.call(root);
        if (!obj.getClass().equals(reference))
            throw new IllegalArgumentException("Parent of Operation failed to return a valid Reference.");
        return context.getFunc().apply(((C) obj), args);
    }

    public Class<C> getReference() {
        return reference;
    }

    public ContextSettings<C, A> getContext() {
        return context;
    }
}
