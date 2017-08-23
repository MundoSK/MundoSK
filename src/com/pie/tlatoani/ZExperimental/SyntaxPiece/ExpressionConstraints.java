package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.google.common.collect.ImmutableSet;
import com.pie.tlatoani.Util.MundoUtil;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by Tlatoani on 4/4/17.
 */
public final class ExpressionConstraints {
    public final ImmutableSet<Type> types;
    public final Kleenean isLiteral;
    public final int time;
    public final boolean nullable;

    public final String syntax;

    public static class Type {
        public final ClassInfo classInfo;
        public final boolean isSingle;

        public Type(ClassInfo classInfo, boolean isSingle) {
            this.classInfo = classInfo;
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
    }

    public ExpressionConstraints(ImmutableSet<Type> types, Kleenean isLiteral, int time, boolean nullable) {
        this.types = types;
        this.isLiteral = isLiteral;
        this.time = time;
        this.nullable = nullable;

        String typeOptions = types.stream().map(type -> type.classInfo.getCodeName()).collect(Collectors.joining("/"));
        String isLiteralPrefix = getLiteralityPrefix(isLiteral);
        String timeSuffix = getTimeSuffix(time);
        String nullablePrefix = nullable ? "-" : "";

        this.syntax = isLiteralPrefix + nullablePrefix + typeOptions + timeSuffix;
    }

    public ExpressionConstraints(String string) {
        Kleenean isLiteral;
        if (string.charAt(0) == '*') {
            isLiteral = Kleenean.TRUE;
            string = string.substring(1);
        } else if (string.charAt(0) == '~') {
            isLiteral = Kleenean.FALSE;
            string = string.substring(1);
        } else {
            isLiteral = Kleenean.UNKNOWN;
        }
        boolean nullable;
        if (string.charAt(0) == '-') {
            nullable = true;
            string = string.substring(0);
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
            ClassInfo classInfo = Classes.getClassInfo(typeStr);
            builder.add(new Type(classInfo, isSingle));
        }

        this.types = builder.build();
        this.isLiteral = isLiteral;
        this.time = time;
        this.nullable = nullable;

        this.syntax = string;
    }

    public Class getSuperClass() {
        Class[] classes = new Class[types.size()];
        int i = 0;
        for (Type type : types) {
            classes[i] = type.classInfo.getC();
            i++;
        }
        return MundoUtil.commonSuperClass(classes);
    }

    public static String getLiteralityPrefix(Kleenean isLiteral) {
        switch (isLiteral) {
            case TRUE: return "*";
            case FALSE: return "~";
            case UNKNOWN: return "";
        }
        throw new IllegalArgumentException("Illegal Kleenean isLiteral value: " + isLiteral);
    }

    public static String getTimeSuffix(int time) {
        switch (time) {
            case 1: return "@1";
            case -1: return "@-1";
            case 0: return "0";
        }
        throw new IllegalArgumentException("Illegal int time value: " + time);
    }

    public static class Collective {
        private HashMap<String, ExpressionConstraints> constraintsMap = new HashMap<>();

        public void addConstraints(String variable, ExpressionConstraints constraints) {
            ExpressionConstraints prevConstraints = constraintsMap.get(variable);
            if (prevConstraints != null) {
                ImmutableSet.Builder builder = ImmutableSet.builder();
                builder.addAll(prevConstraints.types);
                builder.addAll(constraints.types);
                Kleenean isLiteral = constraints.isLiteral == prevConstraints.isLiteral ? constraints.isLiteral : Kleenean.UNKNOWN;
                int time = constraints.time == prevConstraints.time ? constraints.time : 0;
                boolean nullable = constraints.nullable || prevConstraints.nullable;
                ExpressionConstraints newConstraints = new ExpressionConstraints(builder.build(), isLiteral, time, nullable);
                constraintsMap.put(variable, newConstraints);
            } else {
                constraintsMap.put(variable, constraints);
            }
        }
    }
}
