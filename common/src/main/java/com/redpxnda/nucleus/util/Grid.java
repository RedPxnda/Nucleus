package com.redpxnda.nucleus.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Grid<T> {
    private final int rows;
    private final int columns;
    private final List<List<T>> elements;
    private final T defaultValue;

    public Grid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.elements = new ArrayList<>();
        this.defaultValue = null;
        for (int i = 0; i < rows; i++) {
            elements.add(new ArrayList<>());
        }
    }
    public Grid(int rows, int columns, T defaultValue) {
        this.rows = rows;
        this.columns = columns;
        this.elements = new ArrayList<>();
        this.defaultValue = defaultValue;
        for (int i = 0; i < rows; i++) {
            elements.add(new ArrayList<>());
            for (int j = 0; j < columns; j++) {
                elements.get(i).add(defaultValue);
            }
        }
    }

    public T get(int row, int column) {
        validateCoords(row, column);
        return elements.get(row).get(column);
    }

    public void put(T value, int row, int column) {
        validateCoords(row, column);
        elements.get(row).set(column, value);
    }

    public void remove(int row, int column) {
        validateCoords(row, column);
        elements.get(row).set(column, defaultValue);
    }

    public T find(Predicate<T> predicate) {
        for (List<T> row : elements) {
            for (T t : row) {
                if (predicate.test(t))
                    return t;
            }
        }
        return null;
    }

    private void validateCoords(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= columns)
            throw new IndexOutOfBoundsException("Coordinates passed into Grid are either negative or outside grid bounds.");
    }
}
