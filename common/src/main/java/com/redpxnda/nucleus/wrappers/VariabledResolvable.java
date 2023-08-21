package com.redpxnda.nucleus.wrappers;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolvableCodec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

@AutoCodec.Override("codecGetter")
public abstract class VariabledResolvable<T> extends Resolvable<T> {
    public static AutoCodec.CodecGetter<VariabledResolvable> codecGetter = params -> {
        if (params != null && params.length == 1) {
            if (String.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forString);
            if (Double.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forDouble);
            if (Float.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forFloat);
            if (Boolean.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forBoolean);
            if (Integer.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forInteger);
        }
        return null;
    };

    /*public static class TestObject {
        public VariabledResolvable<String> str;
        public VariabledResolvable<Boolean> bool;
        public Resolvable<Double> doub;
        public Resolvable<Float> floa;
    }

    public static void main(String[] args) {
        Wrappers.init();

        Gson gson = new Gson();
        JsonElement element = gson.fromJson("""
                {
                    "str": "abcd efghi $[var] and 82",
                    "bool": "$[var] > 6",
                    "doub": "$[var] * 3",
                    "floa": "$[var] * 3.4"
                }
                """, JsonElement.class);
        TestObject obj = AutoCodec.of(TestObject.class).codec().parse(JsonOps.INSTANCE, element).getOrThrow(false, s -> System.out.println(s));
        System.out.println(obj);
        System.out.println("=====\nbool:");
        obj.bool.provideTemporary("var", 7);
        System.out.println(obj.bool.resolve());
        System.out.println("=====\nstr:");
        obj.str.provideTemporary("var", "STRING WORKS");
        System.out.println(obj.str.resolve());
        System.out.println("=====\ndouble:");
        obj.doub.provideTemporary("var", 4);
        System.out.println(obj.doub.resolve());
        System.out.println("=====\nfloat:");
        obj.floa.provideTemporary("var", 1);
        System.out.println(obj.floa.resolve());
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

    public <A> void provideTemporary(String name, Wrapper<A> wrapper, A instance) {
        super.provideTemporary(name, wrapper, instance);
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
    public static abstract class ExpressionResolvable<N> extends VariabledResolvable<N> {
        public ExpressionResolvable(String base) {
            super(base);
        }
        public ExpressionResolvable(String base, String regex, int varGroup) {
            super(base, regex, varGroup);
        }

        protected EvaluationValue getValue() {
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
        protected Double calculate() {
            return getValue().getNumberValue().doubleValue();
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
        protected Float calculate() {
            return getValue().getNumberValue().floatValue();
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
        protected Integer calculate() {
            return getValue().getNumberValue().intValue();
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
        protected Boolean calculate() {
            return getValue().getBooleanValue();
        }
    }
}
