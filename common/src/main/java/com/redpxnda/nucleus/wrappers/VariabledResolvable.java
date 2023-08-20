package com.redpxnda.nucleus.wrappers;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public abstract class VariabledResolvable<T> extends Resolvable<T> {
    /*public static void main(String[] args) {
        Wrappers.init();

        DoubleExpression ex = forDouble("$[testVar.a] * $[testVar.b]");
        ex.providePermanent("testVar", Map.of("a", 5, "b", 7));
        System.out.println(ex.getValue());
        System.out.println("======");
        System.out.println(ex.base);
        System.out.println(ex.resolved);
        System.out.println(ex.getValue());
    }

    static final Logger LOGGER = LogUtils.getLogger();*/

    public static DoubleExpression forDouble(String expression) {
        return new DoubleExpression(expression);
    }
    public static FloatExpression forFloat(String expression) {
        return new FloatExpression(expression);
    }
    public static IntegerExpression forInteger(String expression) {
        return new IntegerExpression(expression);
    }
    public static BooleanExpression forBoolean(String expression) {
        return new BooleanExpression(expression);
    }
    public static StringResolvable forString(String expression) {
        return new StringResolvable(expression);
    }

    protected String resolved;
    protected String regex = "\\$\\[(.*?)\\]";
    protected int group = 1;

    public VariabledResolvable(String base) {
        super(base);
        this.base = base;
        this.resolved = base;
    }
    public VariabledResolvable(String base, String regex, int varGroup) {
        this(base);
        this.regex = regex;
        this.group = varGroup;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    protected <A> String reshapeWith(String replacement, String name, Wrapper<A> wrapper, A instance) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(base);

        while (matcher.find()) {
            String wholeMatch = matcher.group();
            String match = matcher.group(group);
            String[] parts = match.split("\\.");

            if (!parts[0].equals(name)) continue;

            Object currentObj = instance;
            Wrapper<Object> currentWrapper = (Wrapper<Object>) wrapper;
            int loops = -1;
            for (String part : parts) {
                loops++;
                if (loops == 0) continue;

                if (currentWrapper == null)
                    throw new RuntimeException("Cannot get properties (specifically '" + part + "') from object '" + currentObj + "' as it has no specified wrapper!\n" +
                            "Variable attempted to use: " + match);
                currentObj = currentWrapper.get(currentObj, part);
                currentWrapper = getWrapperFor(currentObj);
            }

            if (currentObj == null) {
                LOGGER.error("""
                        Object resolved from context provided to Resolvable is null!
                        Match: '{}', Provided object instance: '{}'
                        """, match, instance);
                throw new RuntimeException("Failed to get valid object from Wrapper! (See logger error above)");
            }

            replacement = replacement.replace(wholeMatch, currentObj.toString());
            matcher = pattern.matcher(replacement);
        }

        return replacement;
    }

    public <A> void providePermanent(String name, Wrapper<A> wrapper, A instance) {
        super.providePermanent(name, wrapper, instance);
        base = reshapeWith(base, name, wrapper, instance);
        resolved = base;
    }

    public <A> void provideEphemeral(String name, Wrapper<A> wrapper, A instance) {
        super.provideEphemeral(name, wrapper, instance);
        resolved = reshapeWith(resolved, name, wrapper, instance);
    }

    public T resolve() {
        T result = calculate();
        resolved = base;
        return result;
    }

    protected abstract T calculate();

    public static class StringResolvable extends VariabledResolvable<String> {
        public StringResolvable(String base) {
            super(base);
        }
        public StringResolvable(String base, String regex, int varGroup) {
            super(base, regex, varGroup);
        }

        @Override
        public String calculate() {
            return resolved;
        }
    }
    public static abstract class ExpressionResolvable<N> extends VariabledResolvable<EvaluationValue> {
        public ExpressionResolvable(String base) {
            super(base);
        }
        public ExpressionResolvable(String base, String regex, int varGroup) {
            super(base, regex, varGroup);
        }

        public N getValue() {
            return getValue(resolve());
        }

        protected abstract N getValue(EvaluationValue eval);

        @Override
        public EvaluationValue calculate() {
            try {
                return new Expression(resolved).evaluate();
            } catch (EvaluationException | ParseException e) {
                LOGGER.error("Failed to evaluate ExpressionResolvable! Perhaps an attempt at using non-existent context? Resolved string: '{}'", resolved);
                throw new RuntimeException(e);
            }
        }
    }
    public static class DoubleExpression extends ExpressionResolvable<Double> {
        public DoubleExpression(String base) {
            super(base);
        }
        public DoubleExpression(String base, String regex, int varGroup) {
            super(base, regex, varGroup);
        }

        @Override
        protected Double getValue(EvaluationValue eval) {
            return eval.getNumberValue().doubleValue();
        }
    }
    public static class FloatExpression extends ExpressionResolvable<Float> {
        public FloatExpression(String base) {
            super(base);
        }
        public FloatExpression(String base, String regex, int varGroup) {
            super(base, regex, varGroup);
        }

        @Override
        protected Float getValue(EvaluationValue eval) {
            return eval.getNumberValue().floatValue();
        }
    }
    public static class IntegerExpression extends ExpressionResolvable<Integer> {
        public IntegerExpression(String base) {
            super(base);
        }
        public IntegerExpression(String base, String regex, int varGroup) {
            super(base, regex, varGroup);
        }

        @Override
        protected Integer getValue(EvaluationValue eval) {
            return eval.getNumberValue().intValue();
        }
    }
    public static class BooleanExpression extends ExpressionResolvable<Boolean> {
        public BooleanExpression(String base) {
            super(base);
        }
        public BooleanExpression(String base, String regex, int varGroup) {
            super(base, regex, varGroup);
        }

        @Override
        protected Boolean getValue(EvaluationValue eval) {
            return eval.getBooleanValue();
        }
    }
}
