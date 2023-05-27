package com.redpxnda.nucleus.datapack.json.passive;

import com.redpxnda.nucleus.datapack.references.Reference;

import java.util.HashMap;
import java.util.Map;

public class RootContext {
    private final Map<String, ? extends Reference<?>> refs;
    private final Map<String, Object> resolved = new HashMap<>();

    public RootContext(Map<String, ? extends Reference<?>> refs) {
        this.refs = refs;
    }

    public void insert(String str, Object obj) {
        resolved.put(str, obj);
    }
    public Map<String, ? extends Reference<?>> references() {
        return refs;
    }
    public Map<String, Object> resolved() {
        return resolved;
    }
}
