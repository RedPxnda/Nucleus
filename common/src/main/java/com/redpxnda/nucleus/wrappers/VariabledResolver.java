package com.redpxnda.nucleus.wrappers;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import com.mojang.logging.LogUtils;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolverCodec;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static com.redpxnda.nucleus.Nucleus.LOGGER;

@AutoCodec.Override("codecGetter")
public abstract class VariabledResolver<T> extends Resolver<T> {
    public static AutoCodec.CodecGetter<VariabledResolver> codecGetter = params -> {
        if (params != null && params.length == 1) {
            if (String.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forString);
            if (Double.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forDouble);
            if (Float.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forFloat);
            if (Boolean.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forBoolean);
            if (Integer.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forInteger);
        }
        return null;
    };

    /*public static class TestObject {
        public VariabledResolvable<String> str;
        public VariabledResolvable<Boolean> bool;
        public Resolvable<Double> doub;
        public Resolvable<Float> floa;
    }*/

    public static void main(String[] args) {
        Wrappers.init();

        Resolver<String> r = forString("$[var.$[var2]] and $[var.$[var3.$[var2]]]");
        r.providePermanent("var2", "a");
        r.providePermanent("var3", Map.of("a", "b", "b", "a"));
        r.providePermanent("var", Map.of("a", 1, "b", 3));
        System.out.println(r.resolve());
    }

    static final Logger LOGGER = LogUtils.getLogger();

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
    public static StringResolver forString(String expression) {
        return new StringResolver(expression);
    }

    protected String resolved;
    protected Function<String, String> regex = str -> "\\$\\[" + str + "(?:\\.([^\\[]*?))?\\]";
    protected int group = 1;

    /**
     * @param outputClass the class of the output
     * @param base        the expression string
     */
    public VariabledResolver(Class<T> outputClass, String base) {
        super(outputClass, base, false);
        this.base = base;
        this.resolved = base;
    }

    /**
     * @param regex    a function taking the target variable and providing a regex pattern
     *                 that matches every usage of that variable, with {@code varGroup}
     *                 being the group holding method calls to this object. Make sure your
     *                 regex pattern finds inners before outers so variable nesting works.
     * @param varGroup the regex match group holding method calls to this object
     */
    public VariabledResolver(Class<T> outputClass, String base, Function<String, String> regex, int varGroup) {
        this(outputClass, base);
        this.regex = regex;
        this.group = varGroup;
    }

    public Function<String, String> getRegexFunction() {
        return regex;
    }

    public void setRegexFunction(Function<String, String> regex) {
        this.regex = regex;
    }

    public int getRegexGroup() {
        return group;
    }

    public void setRegexGroup(int group) {
        this.group = group;
    }

    protected <A> String reshapeWith(String replacement, String name, Wrapper<A> wrapper, A instance) {
        Pattern pattern = Pattern.compile(regex.apply(name));
        Matcher matcher = pattern.matcher(base);

        while (matcher.find()) {
            String wholeMatch = matcher.group();
            String match = matcher.group(group);
            if (match == null) match = "";
            String[] parts = match.split("\\.");

            Object currentObj = instance;
            Wrapper<Object> currentWrapper = (Wrapper<Object>) wrapper;
            for (String part : parts) {
                if (part.isEmpty()) continue;
                if (currentWrapper == null)
                    throw new RuntimeException("Cannot get properties (specifically '" + part + "') from object '" + currentObj + "' as it has no specified wrapper!\n" +
                            "Variable attempted to use: " + match);
                currentObj = currentWrapper.invoke(currentObj, part);
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
            matcher.reset(replacement);
        }

        return replacement;
    }

    public <A> void providePermanent(String name, Wrapper<A> wrapper, A instance) {
        //super.providePermanent(name, wrapper, instance);
        base = reshapeWith(base, name, wrapper, instance);
        resolved = base;
    }

    public <A> void provideTemporary(String name, Wrapper<A> wrapper, A instance) {
        //super.provideTemporary(name, wrapper, instance);
        resolved = reshapeWith(resolved, name, wrapper, instance);
    }

    public T resolve() {
        T result = calculate();
        resolved = base;
        return result;
    }

    protected abstract T calculate();

    public static class StringResolver extends VariabledResolver<String> {
        public StringResolver(String base) {
            super(String.class, base);
        }
        public StringResolver(String base, Function<String, String> regex, int varGroup) {
            super(String.class, base, regex, varGroup);
        }

        @Override
        public String calculate() {
            return resolved;
        }
    }
    public static abstract class ExpressionResolver<N> extends VariabledResolver<N> {
        public ExpressionResolver(Class<N> cls, String base) {
            super(cls, base);
        }
        public ExpressionResolver(Class<N> cls, String base, Function<String, String> regex, int varGroup) {
            super(cls, base, regex, varGroup);
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
    public static class DoubleExpression extends ExpressionResolver<Double> {
        public DoubleExpression(String base) {
            super(Double.class, base);
        }
        public DoubleExpression(String base, Function<String, String> regex, int varGroup) {
            super(Double.class, base, regex, varGroup);
        }

        @Override
        protected Double calculate() {
            return getValue().getNumberValue().doubleValue();
        }
    }
    public static class FloatExpression extends ExpressionResolver<Float> {
        public FloatExpression(String base) {
            super(Float.class, base);
        }
        public FloatExpression(String base, Function<String, String> regex, int varGroup) {
            super(Float.class, base, regex, varGroup);
        }

        @Override
        protected Float calculate() {
            return getValue().getNumberValue().floatValue();
        }
    }
    public static class IntegerExpression extends ExpressionResolver<Integer> {
        public IntegerExpression(String base) {
            super(Integer.class, base);
        }
        public IntegerExpression(String base, Function<String, String> regex, int varGroup) {
            super(Integer.class, base, regex, varGroup);
        }

        @Override
        protected Integer calculate() {
            return getValue().getNumberValue().intValue();
        }
    }
    public static class BooleanExpression extends ExpressionResolver<Boolean> {
        public BooleanExpression(String base) {
            super(Boolean.class, base);
        }
        public BooleanExpression(String base, Function<String, String> regex, int varGroup) {
            super(Boolean.class, base, regex, varGroup);
        }

        @Override
        protected Boolean calculate() {
            return getValue().getBooleanValue();
        }
    }
}
