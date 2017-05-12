package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionInfo;

/**
 * Created by Tlatoani on 5/12/17.
 */
public class ModifiableExpressionInfo<E extends Expression<T>, T> extends ExpressionInfo<E, T> {

    public ModifiableExpressionInfo(String[] patterns, Class<T> returnType, Class<E> c) throws IllegalArgumentException {
        super(patterns, returnType, c);
    }

    public void setPatterns(String... patterns) {
        if (this.patterns.length == patterns.length) {
            for (int i = 0; i < patterns.length; i++) {
                this.patterns[i] = patterns[i];
            }
        } else {
            ModifiableSyntaxElementInfo.PATTERNS_FIELD.set(this, patterns);
        }
    }
}
