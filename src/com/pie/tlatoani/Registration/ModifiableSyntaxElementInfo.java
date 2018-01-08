package com.pie.tlatoani.Registration;

import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SyntaxElementInfo;
import com.pie.tlatoani.Util.Reflection;

/**
 * Created by Tlatoani on 8/12/17.
 */
public abstract class ModifiableSyntaxElementInfo<I extends SyntaxElementInfo> {
    public static Reflection.FieldAccessor<String[]> PATTERNS_FIELD;

    public final I syntaxElementInfo;

    static {
        PATTERNS_FIELD = Reflection.getField(SyntaxElementInfo.class, "patterns", String[].class);
    }

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
            super(new SyntaxElementInfo<>(patterns, effectClass));
        }

        @Override
        public void register() {
            ReflectiveRegistration.registerEffect(syntaxElementInfo);
        }
    }

    public static class Condition<C extends ch.njol.skript.lang.Condition> extends ModifiableSyntaxElementInfo<SyntaxElementInfo<C>> {

        public Condition(Class<C> conditionClass, String... patterns) {
            super(new SyntaxElementInfo<C>(patterns, conditionClass));
        }

        @Override
        public void register() {
            ReflectiveRegistration.registerCondition(syntaxElementInfo);
        }
    }

    public static class Expression<E extends ch.njol.skript.lang.Expression<T>, T> extends ModifiableSyntaxElementInfo<ExpressionInfo<E, T>> {
        public final ExpressionType expressionType;

        public Expression(Class<E> expressionClass, Class<T> returnType, ExpressionType expressionType, String... patterns) {
            super(new ExpressionInfo<>(patterns, returnType, expressionClass));
            this.expressionType = expressionType;
        }

        @Override
        public void register() {
            ReflectiveRegistration.registerExpression(syntaxElementInfo, expressionType);
        }
    }
}
