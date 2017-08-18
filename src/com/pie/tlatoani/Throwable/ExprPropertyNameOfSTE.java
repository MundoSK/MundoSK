package com.pie.tlatoani.Throwable;

import com.pie.tlatoani.Util.MundoPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprPropertyNameOfSTE extends MundoPropertyExpression<StackTraceElement, String> {
    @Override
    public String convert(StackTraceElement stackTraceElement) {
        switch (property) {
            case "class name": return stackTraceElement.getClassName();
            case "file name": return stackTraceElement.getFileName();
            case "method name": return stackTraceElement.getMethodName();
        };
        throw new IllegalStateException("Invalid property: " + property);
    }
}
