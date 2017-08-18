package com.pie.tlatoani.Throwable;

import com.pie.tlatoani.Util.MundoPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprLineNumberOfSTE extends MundoPropertyExpression<StackTraceElement, Number> {
    @Override
    public Number convert(StackTraceElement stackTraceElement) {
        return stackTraceElement.getLineNumber();
    }
}
