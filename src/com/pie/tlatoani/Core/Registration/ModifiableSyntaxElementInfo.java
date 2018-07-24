package com.pie.tlatoani.Core.Registration;

import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SyntaxElementInfo;
import com.pie.tlatoani.Core.Static.Reflection;

/**
 * Created by Tlatoani on 8/12/17.
 */
public abstract class ModifiableSyntaxElementInfo<I extends SyntaxElementInfo> {
    public static final Reflection.FieldAccessor<String[]> PATTERNS_FIELD
            = Reflection.getField(SyntaxElementInfo.class, "patterns", String[].class);
    public static final Reflection.ConstructorInvoker SYNTAX_ELEMENT_INFO_INIT;
    public static final Reflection.ConstructorInvoker EXPRESSION_INFO_INIT;
    public static final boolean usesNewConstructor;

    static {
        Reflection.ConstructorInvoker syntaxElementInfoInit = null;
        try {
            syntaxElementInfoInit = Reflection.getConstructor(SyntaxElementInfo.class, String[].class, Class.class);
        } catch (IllegalStateException e) {}
        if (syntaxElementInfoInit == null) {
            SYNTAX_ELEMENT_INFO_INIT = Reflection.getConstructor(SyntaxElementInfo.class, String[].class, Class.class, String.class);
            EXPRESSION_INFO_INIT = Reflection.getConstructor(ExpressionInfo.class, String[].class, Class.class, Class.class, String.class);
            usesNewConstructor = true;
        } else {
            SYNTAX_ELEMENT_INFO_INIT = syntaxElementInfoInit;
            EXPRESSION_INFO_INIT = Reflection.getConstructor(ExpressionInfo.class, String[].class, Class.class, Class.class);
            usesNewConstructor = false;
        }
    }


    public final I syntaxElementInfo;

    private ModifiableSyntaxElementInfo(I syntaxElementInfo) {
        this.syntaxElementInfo = syntaxElementInfo;
    }

    public String[] getPatterns() {
        return syntaxElementInfo.patterns;
    }

    public void setPatterns(String... patterns) {
        if (syntaxElementInfo.patterns.length == patterns.length) {
            for (int i = 0; i < patterns.length; i++) {
                syntaxElementInfo.patterns[i] = patterns[i];
            }
        } else {
            PATTERNS_FIELD.set(syntaxElementInfo, patterns);
        }
    }

    public static SyntaxElementInfo createSyntaxElementInfo(String[] patterns, Class c) {
        if (usesNewConstructor) {
            return (SyntaxElementInfo) SYNTAX_ELEMENT_INFO_INIT.invoke(patterns, c, "MUNDOSK_MODIFIABLE_SYNTAX_ELEMENT_INFO");
        } else {
            return (SyntaxElementInfo) SYNTAX_ELEMENT_INFO_INIT.invoke(patterns, c);
        }
    }

    public static ExpressionInfo createExpressionInfo(String[] patterns, Class returnType, Class expressionClass) {
        if (usesNewConstructor) {
            return (ExpressionInfo) EXPRESSION_INFO_INIT.invoke(patterns, returnType, expressionClass, "MUNDOSK_MODIFIABLE_EXPRESSION_INFO");
        } else {
            return (ExpressionInfo) EXPRESSION_INFO_INIT.invoke(patterns, returnType, expressionClass);
        }
    }

    public void addPattern(String pattern) {
        String[] newPatterns = new String[getPatterns().length + 1];
        System.arraycopy(getPatterns(), 0, newPatterns, 0, getPatterns().length);
        newPatterns[getPatterns().length] = pattern;
        setPatterns(newPatterns);
    }

    public void addPatterns(String... patterns) {
        String[] newPatterns = new String[getPatterns().length + patterns.length];
        System.arraycopy(getPatterns(), 0, newPatterns, 0, getPatterns().length);
        System.arraycopy(patterns, 0, newPatterns, getPatterns().length, patterns.length);
        setPatterns(newPatterns);
    }

    public abstract void register();

    public static class Effect<E extends ch.njol.skript.lang.Effect> extends ModifiableSyntaxElementInfo<SyntaxElementInfo<E>> {

        public Effect(Class<E> effectClass, String... patterns) {
            super(createSyntaxElementInfo(patterns, effectClass));
        }

        @Override
        public void register() {
            ReflectiveRegistration.registerEffect(syntaxElementInfo);
        }
    }

    public static class Condition<C extends ch.njol.skript.lang.Condition> extends ModifiableSyntaxElementInfo<SyntaxElementInfo<C>> {

        public Condition(Class<C> conditionClass, String... patterns) {
            super(createSyntaxElementInfo(patterns, conditionClass));
        }

        @Override
        public void register() {
            ReflectiveRegistration.registerCondition(syntaxElementInfo);
        }
    }

    public static class Expression<E extends ch.njol.skript.lang.Expression<T>, T> extends ModifiableSyntaxElementInfo<ExpressionInfo<E, T>> {
        public final ExpressionType expressionType;

        public Expression(Class<E> expressionClass, Class<T> returnType, ExpressionType expressionType, String... patterns) {
            super(createExpressionInfo(patterns, returnType, expressionClass));
            this.expressionType = expressionType;
        }

        @Override
        public void register() {
            ReflectiveRegistration.registerExpression(syntaxElementInfo, expressionType);
        }
    }
}
