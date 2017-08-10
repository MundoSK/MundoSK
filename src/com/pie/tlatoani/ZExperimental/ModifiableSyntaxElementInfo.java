package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.SyntaxElementInfo;
import com.pie.tlatoani.Util.Reflection;

/**
 * Created by Tlatoani on 5/12/17.
 */
public class ModifiableSyntaxElementInfo<E extends SyntaxElement> extends SyntaxElementInfo<E> {
    public static Reflection.FieldAccessor<String[]> PATTERNS_FIELD;

    static {
        PATTERNS_FIELD = Reflection.getField(SyntaxElementInfo.class, "patterns", String[].class);
    }

    public ModifiableSyntaxElementInfo(String[] patterns, Class<E> c) throws IllegalArgumentException {
        super(patterns, c);
    }

    public void setPatterns(String... patterns) {
        if (this.patterns.length == patterns.length) {
            for (int i = 0; i < patterns.length; i++) {
                this.patterns[i] = patterns[i];
            }
        } else {
            PATTERNS_FIELD.set(this, patterns);
        }
    }
}
