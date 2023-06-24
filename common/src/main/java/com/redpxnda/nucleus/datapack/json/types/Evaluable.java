package com.redpxnda.nucleus.datapack.json.types;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.redpxnda.nucleus.Nucleus.EXPRESSION_CONFIG;
import static com.redpxnda.nucleus.Nucleus.LOGGER;

public class Evaluable extends AbstractEvaluable {
    public static class Codecs {
        public static final Codec<Evaluable> VALUE_ONLY = Codec.either(Codec.DOUBLE, Codec.either(Codec.INT, Codec.BOOL)).xmap(either -> {
            if (either.left().isPresent())
                return new Evaluable(String.valueOf(either.left().get()));
            else {
                if (either.right().get().left().isPresent())
                    return new Evaluable(String.valueOf(either.right().get().left().get()));
                else
                    return new Evaluable(String.valueOf(either.right().get().right().get()));
            }
        }, evaluable -> Either.left(Double.parseDouble(evaluable.getRaw())));
        public static final Codec<Evaluable> STRING_ONLY = Codec.either(Codec.STRING, Codec.STRING.listOf()).xmap(either -> {
            if (either.left().isPresent())
                return new Evaluable(either.left().get());
            else {
                List<String> strings = either.right().get();
                StringBuilder builder = new StringBuilder();
                for (String str : strings) {
                    str = str.replaceAll("^\\s+", "");
                    builder.append(str);
                }
                String string = builder.toString();
                return new Evaluable(string);
            }
        }, e -> Either.left(e.raw));
        public static final Codec<Evaluable> STRING_OR_VALUE = Codec.either(STRING_ONLY, VALUE_ONLY).xmap(either -> either.left().isPresent() ? either.left().get() : either.right().get(), Either::left);
        public static final Codec<Evaluable> ROOTABLE = Codec.STRING.xmap(s -> {
            if (!s.contains("=")) return new Evaluable(s);
            String[] sections = s.split("=");
            String newStr = "(" + sections[0] + ") - (" + sections[1] + ")";
            return new Evaluable(newStr);
        }, e -> e.raw);
        public static final Codec<Evaluable> CONDITIONAL_ONLY = Codec.either(BranchCondition.codec(STRING_OR_VALUE), ChainedCondition.codec(STRING_OR_VALUE)).flatComapMap(either -> {
            if (either.left().isPresent()) {
                BranchCondition<Evaluable> c = either.left().get();
                return new Evaluable("SWITCH(" + c.condition.raw + "," + c.primary.raw + "," + c.secondary.raw + ")");
            } else {
                ChainedCondition<Evaluable> c = either.right().get();
                int index = 0;
                StringBuilder str = new StringBuilder();
                for (Condition<Evaluable> condition : c.chain) {
                    if (index != c.chain.size())
                        str.append("SWITCH(").append(condition.condition.raw).append(",").append(condition.result.raw).append(",");
                    else
                        str.append(condition.result).append(")");
                    index++;
                }
                return new Evaluable(str.toString());
            }
        }, e -> DataResult.error("Evaluable cannot be turned into a Conditional."));
        public static final Codec<Evaluable> ALL = Codec.either(STRING_ONLY, Codec.either(VALUE_ONLY, CONDITIONAL_ONLY)).xmap(either -> {
                    return either.left().isPresent() ?
                            either.left().get() :
                            either.right().get().left().isPresent() ?
                                    either.right().get().left().get() : either.right().get().right().get();
                }, Either::left);
    }

    private final Expression expression;

    public Evaluable(String str) {
        super(str);
        this.expression = new Expression(str, EXPRESSION_CONFIG);
    }

    public Evaluable with(String key, Object value) {
        expression.with(key, value);
        return this;
    }
    public Evaluable with(Vector3d vec) {
        expression.with("x", vec.x).with("y", vec.y).with("z", vec.z);
        return this;
    }

    @Override
    public EvaluationValue evaluate() {
        try {
            return expression.evaluate();
        } catch (EvaluationException | ParseException e) {
            LOGGER.error("Failed to evaluate evaluable '{}'!", raw);
            throw new RuntimeException(e);
        }
    }

    @Override
    public EvaluationValue evaluate(Map<String, Object> values) {
        //values = BRR.redirect(values);
        expression.withValues(values);
        return evaluate();
    }

    public EvaluationValue evaluate(String key, Object value) {
        expression.with(key, value);
        return evaluate();
    }

    public double doubleValue() {
        return evaluate().getNumberValue().doubleValue();
    }
    public double doubleValue(String key, Object value) {
        expression.with(key, value);
        return evaluate().getNumberValue().doubleValue();
    }

    @SafeVarargs
    @Override
    public final EvaluationValue evaluate(Map<String, Object>... values) {
        Map<String, Object> map = new HashMap<>(){{
            for (Map<String, Object> obj : values) {
                putAll(obj);
            }
        }};
        //map = BRR.redirect(map);
        expression.withValues(map);
        return evaluate();
    }

    // BRR -> Basic Representable Registry
    public static class BRR {
        private static final Map<Class<?>, Function<?, ?>> classRedirections = new HashMap<>();
        public static Map<Class<?>, Function<?, ?>> getClassRedirections() {
            return classRedirections;
        }
        public static Map<String, Object> redirect(Map<String, Object> map) {
            Map<String, Object> newMap = map.entrySet().stream()
                    .map(entry -> {
                        Object value = entry.getValue();
                        if (value instanceof BasicRepresentable rep)
                            value = rep.getRepresentation();
                        else if (classRedirections.containsKey(value.getClass())) {
                            Function<Object, ?> redirector = (Function<Object, ?>) classRedirections.get(value.getClass());
                            value = redirector.apply(value);
                        }
                        return Pair.of(entry.getKey(), value);
                    })
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

            return newMap;
        }

        public static <T> void add(Class<T> clazz, Function<T, ?> redirector) {
            classRedirections.put(clazz, redirector);
        }
    }
}
