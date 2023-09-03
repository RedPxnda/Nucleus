package com.redpxnda.nucleus.resolving;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolverCodec;
import com.redpxnda.nucleus.resolving.wrappers.Wrapper;
import com.redpxnda.nucleus.resolving.wrappers.WrapperHolder;
import com.redpxnda.nucleus.resolving.wrappers.Wrappers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static com.redpxnda.nucleus.Nucleus.LOGGER;

@AutoCodec.Override("codecGetter")
public abstract class Resolver<T> {
    public static AutoCodec.CodecGetter<Resolver> codecGetter = params -> {
        Class<?> cls;
        if (params != null && params.length == 1) {
            if (String.class.equals(params[0])) return (Codec) StringResolver.codec;
            if (Double.class.equals(params[0])) return (Codec) DoubleExpression.codec;
            if (Float.class.equals(params[0])) return (Codec) FloatExpression.codec;
            if (Boolean.class.equals(params[0])) return (Codec) BooleanExpression.codec;
            if (Integer.class.equals(params[0])) return (Codec) IntegerExpression.codec;
            cls = params[0] instanceof Class<?> clz ? clz : Object.class;
        } else cls = Object.class;
        return new ResolverCodec<>(str -> new DirectResolver<>(cls, str));
    };

    /*public static class TestObject {
        public VariabledResolvable<String> str;
        public VariabledResolvable<Boolean> bool;
        public Resolvable<Double> doub;
        public Resolvable<Float> floa;
    }*/

//    public static void main(String[] args) {
//        Wrappers.init();
//
//        Resolver<Integer> r = new DirectResolver<>(Integer.class, "pos.x");
//        r.providePermanent("pos", new BlockPos(5, 10, 15));
//        System.out.println(r.resolved);
//        System.out.println(r.resolve());
//    }

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

    protected @Nullable T definiteAnswer = null;
    protected String base;
    protected String resolved;
    protected final Class<T> clazz;
    protected final Map<String, WrapperHolder<?>> permanentContexts = new HashMap<>();
    protected final Map<String, WrapperHolder<?>> temporaryContexts = new HashMap<>();
    protected Function<String, String> regex = str -> "\\$\\[" + str + "(?:\\.([^\\[]*?))?\\]";
    protected int group = 1;

    /**
     * @param outputClass the class of the output
     * @param base        the expression string
     */
    public Resolver(Class<T> outputClass, String base) {
        this.clazz = outputClass;
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
    public Resolver(Class<T> outputClass, String base, Function<String, String> regex, int varGroup) {
        this(outputClass, base);
        this.regex = regex;
        this.group = varGroup;
    }

    protected <A> Wrapper<A> getWrapperFor(A instance) {
        return Wrappers.getWrapperFor(instance);
    }

    public Function<String, String> getRegexFunction() {
        return regex;
    }

    public void setDefiniteAnswer(@NotNull T definiteAnswer) {
        this.definiteAnswer = definiteAnswer;
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

    public String getBase() {
        return base;
    }

    public String getResolved() {
        return resolved;
    }

    public void resetToBase() {
        temporaryContexts.clear();
        temporaryContexts.putAll(permanentContexts);
        resolved = base;
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
    protected <A> String reshapeResolvedWith(String replacement, String name, Wrapper<A> wrapper, A instance) {
        return replacement;
    }

    public <A> void providePermanent(String name, Wrapper<A> wrapper, A instance) {
        if (definiteAnswer != null) return;

        WrapperHolder<A> holder = new WrapperHolder<>(wrapper, instance);
        permanentContexts.put(name, holder);
        temporaryContexts.put(name, holder);

        base = reshapeWith(base, name, wrapper, instance);
        resolved = reshapeResolvedWith(base, name, wrapper, instance);
    }
    public <A> void providePermanent(String name, A instance) {
        providePermanent(name, getWrapperFor(instance), instance);
    }
    public void providePermanent(String name, Number instance) {
        providePermanent(name, null, instance);
    }
    public void providePermanent(String name, boolean instance) {
        providePermanent(name, null, instance);
    }
    public void providePermanent(String name, String instance) {
        providePermanent(name, null, instance);
    }

    public <A> void provideTemporary(String name, Wrapper<A> wrapper, A instance) {
        if (definiteAnswer != null) return;

        WrapperHolder<A> holder = new WrapperHolder<>(wrapper, instance);
        temporaryContexts.put(name, holder);

        resolved = reshapeWith(resolved, name, wrapper, instance);
    }
    public <A> void provideTemporary(String name, A instance) {
        provideTemporary(name, getWrapperFor(instance), instance);
    }
    public void provideTemporary(String name, Number instance) {
        provideTemporary(name, null, instance);
    }
    public void provideTemporary(String name, boolean instance) {
        provideTemporary(name, null, instance);
    }
    public void provideTemporary(String name, String instance) {
        provideTemporary(name, null, instance);
    }

    public T resolve() {
        if (definiteAnswer != null) return definiteAnswer;

        T result = calculate();
        resetToBase();
        return result;
    }

    protected abstract T calculate();

}
