package com.redpxnda.nucleus.resolving;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;

import java.util.function.Function;

public abstract class ExpressionResolver<N> extends Resolver<N> {
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
