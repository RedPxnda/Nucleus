package com.redpxnda.nucleus.math.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import java.util.List;

@FunctionParameter(name = "condition")
@FunctionParameter(name = "first")
@FunctionParameter(name = "second")
public class Switch extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... parameterValues) {
        boolean condition = parameterValues[0].getBooleanValue();
        return condition ? parameterValues[1] : parameterValues[2];
    }
}
