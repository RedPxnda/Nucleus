package com.redpxnda.nucleus.util.json;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;
import java.util.Set;

public class JsoncObject extends JsoncElement {
    private final LinkedTreeMap<String, JsoncElement> members = new LinkedTreeMap<>(false);

    public JsoncObject() {
    }

    private JsoncObject(JsoncObject other) { // use copy()
        for (Map.Entry<String, JsoncElement> entry : other.members.entrySet()) {
            add(entry.getKey(), entry.getValue().copy());
        }
    }

    public void add(String property, JsoncElement value) {
        members.put(property, value == null ? JsoncNull.INSTANCE : value);
    }

    public JsoncElement remove(String property) {
        return members.remove(property);
    }

    public Set<Map.Entry<String, JsoncElement>> entrySet() {
        return members.entrySet();
    }

    public Set<String> keySet() {
        return members.keySet();
    }

    public int size() {
        return members.size();
    }

    public boolean has(String memberName) {
        return members.containsKey(memberName);
    }

    public JsoncElement get(String memberName) {
        return members.get(memberName);
    }

    public Map<String, JsoncElement> getRaw() {
        return members;
    }

    @Override
    public JsoncObject copy() {
        return new JsoncObject(this);
    }

    @Override
    public String toString(int depth) {
        if (members.isEmpty()) return "{}";
        StringBuilder builder = new StringBuilder("{\n");
        for (Map.Entry<String, JsoncElement> entry : members.entrySet()) {
            entry.getValue().writeComments(builder, depth + 1);
            builder.append(INDENT.repeat(depth + 1));
            builder.append("\"").append(entry.getKey()).append("\": ");
            builder.append(entry.getValue().toString(depth + 1));
            builder.append(",\n");
        }
        builder.deleteCharAt(builder.length() - 2);
        builder.append(INDENT.repeat(depth)).append("}");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof JsoncObject obj
                && obj.members.equals(members));
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
