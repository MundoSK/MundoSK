package com.pie.tlatoani.Test;

import ch.njol.skript.lang.Expression;
import com.pie.tlatoani.Mundo;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Tlatoani on 1/1/17.
 */
public class LambdaRegistration {

    public static void registerEffect(Object effect, String... patterns) {
        Class methodOwner = effect.getClass();
        Method consumeMethod = null;
        for (Method method : methodOwner.getDeclaredMethods()) {
            if (method.getName().equals("consume")) {
                consumeMethod = method;
                break;
            }
        }
        if (consumeMethod == null) {
            throw new IllegalArgumentException("The effect object " + effect + " does not provide a 'consume' method.");
        }
    }

    public static class MethodWrapper {
        private final Method method;
        private final Expression[] expressions;
        private final boolean[] areSingle;
        public final Class returnType;

        public MethodWrapper(Method method, Expression[] expressions) {
            this.method = method;
            this.expressions = expressions;
            this.returnType = method.getReturnType();
            this.areSingle = new boolean[expressions.length];
            Class[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != expressions.length) {
                throw new IllegalArgumentException("paramTypes.length != expressions.length");
            }
            for (int i = 0; i < expressions.length; i++) {
                if (expressions[i] == null) continue;
                Class paramType = paramTypes[i];
                if (paramType.toString().contains("[")) {
                    areSingle[i] = false;
                    try {
                        paramType = Class.forName(paramType.getName().substring(2, paramType.getName().length() - 1));
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    }
                } else {
                    if (!expressions[i].isSingle()) {
                        throw new IllegalArgumentException("The expression at index " + i + " should be single, but isn't.");
                    }
                    areSingle[i] = true;
                }
                if (!Mundo.classesCompatible(expressions[i].getReturnType(), paramType)) {
                    throw new IllegalArgumentException("Types at index " + i + " not compatible: " + expressions[i].getReturnType() + ", " + paramTypes[i]);
                }
            }
        }

    }
}
