package com.redpxnda.nucleus.util.json;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsoncArray extends JsoncElement implements Iterable<JsoncElement> {
    protected final ArrayList<JsoncElement> elements;

    public JsoncArray() {
        this.elements = new ArrayList<>();
    }

    protected JsoncArray(ArrayList<JsoncElement> elements) {
        this.elements = new ArrayList<>(elements);
    }

    public JsoncArray(int capacity) {
        this.elements = new ArrayList<>(capacity);
    }

    public void add(JsoncElement element) {
        elements.add(element == null ? JsoncNull.INSTANCE : element);
    }

    public void addAll(JsoncArray array) {
        elements.addAll(array.elements);
    }

    public JsoncElement set(int index, JsoncElement element) {
        return elements.set(index, element == null ? JsoncNull.INSTANCE : element);
    }

    public boolean remove(JsoncElement element) {
        return elements.remove(element);
    }

    public JsoncElement remove(int index) {
        return elements.remove(index);
    }

    public boolean contains(JsoncElement element) {
        return elements.contains(element);
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public JsoncElement get(int i) {
        return elements.get(i);
    }

    @Override
    public JsoncArray copy() {
        return new JsoncArray(elements);
    }

    @Override
    public String toString(int depth) {
        if (elements.isEmpty()) return "[]";
        StringBuilder builder = new StringBuilder("[\n");
        for (JsoncElement element : elements) {
            element.writeComments(builder, depth + 1);
            builder.append(INDENT.repeat(depth + 1)).append(element.toString(depth + 1)).append(",\n");
        }
        builder.deleteCharAt(builder.length() - 2);
        builder.append(INDENT.repeat(depth)).append("]");
        return builder.toString();
    }

    @NotNull
    @Override
    public Iterator<JsoncElement> iterator() {
        return elements.iterator();
    }

    public List<JsoncElement> getRaw() {
        return elements;
    }
}
