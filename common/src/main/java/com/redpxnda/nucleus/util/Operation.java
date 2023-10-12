package com.redpxnda.nucleus.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public interface Operation {
    Map<Character, Operation> OPERATIONS = new HashMap<>();
    Codec<Operation> CODEC = Codec.STRING.comapFlatMap(str -> {
        if (str.length() != 1) return DataResult.error(() -> "Operation keys must be 1 character in length! '" + str + "' is not!");
        Operation operation = OPERATIONS.get(str.charAt(0));
        if (operation == null) return DataResult.error(() -> "Could not find operation that has a key of '" + str + "'!");
        return DataResult.success(operation);
    }, op -> String.valueOf(op.getSymbol()));

    SimpleOperation ADD = new SimpleOperation('+', Double::sum);
    SimpleOperation SUBTRACT = new SimpleOperation('-', (f, s) -> f-s);
    SimpleOperation MULTIPLY = new SimpleOperation('*', (f, s) -> f*s);
    SimpleOperation DIVIDE = new SimpleOperation('/', (f, s) -> f/s);
    SimpleOperation POWER = new SimpleOperation('^', Math::pow);

    static void init() {
        Class<?> classLoading = Operation.class;
    }

    char getSymbol();
    double apply(double first, double second);
    default float apply(float first, float second) {
        return (float) apply((double) first, second);
    }

    class SimpleOperation implements Operation {
        private final char symbol;
        private final BiFunction<Double, Double, Double> applier;

        public SimpleOperation(char symbol, BiFunction<Double, Double, Double> applier) {
            this.symbol = symbol;
            this.applier = applier;

            if (OPERATIONS.containsKey(symbol)) throw new RuntimeException("Cannot register Operation as '" + symbol + "' is already registered!");
            OPERATIONS.put(symbol, this);
        }

        @Override
        public char getSymbol() {
            return symbol;
        }

        @Override
        public double apply(double first, double second) {
            return applier.apply(first, second);
        }
    }

    Codec<SingleHolder> SINGLE_HOLDER_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CODEC.fieldOf("operation").forGetter(s -> s.operation),
            Codec.DOUBLE.fieldOf("value").forGetter(s -> s.val)
    ).apply(inst, SingleHolder::new));
    record SingleHolder(Operation operation, double val) {
        public double calculateWithSecond(double second) {
            return operation.apply(val, second);
        }

        public double calculateWithFirst(double first) {
            return operation.apply(first, val);
        }
    }

    Codec<DualHolder> DUAL_HOLDER_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CODEC.fieldOf("operation").forGetter(s -> s.operation),
            Codec.DOUBLE.fieldOf("first").forGetter(s -> s.first),
            Codec.DOUBLE.fieldOf("second").forGetter(s -> s.second)
    ).apply(inst, DualHolder::new));
    record DualHolder(Operation operation, double first, double second) {
        public double calculate() {
            return operation.apply(first, second);
        }
    }
}