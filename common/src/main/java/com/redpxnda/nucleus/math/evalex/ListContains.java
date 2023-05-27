package com.redpxnda.nucleus.math.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import java.util.List;

@FunctionParameter(name = "list")
@FunctionParameter(name = "value")
public class ListContains extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... parameterValues) throws EvaluationException {
        List<EvaluationValue> list = parameterValues[0].getArrayValue();
        EvaluationValue value = parameterValues[1];

        return new EvaluationValue(list.contains(value));
    }
}
