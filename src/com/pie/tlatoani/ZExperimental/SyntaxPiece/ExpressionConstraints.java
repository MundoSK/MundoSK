package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.google.common.collect.ImmutableSet;

import java.util.stream.Collectors;

/**
 * Created by Tlatoani on 4/4/17.
 */
public final class ExpressionConstraints {
    public final ImmutableSet<Type> types;
    public final LiteralState isLiteral;
    public final int time;
    public final boolean nullable;

    public enum LiteralState {
        LITERAL("*"),
        NONLITERAL("~"),
        UNKNOWN("");

        public final String prefix;

        LiteralState(String prefix) {
            this.prefix = prefix;
        }

        public LiteralState getByPrefix(String prefix) {
            for (LiteralState literalState : values()) {
                if (literalState.prefix.equals(prefix)) {
                    return literalState;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public static class Type {
        public final ClassInfo classInfo;
        public final String codename;
        public final boolean isSingle;

        public Type(String codename, boolean isSingle) {
            this.codename = codename;
            this.classInfo = Classes.getClassInfo(codename);
            this.isSingle = isSingle;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Type) {
                Type type = (Type) object;
                return isSingle == type.isSingle && classInfo.equals(type.classInfo);
            }
            return false;
        }

        @Override
        public String toString() {
            return "Type(classInfo = " + codename + ", isSingle = " + isSingle + ")";
        }
    }

    public ExpressionConstraints(ImmutableSet<Type> types, LiteralState isLiteral, int time, boolean nullable) {
        this.types = types;
        this.isLiteral = isLiteral;
        this.time = time;
        this.nullable = nullable;
    }

    public static ExpressionConstraints fromSyntax(String string) {
        LiteralState isLiteral;
        if (string.charAt(0) == '*') {
            isLiteral = LiteralState.LITERAL;
            string = string.substring(1);
        } else if (string.charAt(0) == '~') {
            isLiteral = LiteralState.NONLITERAL;
            string = string.substring(1);
        } else {
            isLiteral = LiteralState.UNKNOWN;
        }
        boolean nullable;
        if (string.charAt(0) == '-') {
            nullable = true;
            string = string.substring(1);
        } else {
            nullable = false;
        }
        int time;
        if (string.endsWith("@1")) {
            time = 1;
            string = string.substring(0, string.length() - 2);
        } else if (string.endsWith("@-1")) {
            time = -1;
            string = string.substring(0, string.length() - 2);
        } else {
            time = 0;
        }
        ImmutableSet.Builder<Type> builder = ImmutableSet.builder();
        for (String typeStr : string.split("/")) {
            boolean isSingle;
            if (typeStr.endsWith("s")) {
                isSingle = false;
                typeStr = typeStr.substring(0, typeStr.length() - 1);
            } else {
                isSingle = true;
            }
            builder.add(new Type(typeStr, isSingle));
        }
        return new ExpressionConstraints(builder.build(), isLiteral, time, nullable);
    }

    public String getTypeOptions() {
        return types.stream().map(type -> type.codename).collect(Collectors.joining("/"));
    }

    public String getSyntax() {
        String timeSuffix = getTimeSuffix(time);
        String nullablePrefix = nullable ? "-" : "";
        return isLiteral.prefix + nullablePrefix + getTypeOptions() + timeSuffix;
    }
    
    public String toString() {
        return "ExpressionConstraints(types = " + types + ", isLiteral = " + isLiteral + ", time = " + time + ", nullable = " + nullable + ")";
    }

    /*public Class getSuperClass() {
        Class[] classes = new Class[types.size()];
        int i = 0;
        for (Type type : types) {
            classes[i] = type.classInfo;
            i++;
        }
        return Utilities.commonSuperClass(classes);
    }*/

    public static String getTimeSuffix(int time) {
        switch (time) {
            case 1: return "@1";
            case -1: return "@-1";
            case 0: return "";
        }
        throw new IllegalArgumentException("Illegal int time value: " + time);
    }

}
