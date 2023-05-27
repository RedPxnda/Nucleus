package com.redpxnda.nucleus.datapack.json.types;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;

import java.util.Map;

public abstract class AbstractEvaluable {
    protected final String raw;

    public AbstractEvaluable(String str) {
        raw = str;
    }

    public abstract EvaluationValue evaluate() throws EvaluationException, ParseException;
    public abstract EvaluationValue evaluate(Map<String, Object> values) throws EvaluationException, ParseException;
    public abstract EvaluationValue evaluate(Map<String, Object>... values) throws EvaluationException, ParseException;

    public String getRaw() {
        return raw;
    }
}
